/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by dodge1
 * Date: Feb 22, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class BasicRoleManager implements RoleManager{
    @Override
    public void setRoles(Connection conn) {
        //No roles to set, so do nothing
    }

    @Override
    public boolean isRunnable() {
        return true;
    }

    @Override
    public boolean isRoleAvailable(Role role) {
        return false;  //There are no roles, so no roles are available
    }

    @Override
    public Collection<Role> getAvailableRoles() {
        return new ArrayList<>();
    }

    @Override
    public boolean hasRoles() {
        return false;  //There are no roles available
    }

    @Override
    public void addAvailableRoles(Collection<Role> roles) {
        //This action does nothing here.
    }
}
