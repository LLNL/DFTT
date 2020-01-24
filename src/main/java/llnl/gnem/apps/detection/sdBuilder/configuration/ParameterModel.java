/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.configuration;

import java.util.prefs.Preferences;
import llnl.gnem.apps.detection.core.dataObjects.DetectorType;

/**
 *
 * @author dodge1
 */
public class ParameterModel {

    private double correlationWindowLength;
    private double correlationThreshold;
    private double detectionThreshold;
    private double energyCapture;
    private double blackoutSeconds;
    private DetectorType detectorType;
    private int fftSize;
    private int matchedFieldDesignFactor;
    private int matchedFieldDimension;
    private double staDuration;
    private double ltaDuration;
    private double gapDuration;
    private boolean normalizeStatistics;
    private boolean prewhitenStatistics;
    private boolean enableSpawning;
    private double traceLength;
    private boolean isFixShiftsToZero;
    private int minDetectionCountForRetrieval;
    private double prepickSeconds;
    private final Preferences prefs;
    private double windowStart;
    private double minDetStatThreshold;
    private double maxDetStatThreshold;
    private boolean suppressBadDetectors;
    private boolean requireCorrelation;
    private boolean fixSubspaceDimension;
    private int subspaceDimension;
    private boolean autoApplyFilter;
    private boolean requireWindowPositionConfirmation;
    private boolean showCorrelationWindow;
    private boolean retrieveByBlocks;
    private int blockSize;

    private ParameterModel() {
        prefs = Preferences.userNodeForPackage(this.getClass());
        correlationThreshold = prefs.getDouble("CorrelationThreshold", 0.7);
        detectionThreshold = prefs.getDouble("DetectionThreshold", 0.5);
        energyCapture = prefs.getDouble("EnergyCapture", 0.8);
        blackoutSeconds = prefs.getDouble("BlackoutSeconds", 5.0);
        detectorType = DetectorType.valueOf(prefs.get("DetectorType", DetectorType.SUBSPACE.toString()));
        fftSize = prefs.getInt("FFT_SIZE", 128);
        matchedFieldDesignFactor = prefs.getInt("DesignFactor", 3);
        matchedFieldDimension = prefs.getInt("MFDimension", 1);
        staDuration = prefs.getDouble("STADuration", 4.0);
        ltaDuration = prefs.getDouble("LTADuration", 40.0);
        gapDuration = prefs.getDouble("GapDuration", 1.0);
        normalizeStatistics = prefs.getBoolean("NORMALIZE_STATS", true);
        prewhitenStatistics = prefs.getBoolean("PREWHITEN_STATS", false);
        enableSpawning = prefs.getBoolean("ENABLE_SPAWNING", true);
        prepickSeconds = prefs.getDouble("PRE_PICK_SECONDS", 150.0);
        double tmp = prefs.getDouble("TRACE_LENGTH", 200.0);
        if (tmp < prepickSeconds + 10) {
            tmp = prepickSeconds + 10;
        }
        traceLength = tmp;
        isFixShiftsToZero = prefs.getBoolean("FORCE_ZERO_SHIFTS", false);
        minDetectionCountForRetrieval = prefs.getInt("MIN_DETECTION_COUNT", 1);
        minDetStatThreshold = prefs.getDouble("MIN_DET_STAT_THRESH", 0.1);
        maxDetStatThreshold = prefs.getDouble("MAX_DET_STAT_THRESH", 1.0);
        windowStart = 0;
        suppressBadDetectors = prefs.getBoolean("SUPPRESS_BAD_DETECTORS", true);
        requireCorrelation = prefs.getBoolean("REQUIRE_CORRELATION", false);
        fixSubspaceDimension = prefs.getBoolean("FIX_SUBSPACE_DIMENSION", false);
        subspaceDimension = prefs.getInt("SUBSPACE_DIMENSION", 1);
        autoApplyFilter = prefs.getBoolean("AUTO_APPLY_FILTER", false);
        requireWindowPositionConfirmation = prefs.getBoolean("REQUIRE_WINDOW_POSITION_CONFIRMATION", true);
        showCorrelationWindow = prefs.getBoolean("SHOW_CORRELATION_WINDOW", true);
        retrieveByBlocks = prefs.getBoolean("RETRIEVE_BY_BLOCKS", false);
        blockSize = prefs.getInt("BLOCK_SIZE", 100);

    }

    public static ParameterModel getInstance() {
        return ParameterModelHolder.INSTANCE;
    }

    public boolean isRequireCorrelation() {
        return requireCorrelation;
    }

    public void setRequireCorrelation(boolean requireCorrelation) {
        this.requireCorrelation = requireCorrelation;
        prefs.putBoolean("REQUIRE_CORRELATION", requireCorrelation);
        DetectorCreationEnabler.getInstance().correlationRequired(requireCorrelation);
    }

    public boolean isAutoApplyFilter() {
        return autoApplyFilter;
    }

    public void setAutoApplyFilter(boolean autoApplyFilter) {
        this.autoApplyFilter = autoApplyFilter;
        prefs.putBoolean("AUTO_APPLY_FILTER", autoApplyFilter);
    }

    public boolean isRequireWindowPositionConfirmation() {
        return requireWindowPositionConfirmation;
    }

    public void setRequireWindowPositionConfirmation(boolean value) {
        requireWindowPositionConfirmation = value;
        prefs.putBoolean("REQUIRE_WINDOW_POSITION_CONFIRMATION", requireWindowPositionConfirmation);
    }

    public boolean isShowCorrelationWindow() {
        return showCorrelationWindow;
    }
    
    public void setShowCorrelationWindow( boolean value){
        showCorrelationWindow = value;
        prefs.putBoolean("SHOW_CORRELATION_WINDOW", showCorrelationWindow);
    }

    private static class ParameterModelHolder {

        private static final ParameterModel INSTANCE = new ParameterModel();
    }

    public void setCorrelationWindowLength(double correlationWindowLength) {
        this.correlationWindowLength = correlationWindowLength;
    }

    /**
     * @return the correlationWindowLength
     */
    public double getCorrelationWindowLength() {
        return correlationWindowLength;
    }

    /**
     * @param prepickSeconds the prepickSeconds to set
     */
    public void setPrepickSeconds(double prepickSeconds) {
        this.prepickSeconds = prepickSeconds;
        prefs.putDouble("PRE_PICK_SECONDS", prepickSeconds);
    }

    public double getPrepickSeconds() {
        return prepickSeconds;
    }

    public void adjustWindowStart(double deltaT) {
        windowStart += deltaT;
    }

    /**
     * @return the correlationThreshold
     */
    public double getCorrelationThreshold() {
        return correlationThreshold;
    }

    /**
     * @param correlationThreshold the correlationThreshold to set
     */
    public void setCorrelationThreshold(double correlationThreshold) {
        this.correlationThreshold = correlationThreshold;
        prefs.putDouble("CorrelationThreshold", correlationThreshold);
    }

    public double getDetectionThreshold() {
        return detectionThreshold;
    }

    public double getEnergyCapture() {
        return energyCapture;
    }

    public double getBlackoutSeconds() {
        return blackoutSeconds;
    }

    /**
     * @param detectionThreshold the detectionThreshold to set
     */
    public void setDetectionThreshold(double detectionThreshold) {
        this.detectionThreshold = detectionThreshold;
        prefs.putDouble("DetectionThreshold", detectionThreshold);
    }

    /**
     * @param energyCapture the energyCapture to set
     */
    public void setEnergyCapture(double energyCapture) {
        this.energyCapture = energyCapture;
        prefs.putDouble("EnergyCapture", energyCapture);
    }

    public void setFixSubspaceDimension(boolean value) {
        this.fixSubspaceDimension = value;
        prefs.putBoolean("FIX_SUBSPACE_DIMENSION", value);
    }

    public void setSubspaceDimension(int dimension) {
        this.subspaceDimension = dimension;
        prefs.putInt("SUBSPACE_DIMENSION", dimension);
    }

    /*
    fixSubspaceDimension = prefs.getBoolean("FIX_SUBSPACE_DIMENSION", false);
        subspaceDimension = prefs.getInt("SUBSPACE_DIMENSION", 1);
     */
    /**
     * @param blackoutSeconds the blackoutSeconds to set
     */
    public void setBlackoutSeconds(double blackoutSeconds) {
        this.blackoutSeconds = blackoutSeconds;
        prefs.putDouble("BlackoutSeconds", blackoutSeconds);
    }

    /**
     * @return the detectorType
     */
    public DetectorType getDetectorType() {
        return detectorType;
    }

    /**
     * @param detectorType the detectorType to set
     */
    public void setDetectorType(DetectorType detectorType) {
        this.detectorType = detectorType;
        prefs.put("DetectorType", detectorType.toString());
    }

    /**
     * @return the matchedFieldBands
     */
    public int getFFTSize() {
        return fftSize;
    }

    public void setFFTSize(int size) {
        this.fftSize = size;
        prefs.putInt("FFT_SIZE", fftSize);
    }

    /**
     * @return the matchedFieldDesignFactor
     */
    public int getMatchedFieldDesignFactor() {
        return matchedFieldDesignFactor;
    }

    /**
     * @param matchedFieldDesignFactor the matchedFieldDesignFactor to set
     */
    public void setMatchedFieldDesignFactor(int matchedFieldDesignFactor) {
        this.matchedFieldDesignFactor = matchedFieldDesignFactor;
        prefs.putInt("DesignFactor", matchedFieldDesignFactor);
    }

    /**
     * @return the matchedFieldDimension
     */
    public int getMatchedFieldDimension() {
        return matchedFieldDimension;
    }

    /**
     * @param matchedFieldDimension the matchedFieldDimension to set
     */
    public void setMatchedFieldDimension(int matchedFieldDimension) {
        this.matchedFieldDimension = matchedFieldDimension;
        prefs.putInt("MFDimension", matchedFieldDimension);
    }

    /**
     * @return the staDuration
     */
    public double getStaDuration() {
        return staDuration;
    }

    /**
     * @param staDuration the staDuration to set
     */
    public void setStaDuration(double staDuration) {
        this.staDuration = staDuration;
        prefs.putDouble("STADuration", staDuration);
    }

    /**
     * @return the ltaDuration
     */
    public double getLtaDuration() {
        return ltaDuration;
    }

    /**
     * @param ltaDuration the ltaDuration to set
     */
    public void setLtaDuration(double ltaDuration) {
        this.ltaDuration = ltaDuration;
        prefs.putDouble("LTADuration", ltaDuration);
    }

    /**
     * @return the staLtaDelaySeconds
     */
    public double getGapDuration() {
        return gapDuration;
    }

    public void setGapDuration(double duration) {
        this.gapDuration = duration;
        prefs.putDouble("GapDuration", gapDuration);
    }

    /**
     * @return the normalize
     */
    public boolean isNormalizeStatistics() {
        return normalizeStatistics;
    }

    public void setNormalizeStatistics(boolean normalizeStatistics) {
        this.normalizeStatistics = normalizeStatistics;
        prefs.putBoolean("NORMALIZE_STATS", normalizeStatistics);
    }

    public boolean isPrewhitenStatistics() {
        return prewhitenStatistics;
    }

    public void setPrewhitenStatistics(boolean prewhitenStatistics) {
        this.prewhitenStatistics = prewhitenStatistics;
        prefs.putBoolean("PREWHITEN_STATS", prewhitenStatistics);
    }

    /**
     * @return the enableSpawning
     */
    public boolean isEnableSpawning() {
        return enableSpawning;
    }

    /**
     * @param enableSpawning the enableSpawning to set
     */
    public void setEnableSpawning(boolean enableSpawning) {
        this.enableSpawning = enableSpawning;
        prefs.putBoolean("ENABLE_SPAWNING", enableSpawning);
    }

    /**
     * @return the traceLength
     */
    public double getTraceLength() {
        return traceLength;
    }

    /**
     * @param traceLength the traceLength to set
     */
    public void setTraceLength(double traceLength) {
        this.traceLength = traceLength;
        prefs.putDouble("TRACE_LENGTH", traceLength);
    }

    public boolean isFixShiftsToZero() {
        return isFixShiftsToZero;
    }

    public void setFixShiftsToZero(boolean fixShiftsToZero) {
        this.isFixShiftsToZero = fixShiftsToZero;
        prefs.putBoolean("FORCE_ZERO_SHIFTS", isFixShiftsToZero);
    }

    public int getMinDetectionCountForRetrieval() {
        return minDetectionCountForRetrieval;
    }

    public void setMinDetectionCountForRetrieval(int minDetectionCount) {
        minDetectionCountForRetrieval = minDetectionCount;
        prefs.putInt("MIN_DETECTION_COUNT", minDetectionCountForRetrieval);
    }

    public double getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(double value) {
        windowStart = value;
    }

    public double getMinDetStatThreshold() {
        return minDetStatThreshold;
    }

    public void setMinDetStatThreshold(double value) {
        minDetStatThreshold = value;
        prefs.putDouble("MIN_DET_STAT_THRESH", value);
    }

    public double getMaxDetStatThreshold() {
        return maxDetStatThreshold;
    }

    public void setMaxDetStatThreshold(double value) {
        maxDetStatThreshold = value;
        prefs.putDouble("MAX_DET_STAT_THRESH", value);
    }

    public boolean isSuppressBadDetectors() {
        return suppressBadDetectors;
    }

    public void setSuppressBadDetectors(boolean suppressBadDetectors) {
        this.suppressBadDetectors = suppressBadDetectors;
        prefs.putBoolean("SUPPRESS_BAD_DETECTORS", suppressBadDetectors);
    }

    public boolean isFixSubspaceDimension() {
        return fixSubspaceDimension;
    }

    public int getSubspaceDimension() {
        return subspaceDimension;
    }
    
    public boolean isRetrieveByBlocks() {
        return retrieveByBlocks;
    } 
    
    public int getBlockSize() {
        return blockSize;
    }
    
    public void setRetrieveByBlocks(boolean value) {
        retrieveByBlocks = value;
        prefs.putBoolean("RETRIEVE_BY_BLOCKS", value);
    }
    
    public void setBlockSize(int value) {
        blockSize = value;
        prefs.putInt("BLOCK_SIZE", value);
    }
   
}
