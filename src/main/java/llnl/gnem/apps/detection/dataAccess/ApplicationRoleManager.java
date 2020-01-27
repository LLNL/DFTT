/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.apps.detection.dataAccess;

import llnl.gnem.core.database.Role;
import llnl.gnem.core.database.RoleManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class ApplicationRoleManager implements RoleManager {

    @Override
    public boolean hasRoles() {
        return true;
    }

    @Override
    public void addAvailableRoles(Collection<Role> roles) {
        availableRoles.addAll(roles);
    }

    private final Collection<Role> availableRoles;

    public ApplicationRoleManager() {
        availableRoles = new ArrayList<>();
    }


    /**
     * Set role for this application
     * @param conn
     */
    @Override
    public void setRoles(Connection conn) {

        try {
            setRole(conn);
        } catch (SQLException ignore) {
            // Nothing to do here
        }

    }

    private void setRole(Connection conn) throws SQLException {
    }


    @Override
    public boolean isRunnable() {
        return true;
    }

    @Override
    public boolean isRoleAvailable(Role role) {
        return availableRoles.contains(role);
    }

    @Override
    public Collection<Role> getAvailableRoles() {
        return new ArrayList<>(availableRoles);
    }
}
