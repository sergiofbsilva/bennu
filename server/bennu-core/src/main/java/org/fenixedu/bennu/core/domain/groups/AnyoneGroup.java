/*
 * AnyoneGroup.java
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
package org.fenixedu.bennu.core.domain.groups;

import java.util.Collections;
import java.util.Set;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.joda.time.DateTime;

import com.google.common.base.Supplier;

/**
 * Group that always returns <code>true</code> on membership tests.
 * 
 * @see Group
 */
public final class AnyoneGroup extends AnyoneGroup_Base {
    protected AnyoneGroup() {
        super();
    }

    @Override
    public String getPresentationName() {
        return BundleUtil.getString("resources.BennuResources", "label.bennu.group.anyone");
    }

    @Override
    public String expression() {
        return "anyone";
    }

    @Override
    public Set<User> getMembers() {
        return Collections.unmodifiableSet(Bennu.getInstance().getUserSet());
    }

    @Override
    public boolean isMember(final User user) {
        return true;
    }

    @Override
    public Set<User> getMembers(DateTime when) {
        return getMembers();
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        return isMember(user);
    }

    @Override
    public Group not() {
        return NobodyGroup.getInstance();
    }

    @Override
    protected boolean isGarbageCollectable() {
        // Singleton group, no point in delete
        return false;
    }

    /**
     * Get or create singleton instance of {@link AnyoneGroup}
     * 
     * @return singleton {@link AnyoneGroup} instance
     */
    public static AnyoneGroup getInstance() {
        return select(AnyoneGroup.class, new Supplier<AnyoneGroup>() {
            @Override
            public AnyoneGroup get() {
                return new AnyoneGroup();
            }
        });
    }
}
