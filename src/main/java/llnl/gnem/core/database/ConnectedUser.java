/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.database;

/**
 *
 * @author dodge1
 */
public class ConnectedUser {

    private static ConnectedUser instance = null;
    private final String user;

    private ConnectedUser(String user) {
        this.user = user;
    }

    public static ConnectedUser getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Attempt to access uninitialized instance!");
        }
        return instance;
    }

    public static void createInstance(String user) {
        if (instance != null) {
            throw new IllegalStateException("Attempt to create 2nd instance!");
        }
        instance = new ConnectedUser(user);

    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }
}
