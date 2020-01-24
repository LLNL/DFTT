/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.metadata;

import java.io.Serializable;

import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */

@ThreadSafe
public class EtypeInfo implements Serializable, TypeInfo {
    private final String code;
    private final String description;
    static final long serialVersionUID = -8208426757913606192L;
    
    public EtypeInfo(String code, String description)
    {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString()
    {
        return code + "  (" + description + ")";
    }
    
}
