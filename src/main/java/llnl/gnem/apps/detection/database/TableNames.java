/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
