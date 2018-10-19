package org.fenixedu.bennu.core.security;

import java.util.List;

import org.fenixedu.bennu.core.domain.User;

/***
 *
 * Implement this interface to prevent users from logging in.
 * A list of messages should be return by the {@link LoginBlocker#getLoginBlockers(User)} method so the user is informed
 * how to proceed in order to unblock the login.
 *
 * Created by Sérgio Silva (hello@fenixedu.org).
 */
public interface LoginBlocker {

    List<String> getLoginBlockers(User user);

}