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
package llnl.gnem.apps.detection.sdBuilder.configuration;

import java.util.prefs.Preferences;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;

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
    private boolean capSubspaceDimension;
    private boolean displayNewTemplates;
    private DetectorCreationOption detectorCreationOption;
    private boolean displayAllStationPredictedPicks;
    private boolean displayAllStationEventIDs;
    private boolean displayAllStationDetectionMarkers;
    private double fKMaxSlowness;
    private int fKNumSlowness;
    private double fKMinFrequency;
    private double fKMaxFrequency;
    private double rWFloorFactor;
    private double rWSNRThreshold;
    private boolean refineWindow;
    private double rWAnalysisWindowLength;
    private double rWMinimumWindowLength;

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
        capSubspaceDimension = prefs.getBoolean("CAP_SUBSPACE_DIMENSION", false);
        subspaceDimension = prefs.getInt("SUBSPACE_DIMENSION", 1);
        autoApplyFilter = prefs.getBoolean("AUTO_APPLY_FILTER", false);
        requireWindowPositionConfirmation = prefs.getBoolean("REQUIRE_WINDOW_POSITION_CONFIRMATION", true);
        showCorrelationWindow = prefs.getBoolean("SHOW_CORRELATION_WINDOW", true);
        retrieveByBlocks = prefs.getBoolean("RETRIEVE_BY_BLOCKS", false);
        blockSize = prefs.getInt("BLOCK_SIZE", 100);
        displayNewTemplates = prefs.getBoolean("DISPLAY_NEW_TEMPLATES", true);
        detectorCreationOption = DetectorCreationOption.valueOf(prefs.get("DETECTOR_CREATION_OPTION", "PROMPT"));
        displayAllStationPredictedPicks = prefs.getBoolean("DISPLAY_ALL_STATION_PREDICTED_PICKS", false);
        displayAllStationEventIDs = prefs.getBoolean("DISPLAY_ALL_STATION_EVENTID_MARKERS", false);
        displayAllStationDetectionMarkers = prefs.getBoolean("DISPLAY_ALL_STATION_DETECTION_MARKERS", false);
        fKMaxSlowness = prefs.getDouble("FK_MAX_SLOWNESS", 0.5);
        fKNumSlowness = prefs.getInt("FK_NUM_SLOWNESS", 100);
        fKMinFrequency = prefs.getDouble("FK_MIN_FREQUENCY", 0.5);
        fKMaxFrequency = prefs.getDouble("FK_MAX_FREQUENCY", 8.0);

        rWFloorFactor = prefs.getDouble("REFINE_WINDOW_FLOOR_FACTOR", 2.0);
        rWSNRThreshold = prefs.getDouble("REFINE_WINDOW_SNR_THRESHOLD", 1.5);
        refineWindow = prefs.getBoolean("DO_REFINE_WINDOW", true);
        rWAnalysisWindowLength = prefs.getDouble("REFINE_WINDOW_ANALYSIS_WINDOW_LENGTH", 10.0);
        rWMinimumWindowLength = prefs.getDouble("REFINE_WINDOW_MIN_WINDOW_LENGTH", 30.0);

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

    public void setShowCorrelationWindow(boolean value) {
        showCorrelationWindow = value;
        prefs.putBoolean("SHOW_CORRELATION_WINDOW", showCorrelationWindow);
    }

    public boolean isCapSubspaceDimension() {
        return capSubspaceDimension;
    }

    public void setCapSubspaceDimension(boolean value) {
        this.capSubspaceDimension = value;
        prefs.putBoolean("CAP_SUBSPACE_DIMENSION", value);
    }

    public boolean isDisplayNewTemplates() {
        return displayNewTemplates;
    }

    public void setDisplayNewTemplates(boolean value) {
        this.displayNewTemplates = value;
        prefs.putBoolean("DISPLAY_NEW_TEMPLATES", value);
    }

    public DetectorCreationOption getDetectorCreationOption() {
        return detectorCreationOption;
    }

    public void setDetectorCreationOption(DetectorCreationOption value) {
        detectorCreationOption = value;
        prefs.put("DETECTOR_CREATION_OPTION", value.toString());
    }

    public boolean isDisplayAllStationPredictedPicks() {
        return displayAllStationPredictedPicks;
    }

    public void setDisplayAllStationPredictedPicks(boolean value) {
        displayAllStationPredictedPicks = value;
        prefs.putBoolean("DISPLAY_ALL_STATION_PREDICTED_PICKS", value);
    }

    public boolean isDisplayAllStationEventIDs() {
        return displayAllStationEventIDs;
    }

    public void setDisplayAllStationEventIDs(boolean value) {
        displayAllStationEventIDs = value;
        prefs.putBoolean("DISPLAY_ALL_STATION_EVENTID_MARKERS", value);
    }

    public boolean isDisplayAllStationDetectionMarkers() {
        return displayAllStationDetectionMarkers;
    }

    public void setDisplayAllStationDetectionMarkers(boolean value) {
        displayAllStationDetectionMarkers = value;
        prefs.putBoolean("DISPLAY_ALL_STATION_DETECTION_MARKERS", value);
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

    public double getFKMaxSlowness() {
        return fKMaxSlowness;
    }

    public void setFKMaxSlowness(double value) {
        this.fKMaxSlowness = value;
        prefs.putDouble("FK_MAX_SLOWNESS", fKMaxSlowness);
    }

    public int getFKNumSlowness() {
        return fKNumSlowness;
    }

    public void setFKNumSlowness(int value) {
        fKNumSlowness = value;
        prefs.putInt("FK_NUM_SLOWNESS", fKNumSlowness);
    }

    public double getFKMinFrequency() {
        return fKMinFrequency;
    }

    public void setFKMinFrequency(double value) {
        this.fKMinFrequency = value;
        prefs.putDouble("FK_MIN_FREQUENCY", fKMinFrequency);
    }

    public double getFKMaxFrequency() {
        return fKMaxFrequency;
    }

    public void setKMaxFrequency(double value) {
        this.fKMaxFrequency = value;
        prefs.putDouble("FK_MAX_FREQUENCY", fKMaxFrequency);
    }

    public double getFloorFactor() {
        return rWFloorFactor;
    }

    public void setRWFloorFactor(double value) {
        this.rWFloorFactor = value;
        prefs.putDouble("REFINE_WINDOW_FLOOR_FACTOR", rWFloorFactor);
    }

    public double getSNRThreshold() {
        return rWSNRThreshold;
    }

    public void setRWSNRThreshold(double value) {
        this.rWSNRThreshold = value;
        prefs.putDouble("REFINE_WINDOW_SNR_THRESHOLD", rWSNRThreshold);
    }

    public boolean isRefineWindow() {
        return refineWindow;
    }

    public void setRefineWindow(boolean value) {
        this.refineWindow = value;
        prefs.getBoolean("DO_REFINE_WINDOW", value);
    }

    public double getAnalysisWindowLength() {
        return rWAnalysisWindowLength;
    }

    public void setRWAnalysisWindowLength(double value) {
        this.rWAnalysisWindowLength = value;
        prefs.putDouble("REFINE_WINDOW_ANALYSIS_WINDOW_LENGTH", rWAnalysisWindowLength);
    }

    public double getMinimumWindowLength() {
        return rWMinimumWindowLength;
    }

    public void setRWMinimumWindowLength(double value) {
        this.rWMinimumWindowLength = value;
        prefs.putDouble("REFINE_WINDOW_MIN_WINDOW_LENGTH", rWMinimumWindowLength);
    }

}
