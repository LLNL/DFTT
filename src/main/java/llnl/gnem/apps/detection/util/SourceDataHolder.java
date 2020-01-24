/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.util;

import llnl.gnem.apps.detection.database.ChannelSubstitutionDAO;
import llnl.gnem.apps.detection.database.ConfigurationDAO;
import llnl.gnem.apps.detection.database.DbOps;
import llnl.gnem.apps.detection.database.FrameworkRunDAO;
import llnl.gnem.apps.detection.source.SourceData;
import llnl.gnem.apps.detection.source.WfdiscTableSourceData;

/**
 *
 * @author dodge1
 */
public class SourceDataHolder {

    private SourceData source;
    private int currentRunid;

    private SourceDataHolder() {
    }

    public static SourceDataHolder getInstance() {
        return SourceDataHolderHolder.INSTANCE;
    }

    private static class SourceDataHolderHolder {

        private static final SourceDataHolder INSTANCE = new SourceDataHolder();
    }

    public SourceData getSourceData(int runid) throws Exception {
        if (currentRunid == runid) {
            return source;
        } else {
            if (source != null) {
                source.close();
            }
            FrameworkRun fr = FrameworkRunDAO.getInstance().getFrameworkRun(runid);
            String sourceWfdiscTable = fr.getWfdisc();
            double fixedRawRate = fr.getFixedRawSampleRate();
            String configName = ConfigurationDAO.getInstance().getConfigNameForRun(runid);
            int configid = ConfigurationDAO.getInstance().getConfigid(configName);
            source = new WfdiscTableSourceData(sourceWfdiscTable, configName, false);
            source.setFixedRawRate(fixedRawRate);
            source.setChannelSubstitutions(ChannelSubstitutionDAO.getInstance().getChannelSubstitutions(configid));
            source.setStaChanArrays();
            currentRunid = runid;
            return source;
        }
    }
}
