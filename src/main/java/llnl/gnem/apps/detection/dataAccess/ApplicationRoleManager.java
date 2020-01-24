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
