/**
 * Created by dodge1 Date: Jul 1, 2008 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
package llnl.gnem.core.waveform.responseProcessing;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Level;

import llnl.gnem.core.database.Connections;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.Passband;
import llnl.gnem.core.waveform.responseProcessing.ResponseMetadataManager.EnhancedResponseMetaData;
import llnl.gnem.core.waveform.responseProcessing.spi.TransferData;
import llnl.gnem.core.waveform.responseProcessing.spi.TransferFunctionServiceProvider;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;

/**
 * A class that manages the process of removing instrument responses from
 * Seismograms. This class is intended for use within the RBAP code. It assumes
 * the existence of Sensor, Instrument tables that contain responses in RESP,
 * SAC pole-zero, or cssndc (paz, fap, pazfir) formats. It further assumes that
 * any CssSeismogram that it is called on to process will properly join to a
 * response in the sensor and instrument tables.
 *
 * @author Doug Dodge
 */
public class TransferFunctionService {

    private static TransferFunctionService ourInstance = null;
    private final ResponseMetadataManager metadataManager;
    private final ServiceLoader<TransferFunctionServiceProvider> loader;
    private Connections connection;

    public static TransferFunctionService getInstance() {
        if (ourInstance == null) {
            ourInstance = new TransferFunctionService();
        }
        return ourInstance;
    }

    public void setConnection(Connections connection) {
        this.connection = connection;
    }

    private TransferFunctionService() {
        metadataManager = new ResponseMetadataManager();
        loader = ServiceLoader.load(TransferFunctionServiceProvider.class);

    }

    public CompositeTransferFunction getTransferFunction(CssSeismogram s, ResponseType toType) throws SQLException, IOException {
        if (connection == null) {
            throw new IllegalStateException(
                    "TransferFunctionService does not have initialized connections object! This must be done before first use by calling the setConnection(Connections) method.");
        }
        Connection conn = null;
        try {
            conn = connection.checkOut();
            return getTransferFunction(s, toType, conn);
        } finally {
            if (conn != null) {
                connection.checkIn(conn);
            }
        }
    }

    private CompositeTransferFunction getTransferFunction(CssSeismogram s, ResponseType toType, Connection conn)
            throws SQLException, IOException {
        EnhancedResponseMetaData rmd = metadataManager.getEnhancedResponseMetaData(s, conn);
        if (rmd == null) {
            String msg = String.format("Error in Transfer getting Response Meta Data for (sta = %s, chan = %s, start = %s )",
                    s.getSta(), s.getChan(), s.getTime());
            throw new IllegalStateException(msg);
        } else {
            FreqLimits limits = TransferParams.getInstance().getFreqLimits(s.getNyquistFreq(), s.getSegmentLength());
            return getTransferFunction(s, toType, rmd, limits);
        }
    }

    /**
     * Deconvolve the instrument response from a CssSeismogram. Resulting
     * CssSeismogram will be in ground-motion units. The actual ground motion
     * type is specified by the toType value. Assumes that a response exists in
     * the database for this CssSeismogram.
     *
     * @param s The CssSeismogram (assumed to be in digital counts) which is to
     * be transformed.
     * @param toType The ground motion type to produce (one of displacement,
     * velocity, or acceleration).
     * @return Returns true if operation was successful.
     * @throws SQLException Exception thrown if there is an error on the
     * database server for some SQL transaction.
     */
    public boolean transfer(CssSeismogram s, ResponseType toType) throws SQLException {
        if (connection == null) {
            throw new IllegalStateException(
                    "TransferFunctionService does not have initialized connections object! This must be done before first use by calling the setConnection(Connections) method.");
        }
        Connection conn = null;
        try {
            conn = connection.checkOut();
            return transfer(s, toType, conn);
        } finally {
            if (conn != null) {
                connection.checkIn(conn);
            }
        }
    }

    public FreqLimits determineFreqLimitsFromTransferFunction(CssSeismogram seis, Connection conn) throws SQLException, IOException {
        ApplicationLogger.getInstance().log(Level.FINE, String.format("Beginning retrieval of transfer function for seismogram: %s...", seis.toString()));
        EnhancedResponseMetaData rmd = metadataManager.getEnhancedResponseMetaData(seis, conn);
        if (rmd == null) {
            String msg = String.format(
                    "Error in Transfer getting Response Meta Data for wfid %d (sta = %s, chan = %s, start = %s )",
                    seis.getWaveformID(), seis.getSta(), seis.getChan(), seis.getTime());
            ApplicationLogger.getInstance().log(Level.WARNING, msg);
            throw new IllegalStateException(msg);
        }

        ResponseType fromType = rmd.getRsptype();
        Iterator<TransferFunctionServiceProvider> providers = loader.iterator();
        while (providers.hasNext()) {
            TransferFunctionServiceProvider provider = providers.next();
            if (provider.supportedSourceResponseTypes().contains(fromType)) {
                return provider.determineFreqLimitsFromTransferFunction(seis, rmd);
            }
        }
        throw new IllegalStateException("Failed to find provider for FreqLimits determination!");
    }

    public boolean transfer(CssSeismogram s, ResponseType toType, Connection conn) throws SQLException {
        FreqLimits limits = TransferParams.getInstance().getFreqLimits(s.getNyquistFreq(), s.getSegmentLength());
        return transferWithLimits(s, toType, limits, conn );
    }

    public boolean transferWithLimits(CssSeismogram s, ResponseType toType, FreqLimits limits, Connection conn) throws SQLException {
        ApplicationLogger.getInstance().log(Level.FINE, String.format("Beginning transfer of seismogram: %s...", s.toString()));
        EnhancedResponseMetaData rmd = metadataManager.getEnhancedResponseMetaData(s, conn);
        if (rmd == null) {
            String msg = String.format(
                    "Error in Transfer getting Response Meta Data for wfid %d (sta = %s, chan = %s, start = %s )",
                    s.getWaveformID(), s.getSta(), s.getChan(), s.getTime());
            ApplicationLogger.getInstance().log(Level.WARNING, msg);
            return false;
        }
        try {

            transfer(s, toType, rmd, limits, true);
            return true;
        } catch (IOException e) {

            StringBuilder msg = new StringBuilder("Error in Transfer processing response for wfid ");
            msg.append(s.getWaveformID()).append(" (sta = ");
            msg.append(s.getSta()).append(", chan = ").append(s.getChan()).append(", start = ").append(s.getTimeAsDouble())
                    .append(")  Error was: ");
            msg.append(e.toString()).append(" File was ").append(rmd.getFilename());
            ApplicationLogger.getInstance().log(Level.WARNING, msg.toString(), e);
            return false;
        }
    }

    //FIXME fix up to support other packages testing!
    public void transfer(CssSeismogram s, ResponseType toType, ResponseMetaData rmd, FreqLimits limits, boolean conditionSignalBeforeTransfer) throws IOException {

        String sta = s.getSta();

        if (conditionSignalBeforeTransfer) {
            boolean twoPass = true;
            s.RemoveMean();
            s.Taper(5.0);
            s.filter(2, Passband.BAND_PASS, limits.getLowpass(), limits.getHighpass(), twoPass);
        }
        javaTransfer(s, toType, rmd, limits);
    }

    private void javaTransfer(CssSeismogram seis, ResponseType toType, ResponseMetaData rmd, FreqLimits limits) throws IOException {
        /*
         * =====================================================================
         * PURPOSE: To apply an instrument transfer function to a data set.
         * =====================================================================
         * /* PROCEDURE:
         */

        int nfft = TransferFunctionUtils.next2(seis.getNsamp());
        int nfreq = nfft / 2 + 1;
        double[] sre = new double[nfft];
        double[] sim = new double[nfft];

        double delta = 1.0 / seis.getSamprate();

        /*
         * - Fill a complex zero-padded vector and then transform it.
         */
        float[] data = seis.getData();
        for (int i = 0; i < data.length; ++i) {
            sre[i] = data[i] * delta;
            sim[i] = 0.0;
        }

        for (int i = seis.getNsamp(); i < nfft; ++i) {
            sre[i] = 0.0;
            sim[i] = 0.0;
        }

        DCPFT dcpft = new DCPFT(nfft);
        dcpft.dcpft(sre, sim, nfft, -1);

        /*
         * - Multiply transformed data by composite transfer operator.
         */
        CompositeTransferFunction ctf = javaGetTransferFunction(seis, toType, rmd, limits);
        double[] xxre = ctf.getXre();
        double[] xxim = ctf.getXim();
        for (int i = 0; i < nfreq; ++i) {
            double tempR = xxre[i] * sre[i] - xxim[i] * sim[i];
            double tempI = xxre[i] * sim[i] + xxim[i] * sre[i];
            sre[i] = tempR;
            sim[i] = tempI;

            /*
             * Input data are real so F(N-j) = F(j)
             */
            if (i > 0 && i < nfreq - 1) {
                int j = nfft - i;
                sre[j] = tempR;
                sim[j] = -tempI;
            }
        }

        /*
         * - Perform the inverse transform.
         */
        dcpft.dcpft(sre, sim, nfft, 1);

        /*
         * - Copy the transformed data back into the original data array.
         */

 /*
         * -- nmScale is 1 by default, but if EVALRESP is used, nmScale converts
         * to nm. multiplier is used to apply or unapply calib
         */
        float nmScale = ctf.getNmScale();
        for (int i = 0; i < data.length; ++i) {
            data[i] = (float) (sre[i] * nmScale * ctf.getDataMultiplier());
        }

        seis.setData(data);
    }

    private CompositeTransferFunction getTransferFunction(CssSeismogram s, ResponseType toType, ResponseMetaData rmd, FreqLimits limits) throws IOException {
        CompositeTransferFunction ctf = javaGetTransferFunction(s, toType, rmd, limits);
        return ctf;
    }

    private CompositeTransferFunction javaGetTransferFunction(CssSeismogram seis, ResponseType toType, ResponseMetaData rmd, FreqLimits limits) throws IOException {

        CompositeTransferFunction function = null;

        ResponseType fromType = rmd.getRsptype();
        Iterator<TransferFunctionServiceProvider> providers = loader.iterator();

        TransferData transferData = null;

        // preprocess
        while (providers.hasNext()) {
            TransferFunctionServiceProvider provider = providers.next();
            if (provider.supportedSourceResponseTypes().contains(fromType)) {

                transferData = provider.preprocessWaveformData(seis, toType, rmd);
                if (transferData != null) {
                    break;
                }
            }
        }

        if (transferData == null) {
            ApplicationLogger.getInstance().log(Level.WARNING, "Could not create a TransferData for ResponseType: " + fromType);
            throw new IllegalStateException("Unhandled From Type: " + fromType);
        }

        //reset the providers
        providers = loader.iterator();
        // buildTransferFunciton!!
        while (providers.hasNext()) {
            TransferFunctionServiceProvider provider = providers.next();
            if (provider.supportedSourceResponseTypes().contains(toType)) {

                function = provider.buildTransferFunction(seis, toType, rmd, limits, true, transferData);
                if (function != null) {
                    break;
                }
            }
        }

        if (function == null) {
            ApplicationLogger.getInstance().log(Level.WARNING, "Could not create a CompositeTransferFunction for ResponseType: " + toType);
            throw new IllegalStateException("Unhandled To Type: " + toType);
        }

        return function;
    }

    public static void setSensorTable(String st) {
        ResponseMetadataManager.setSensorTable(st);
    }

    public static void setInstrumentTable(String it) {
        ResponseMetadataManager.setInstrumentTable(it);
    }
}
