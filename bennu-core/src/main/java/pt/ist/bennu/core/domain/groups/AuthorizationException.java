/*
 * AuthorizationException.java
 *
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 *
 * This file is part of bennu-core.
 *
 * bennu-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * bennu-core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with bennu-core.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.bennu.core.domain.groups;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.exceptions.BennuCoreDomainException;

/**
 * Group access authorization exception.
 */
public class AuthorizationException extends BennuCoreDomainException {
    private AuthorizationException(String key, String... args) {
        super(key, args);
    }

    private AuthorizationException(Throwable cause, String key, String... args) {
        super(cause, key, args);
    }

    public static AuthorizationException unauthorized(PersistentGroup group, User user) {
        return new AuthorizationException("error.bennu.group.unauthorized", user.getUsername(), group.getPresentationName());
    }

    public static AuthorizationException badAccessGroupConfiguration() {
        return new AuthorizationException("error.bennu.group.badaccessgroupconfiguration");
    }
}