package llnl.gnem.core.database.login;

import java.awt.HeadlessException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.apache.commons.cli.ParseException;

import llnl.gnem.core.database.AdminValidator;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.database.RoleManager;
import llnl.gnem.core.gui.util.ExceptionDialog;
import llnl.gnem.core.util.ApplicationLogger;

/**
 * Created by: dodge1 Date: Dec 8, 2004 COPYRIGHT NOTICE GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
public class LoginManager {

    private static int numTries;
    private static boolean isAdmin;
    
	private static final String DEFAULT_USER_ACK = "====================================================================\n"
			+ "  Default User Acknowledgement "
			+ "====================================================================";
    
    private static String userAcknowlegement = "";

    private static boolean adminStatusOk(AdminValidator validator, String userName, RoleManager manager) {

            return false;
    }

    private static void setApplicationRoles(RoleManager roleManager) {
        try {
            ConnectionManager cm = ConnectionManager.getInstance();

            if (!cm.setApplicationRoles(roleManager)) {
                ConnectionManager.releaseInstance();
                throw new IllegalStateException("Unable to set necessary appliaction role(s)");
            }

        } catch (IllegalStateException | InterruptedException | SQLException e) {
            ExceptionDialog.displayError(e);
            System.exit(1);
        }

    }

    public static boolean login(DbCredentials creds) {
        return login(creds.getUsername(), creds.getPassword());
    }

    public static boolean login(final String user, final String password) {
        boolean loginSuccessful = false;
        try {
            ConnectionManager.getInstance(user, password);
            loginSuccessful = true;
        } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e) {
            final ApplicationLogger appLogger = ApplicationLogger.getInstance();
            if (appLogger.hasGui()) {
                (new ExceptionDialog()).displayException(e);
            } else {
                appLogger.log(Level.FINER, "Login did not succeed", e);
            }
            ConnectionManager.releaseInstance();

            if (e.getMessage().contains("Unable to set role")) {
                System.exit(1);
            }
        }
        return loginSuccessful;
    }

    /**
     * This method used for generic logins to the Database Note: this will not
     * kill the program if the login fails
     *
     * @return true if the user has successfully logged on.
     */
    public static boolean login() {
        try {
            DbServiceInfoManager dsim = DbServiceInfoManager.getInstance();
            LoginInfoPanel infoPanel = new LoginInfoPanel(dsim);
            int option = JOptionPane.showOptionDialog(null, new Object[]{infoPanel}, "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

            if ((option == JOptionPane.CLOSED_OPTION) || (option == JOptionPane.CANCEL_OPTION)) {
                return false;
            }

            String user = infoPanel.getLoginName();
            String password = infoPanel.getPassword();
            DbServiceInfo dsi = infoPanel.getSelectedService();
            dsim.setSelectedService(dsi);
            if (dsi.isSensitiveService() && !hasUserAcceptedConditions()) {
                login();
            }
            return login(user, password);
        } catch (HeadlessException | IOException | ClassNotFoundException e) {
            ExceptionDialog.displayError(e);
        }
        return false;
    }

    public static void login(AdminValidator validator, RoleManager manager, DbCredentials credentials, ImageIcon icon) {
        try {
            ConnectionManager.getInstance(credentials);
            ConnectionManager.getInstance().setApplicationRoles(manager);
        } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException | InterruptedException | SQLException e) {
            login(validator, manager, null);
        }
    }

    public static void login(AdminValidator validator, RoleManager manager, String[] args, ImageIcon icon) {
        try {
            if (args.length > 0) {
                DbCredentials creds = new DbCredentials(args[0]);
                ConnectionManager.getInstance(creds.getUsername(), creds.getPassword(), creds.getInstance());
                ConnectionManager.getInstance().setApplicationRoles(manager);
            } else {
                login(validator, manager, icon);
            }
        } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException | InterruptedException | SQLException | ParseException e) {
            login(validator, manager, icon);
        }
    }

    public static void login(AdminValidator validator, RoleManager roleManager, ImageIcon icon) {
        if (numTries++ > 2) {
            System.exit(1);
        }

        try {
            setApplicationAdminStatus(false);
            DbServiceInfoManager dsim = DbServiceInfoManager.getInstance();
            LoginInfoPanel infoPanel = new LoginInfoPanel(dsim);
            LoginAuxInfoPanel auxPanel = new LoginAuxInfoPanel(roleManager);

            int option = JOptionPane.showOptionDialog(
                    null,
                    new Object[]{infoPanel, auxPanel},
                    "Login",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    icon,
                    null, null);
            if ((option == JOptionPane.CLOSED_OPTION) || (option == JOptionPane.CANCEL_OPTION)) {
                System.exit(0);
            }
            String user = infoPanel.getLoginName();
            String password = infoPanel.getPassword();
            DbServiceInfo dsi = infoPanel.getSelectedService();
            dsim.setSelectedService(dsi);
            if (dsi.isSensitiveService() && !hasUserAcceptedConditions()) {
                login(validator, roleManager, icon);
            }

            if (!login(user, password)) {
                login(validator, roleManager, icon);
            }

            setApplicationRoles(roleManager);
            if (validator.isAdminOptionAvailable() && auxPanel.isAdminStatusSelected()) {
                if (adminStatusOk(validator, user, roleManager)) {
                    setApplicationAdminStatus(true);
                } else {
                    login(validator, roleManager, icon);
                }
            } else {
                setApplicationAdminStatus(false);
            }

        } catch (HeadlessException | IOException | ClassNotFoundException e) {
            ExceptionDialog.displayError(e);
        }
    }

    protected static void setApplicationAdminStatus(boolean ok) {
        isAdmin = ok;
    }

	public static boolean hasUserAcceptedConditions() {

		if (userAcknowlegement == null || userAcknowlegement.isEmpty()) {
			userAcknowlegement = DEFAULT_USER_ACK;
		}

		int answer = JOptionPane.showConfirmDialog(null, userAcknowlegement, "Resource Contains Sensitive Data",
				JOptionPane.OK_CANCEL_OPTION);
		
		return answer == JOptionPane.OK_OPTION;
	}

    public static boolean isAdmin() {
        return isAdmin;
    }

	public static String getUserAcknowlegement() {
		return userAcknowlegement;
	}

	public static void setUserAcknowlegement(String userAcknowlegement) {
		LoginManager.userAcknowlegement = userAcknowlegement;
	}
}
