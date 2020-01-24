package llnl.gnem.apps.detection.source;

import llnl.gnem.apps.detection.database.DbOps;
import java.sql.SQLException;

/**
 * Created by dodge1 Date: Jul 14, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class WfdiscTableSourceData extends SourceData {

    public WfdiscTableSourceData(String srcWfdiscTable, String streamGroup, boolean scaleByCalib) throws Exception {
        super(streamGroup, scaleByCalib);
        testWfdiscTable(srcWfdiscTable);
        setWfdiscTable(srcWfdiscTable);
    }

    private void testWfdiscTable(String wfdiscTable) throws SQLException {

        if (!DbOps.getInstance().isTableExists(wfdiscTable)) {
            throw new IllegalArgumentException(String.format("The specified table(%s) does not exist or is inaccessible!!", wfdiscTable));
        }
    }

    @Override
    public void close() throws SQLException {
    }
}
