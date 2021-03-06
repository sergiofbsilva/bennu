/*
 * Authenticate.java
 * 
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 * 
 * This file is part of bennu-core.
 * 
 * bennu-core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * bennu-core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with bennu-core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.core.security;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.exceptions.AuthorizationException;
import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.domain.groups.DynamicGroup;
import org.fenixedu.bennu.core.domain.groups.Group;
import org.fenixedu.bennu.core.domain.groups.UserGroup;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.core.util.TransactionalThread;
import org.fenixedu.commons.i18n.I18N;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class Authenticate {
    private static final Logger logger = LoggerFactory.getLogger(Authenticate.class);

    private static final String USER_SESSION_ATTRIBUTE = "lzsWdyqswo";

    public static final String LOGGED_USER_ATTRIBUTE = "LOGGED_USER_ATTRIBUTE";

    private static final InheritableThreadLocal<UserSession> wrapper = new InheritableThreadLocal<>();

    private static Set<AuthenticationListener> authenticationListeners;

    /**
     * Login user with the specified username, intended for use with an external user authentication mechanism, local password is
     * ignored. For password based authentication use: {@link #login(HttpSession, String, String)}.
     * 
     * @param session session on with to log the user
     * @param username username of the user to login
     * @return user user logged in.
     */
    public static User login(HttpSession session, String username) {
        return internalLogin(session, username, null, false);
    }

    /**
     * Login user with the specified username and password, intended for password based authentication. For external
     * authentication mechanisms use: {@link #login(HttpSession, String)}.
     * 
     * @param session session on with to log the user
     * @param username username of the user to login
     * @param password password of the user to login
     * @return user user logged in.
     */
    public static User login(HttpSession session, String username, String password) {
        return internalLogin(session, username, password, true);
    }

    private static User internalLogin(HttpSession session, String username, String password, boolean checkPassword) {
        User user = User.findByUsername(username);
        if (checkPassword && !CoreConfiguration.getConfiguration().developmentMode()) {
            if (user == null || !user.matchesPassword(password)) {
                throw AuthorizationException.authenticationFailed();
            }
        }
        if (user == null) {
            if (CoreConfiguration.casConfig().isCasEnabled() || Bennu.getInstance().getUserSet().isEmpty()) {
                user = attemptBootstrapUser(username);
            } else {
                throw AuthorizationException.authenticationFailed();
            }
        }
        if (user.isLoginExpired()) {
            throw AuthorizationException.authenticationFailed();
        }

        UserSession userWrapper = new UserSession(user);
        wrapper.set(userWrapper);
        session.setAttribute(USER_SESSION_ATTRIBUTE, userWrapper);
        session.setAttribute(LOGGED_USER_ATTRIBUTE, user);
        final Locale preferredLocale = user.getPreferredLocale();
        if (preferredLocale != null) {
            I18N.setLocale(session, preferredLocale);
        }

        fireLoginListeners(user);
        logger.debug("Logged in user: " + user.getUsername());

        return user;
    }

    @Atomic(mode = TxMode.WRITE)
    private static User attemptBootstrapUser(String username) {
        try {
            User user = User.findByUsername(username);
            if (user != null) {
                return user;
            }
            if (CoreConfiguration.casConfig().isCasEnabled() || Bennu.getInstance().getUserSet().isEmpty()) {
                user = new User(username);
                try {
                    DynamicGroup.getInstance("managers");
                    // Managers groups already initialized.
                } catch (BennuCoreDomainException e) {
                    wrapper.set(new UserSession(user));
                    DynamicGroup.initialize("managers", UserGroup.getInstance(user));
                    logger.info("Bootstrapped #managers group to user: " + user.getUsername());
                }
                return user;
            }
            throw AuthorizationException.authenticationFailed();
        } finally {
            wrapper.set(null);
        }
    }

    public static void logout(HttpSession session) {
        if (session != null) {
            final UserSession userWrapper = (UserSession) session.getAttribute(USER_SESSION_ATTRIBUTE);
            if (userWrapper != null) {
                userWrapper.getUser().markLogoutTime();
            }
            session.removeAttribute(USER_SESSION_ATTRIBUTE);
            session.invalidate();
        }
        wrapper.set(null);
    }

    public static void mock(User user) {
        wrapper.set(new UserSession(user));
    }

    public static void unmock() {
        wrapper.set(null);
    }

    public static User getUser() {
        return wrapper.get() != null ? wrapper.get().getUser() : null;
    }

    public static Set<Group> accessibleGroups() {
        return wrapper.get() != null ? wrapper.get().accessibleGroups() : Collections.<Group> emptySet();
    }

    public static boolean isLogged() {
        return wrapper.get() != null;
    }

    static void updateFromSession(HttpSession session) {
        UserSession user = (UserSession) (session == null ? null : session.getAttribute(USER_SESSION_ATTRIBUTE));
        if (user != null && user.isSessionStillValid()) {
            wrapper.set(user);
        } else {
            wrapper.set(null);
        }
    }

    static void clear() {
        wrapper.set(null);
    }

    public static void addAuthenticationListener(AuthenticationListener listener) {
        if (authenticationListeners == null) {
            authenticationListeners = new HashSet<>();
        }
        authenticationListeners.add(listener);
    }

    public static void removeAuthenticationListener(AuthenticationListener listener) {
        if (authenticationListeners != null) {
            authenticationListeners.remove(listener);
        }
    }

    private static void fireLoginListeners(final User user) {
        if (authenticationListeners != null) {
            for (final AuthenticationListener listener : authenticationListeners) {
                final TransactionalThread thread = new TransactionalThread() {
                    @Override
                    public void transactionalRun() {
                        listener.afterLogin(user);
                    }
                };
                thread.start();
            }
        }
    }
}
