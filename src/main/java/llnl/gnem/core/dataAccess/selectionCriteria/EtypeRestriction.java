/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.selectionCriteria;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author dodge1
 */
public class EtypeRestriction {

    private final ArrayList<String> etypes;
    private boolean restrictByEtype;

    public EtypeRestriction(Collection<String> etypes, boolean restrictByEtype) {
        this.etypes = new ArrayList<>(etypes);
        this.restrictByEtype = restrictByEtype;
    }

    public EtypeRestriction() {
        etypes = new ArrayList<>();
    }

    public String getSQLClause() {
        if (!restrictByEtype || etypes.isEmpty()) {
            return " ";
        } else {
            StringBuilder sb = new StringBuilder(" and etype in (");
            for (int j = 0; j < etypes.size(); ++j) {
                String tmp = "'" + etypes.get(j) + "'";
                sb.append(tmp);
                if (j < etypes.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append(")");
            return sb.toString();
        }

    }
}
