/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author dodge1
 */
public interface Connections {

    public Connection checkOut() throws SQLException;

    public void checkIn(Connection conn) throws SQLException;
    
}
