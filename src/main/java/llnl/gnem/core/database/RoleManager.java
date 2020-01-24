package llnl.gnem.core.database;

import java.sql.Connection;
import java.util.Collection;

/**
 * Created by dodge1
 * Date: Feb 19, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public interface RoleManager {
    void setRoles(Connection conn);
    boolean isRunnable();
    boolean isRoleAvailable( Role role );
    Collection<Role> getAvailableRoles();
    boolean hasRoles();
    void addAvailableRoles( Collection<Role> roles );
}
