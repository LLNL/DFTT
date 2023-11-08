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
package llnl.gnem.dftt.core.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class GenericRoleManager implements RoleManager {

  
    private final Collection<Role> availableRoles;


    @Override
    public void addAvailableRoles(Collection<Role> roles) {
        availableRoles.addAll(roles);
    }


    @Override
    public boolean hasRoles() {
        return !(availableRoles.isEmpty());
    }

    public GenericRoleManager(Collection<Role> roles) {
        availableRoles = new ArrayList<>();
  
        for (Role role : roles) {
            availableRoles.add(role);
        }
    }

    @Override
    public void setRoles(Connection conn) {
        try {
            for (Role role : availableRoles) {
                setRole(role, conn);
            }
        } catch (SQLException ignore) {
            // Nothing to do here
        }

    }

    private String getSql(Role role) {
        return String.format("set role ", role.toString());
    }

    @Override
    public boolean isRunnable() {
        return !(availableRoles.isEmpty());
    }

    @Override
    public boolean isRoleAvailable(Role role) {
        return availableRoles.contains(role);
    }

    @Override
    public Collection<Role> getAvailableRoles() {
        ArrayList<Role> result =  new ArrayList<>(availableRoles);
        return result;
    }

    private void setRole(Role role, Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(getSql(role));
        }
    }
}
