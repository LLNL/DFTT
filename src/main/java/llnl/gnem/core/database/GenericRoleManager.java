package llnl.gnem.core.database;

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
