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
package llnl.gnem.apps.detection.database;

import java.util.prefs.Preferences;

/**
 *
 * @author dodge1
 */
public class TableNames {
    private String siteTableName;
    private String originTableName;
    private String chanDescTableName;
    private String channelSubstitutionTableName;
    private final Preferences prefs;
    
    private TableNames() {
        prefs = Preferences.userNodeForPackage(this.getClass());
        siteTableName = prefs.get("SITE_TABLE_NAME", "site");
        originTableName = prefs.get("ORIGIN_TABLE_NAME", "origin");
        chanDescTableName = prefs.get("CHAN_DESC_TABLE_NAME", "chan_desc");
        channelSubstitutionTableName = prefs.get("CHAN_SUB_TABLE_NAME", "channel_substitution");
    }
    
    public static TableNames getInstance() {
        return TableNamesHolder.INSTANCE;
    }

    /**
     * @return the siteTableName
     */
    public synchronized String getSiteTableName() {
        return siteTableName;
    }

    /**
     * @param siteTableName the siteTableName to set
     */
    public synchronized void setSiteTableName(String siteTableName) {
        this.siteTableName = siteTableName;
        prefs.put("SITE_TABLE_NAME", siteTableName);
    }

    /**
     * @return the originTableName
     */
    public synchronized String getOriginTableName() {
        return originTableName;
    }

    /**
     * @param originTableName the originTableName to set
     */
    public synchronized void setOriginTableName(String originTableName) {
        this.originTableName = originTableName;
        prefs.put("ORIGIN_TABLE_NAME", originTableName);
    }

    /**
     * @return the chanDescTableName
     */
    public synchronized String getChanDescTableName() {
        return chanDescTableName;
    }

    /**
     * @param chanDescTableName the chanDescTableName to set
     */
    public synchronized void setChanDescTableName(String chanDescTableName) {
        this.chanDescTableName = chanDescTableName;
        prefs.put("CHAN_DESC_TABLE_NAME", chanDescTableName);
    }

    public synchronized String getChanSubTableName() {
        return channelSubstitutionTableName;
    }
    
    private static class TableNamesHolder {

        private static final TableNames INSTANCE = new TableNames();
    }
}
