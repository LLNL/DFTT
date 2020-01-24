package llnl.gnem.core.waveform.io.mseed;

import edu.iris.dmc.seedcodec.CodecException;
import edu.iris.dmc.timeseries.model.Timeseries;
import edu.iris.dmc.timeseries.model.Util;
import edu.sc.seis.seisFile.mseed.Blockette;
import edu.sc.seis.seisFile.mseed.Blockette1001;
import edu.sc.seis.seisFile.mseed.DataHeader;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.SeedRecord;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Read MiniSeed files using SeisFile
 *
 * @author maganazook1 (maganazook1@llnl.gov)
 */
public class MiniSeedReader {

    public static final int DEFAULT_SEED_RECORD_SIZE = 4096;

    public static ArrayList<Timeseries> read(String filepath) throws IOException, SeedFormatException, CodecException {
        return MiniSeedReader.read(new DataInputStream(new FileInputStream(filepath)));
    }

    /**
     * Mostly a code copy from IRIS WS library but re-tooled to read from
     * file/stream instead of from a web service.
     *
     * @param fileStream
     * @return
     * @throws IOException
     * @throws SeedFormatException
     * @throws CodecException
     */
    public static ArrayList<Timeseries> read(DataInput fileStream) throws IOException, SeedFormatException, CodecException {
        ArrayList<Timeseries> collectionTimeseries = new ArrayList<>();

        while (true) // loops until it reaches end of file exception. there has to be a better way!
        {
            try {
                SeedRecord sr = SeedRecord.read(fileStream, DEFAULT_SEED_RECORD_SIZE);
                if (sr instanceof DataRecord) {
                    DataRecord dr = (DataRecord) sr;
                    byte microseconds = 0;
                    Blockette[] bs = dr.getBlockettes(1001);
                    if (bs.length > 0) {
                        Blockette1001 b1001 = (Blockette1001) bs[0];
                        microseconds = b1001.getMicrosecond();
                    }

                    if (dr.getBlockettes(1000).length != 0) {
                        // ControlHeader
                        DataHeader header = (DataHeader) dr
                                .getControlHeader();
                        String network = header.getNetworkCode();
                        String station = header.getStationIdentifier();
                        String location = header
                                .getLocationIdentifier();
                        String channel = header.getChannelIdentifier();

                        Timestamp startTime = Util.toTime(
                                header.getStartBtime(),
                                header.getActivityFlags(),
                                header.getTimeCorrection(),
                                microseconds);

                        Timeseries timeseries = new Timeseries(network,
                                station, location, channel);

                        // Add segments
                        timeseries.add(startTime, dr);

                        collectionTimeseries.add(timeseries);
                    }
                }
            } catch (EOFException e) {
                break; // done reading seed records
            }
        }

        return collectionTimeseries;
    }
}
