package llnl.gnem.core.database.dao;

import llnl.gnem.core.waveform.Wfdisc;
import llnl.gnem.core.waveform.io.BinaryData;
import llnl.gnem.core.waveform.io.BinaryDataReader;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;

/**
 *
 * @author addair1
 */
public abstract class WaveformDAO {
     /**
     * Used heavily in Big Data Applications
     *
     * @param wfdisc
     * @param rawData
     * @return
     */
    public static CssSeismogram getSeismogram(Wfdisc wfdisc, byte[] rawData) {
        try {
            BinaryDataReader bdr = BinaryDataReader.getReader(wfdisc.getDatatype());
            BinaryData bdata = bdr.readFloatData(rawData, wfdisc.getFoff(), wfdisc.getNsamp());

            return new CssSeismogram(wfdisc, bdata);
        } catch (Exception ex) {
            String msg = String.format("Failed attempting to read %d samples "
                    + "of %s data at offset %d", wfdisc.getNsamp(), wfdisc.getDatatype(), wfdisc.getFoff());
            throw new IllegalStateException(msg, ex);
        }
    }

    public abstract CssSeismogram getSeismogram(Wfdisc wfdisc) throws InterruptedException;

    public BinaryData getBinaryData(String dir, String dfile, int foff, int nsamp, String datatype) throws InterruptedException {
        String fname = dir + '/' + dfile;

        BinaryDataReader bdr = BinaryDataReader.getReader(datatype);
        if (bdr != null) {
            try {
                return bdr.readFloatData(fname, foff, nsamp);
            } catch (Exception e) {

                if (e instanceof InterruptedException) {
                    throw new InterruptedException();
                }
                String msg = String.format("Failed attempting to read %d samples "
                        + "of %s data at offset %d for file: %s", nsamp, datatype, foff, fname);

                throw new IllegalStateException(msg, e);
            }
        } else {
            throw new IllegalStateException("No BinaryDataReader was instantiated. Could not read data.");
        }
    }

    public float[] getSeismogramData(String dir, String dfile, int foff, int nsamp, String datatype) throws InterruptedException {
        return getBinaryData(dir, dfile, foff, nsamp, datatype).getFloatData();
    }
}
