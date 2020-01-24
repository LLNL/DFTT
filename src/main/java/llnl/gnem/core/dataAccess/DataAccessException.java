/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess;

import java.sql.SQLException;

/**
 *
 * @author dodge1
 */
public class DataAccessException extends Exception{

    private static final long serialVersionUID = -1444951393721941342L;
    
    public DataAccessException(SQLException ex){
        super(ex);
    }
    public DataAccessException( String msg)
    {
        super(msg);
    }
}
