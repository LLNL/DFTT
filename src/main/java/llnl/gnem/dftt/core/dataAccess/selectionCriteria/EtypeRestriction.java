/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
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
package llnl.gnem.dftt.core.dataAccess.selectionCriteria;

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
