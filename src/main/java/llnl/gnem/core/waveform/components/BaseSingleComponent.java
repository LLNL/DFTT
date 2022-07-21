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
package llnl.gnem.core.waveform.components;

import java.io.IOException;
import java.sql.SQLException;

import org.ojalgo.matrix.Primitive32Matrix;
import org.ojalgo.matrix.Primitive32Matrix.DenseReceiver;

import llnl.gnem.core.dataAccess.dataObjects.ApplicationStationInfo;
import llnl.gnem.core.dataAccess.dataObjects.ComponentKey;
import llnl.gnem.core.dataAccess.dataObjects.StreamEpochInfo;
import llnl.gnem.core.gui.map.stations.StationInfo;
import llnl.gnem.core.seismicData.AbstractEventInfo;
import llnl.gnem.core.traveltime.Point3D;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.PairT;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.Geometry.EModel;
import llnl.gnem.core.waveform.BaseTraceData;
import llnl.gnem.core.waveform.filter.StoredFilter;
import llnl.gnem.core.waveform.responseProcessing.TransferStatus;
import llnl.gnem.core.waveform.responseProcessing.WaveformDataType;
import llnl.gnem.core.waveform.responseProcessing.WaveformDataUnits;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;

/**
 * Created by dodge1 Date: Mar 12, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class BaseSingleComponent {

    /**
     * @return the streamEpochInfo
     */
    public StreamEpochInfo getStreamEpochInfo() {
        return streamEpochInfo;
    }

    private final StationInfo station;
    private ComponentIdentifier identifier;
    private TransferStatus transferStatus;
    private RotationStatus rotationStatus;
    private BaseTraceData traceData;
    private StreamEpochInfo streamEpochInfo;
    private final static double FDSN_DIP_TOLERANCE = 5;

    public BaseSingleComponent(CssSeismogram seis, StreamEpochInfo info, ApplicationStationInfo asi) {
        WaveformDataType dataType = WaveformDataType.counts;
        WaveformDataUnits dataUnits = WaveformDataUnits.unknown;
        traceData = new BaseTraceData(seis, dataType, dataUnits);
        station = asi;
        identifier = new ComponentIdentifier(info.getStreamInfo().getBand(),
                                             info.getStreamInfo().getInstrumentCode(),
                                             info.getStreamInfo().getOrientation(),
                                             info.getStreamInfo().getStreamKey().getLocationCode());
        this.streamEpochInfo = info;
        transferStatus = TransferStatus.UNTRANSFERRED;
        rotationStatus = RotationStatus.UNROTATED;
    }

    public ComponentKey getComponentKey() {
        return new ComponentKey(station.getStationId(),
                                streamEpochInfo.getStreamInfo().getBand(),
                                streamEpochInfo.getStreamInfo().getInstrumentCode(),
                                streamEpochInfo.getStreamInfo().getStreamKey().getLocationCode());
    }

    public ComponentType getComponentType() {
        return new ComponentType(streamEpochInfo.getStreamInfo().getBand().charAt(0), streamEpochInfo.getStreamInfo().getInstrumentCode().charAt(0));
    }

    public BaseSingleComponent(StationInfo station, ComponentIdentifier identifier, TransferStatus transferStatus, RotationStatus rotationStatus, BaseTraceData traceData,
            StreamEpochInfo streamEpochInfo) {

        this.station = station;
        this.identifier = identifier;
        this.transferStatus = transferStatus;
        this.rotationStatus = rotationStatus;
        this.traceData = traceData.newCopy();
        this.streamEpochInfo = new StreamEpochInfo(streamEpochInfo);
    }

    public BaseSingleComponent(BaseSingleComponent other) {
        this(other, new ComponentIdentifier(other.identifier));
    }

    public BaseSingleComponent(BaseSingleComponent other, ComponentIdentifier identifier) {
        station = other.station.newCopy();
        this.identifier = identifier;
        transferStatus = other.transferStatus;
        rotationStatus = other.rotationStatus;
        traceData = other.traceData.newCopy();
        streamEpochInfo = new StreamEpochInfo(other.streamEpochInfo);
    }

    public BaseSingleComponent copy(ComponentType type) {
        return new BaseSingleComponent(this, new ComponentIdentifier(type, identifier.getOrientation(), identifier.getLocid()));
    }

    public long getStationId() {
        return station.getStationId();
    }

    public String getChannelLabel() {
        return String.format("%s", getSeismogram().getStreamKey().toString());
    }

    public StationInfo getStationInfo() {
        return station;
    }

    public Long getArrayId() {
        ApplicationStationInfo asi = station instanceof ApplicationStationInfo ? (ApplicationStationInfo) station : null;
        return asi != null ? asi.getArrayId() : null;
    }

    @Override
    public String toString() {
        String objectId = Integer.toHexString(System.identityHashCode(this));
        return String.format("(BaseSingleComponent(%s): %s (Transfer Status = %s)", objectId, identifier.toString(), transferStatus);
    }

    public Epoch getEpoch() {
        return traceData.getEpoch();
    }

    public void trimTo(Epoch epoch) {
        traceData.trimTo(epoch);
    }

    public void removeTrend() {
        traceData.removeTrend();
    }

    public void removeMean() {
        traceData.removeMean();
    }

    public void applyTaper(double taperPercent) {
        traceData.taper(taperPercent);
    }

    public void convertToType(WaveformDataType newType) throws SQLException, IOException {
        traceData.convertToType(newType);
    }

    public static void verifyComponentsCompatibleForRotation(BaseSingleComponent comp1, BaseSingleComponent comp2) {
        if (!comp1.station.equals(comp2.station)) {
            throw new IllegalStateException("Stations are not the same!");
        }
        if (!comp1.identifier.isCompatibleWith(comp2.identifier)) {
            throw new IllegalStateException(String.format("%s is not compatible with %s!", comp1.identifier, comp2.identifier));
        }
        if (!comp1.isHorizontalComponent() || !comp2.isHorizontalComponent()) {
            throw new IllegalStateException("One or more of the components is not horizontal!");
        }
        if (comp1.traceData.getSampleRate() != comp2.traceData.getSampleRate()) {
            double delta1 = comp1.traceData.getDelta();
            double delta2 = comp2.traceData.getDelta();
            int npts = comp1.getSeismogram().getLength();
            if (Math.abs((delta1 - delta2) * (npts - 1) / delta1) > 1.0) {
                throw new IllegalStateException("Sample rates are not sufficiently close!");
            }
        }
        double startTimeDifference = Math.abs(comp1.traceData.getTime().subtractD(comp2.traceData.getTime()));
        if (startTimeDifference > comp1.traceData.getDelta() / 2) {
            throw new IllegalStateException("Component start times are not the same!");
        }
        if (comp1.traceData.getNsamp() != comp2.traceData.getNsamp()) {
            throw new IllegalStateException("Component lengths are not the same!");
        }
    }

    public static PairT<BaseSingleComponent, BaseSingleComponent> rotateToGcp(PairT<? extends BaseSingleComponent, ? extends BaseSingleComponent> inputComponents, AbstractEventInfo eventInfo) {
        BaseSingleComponent comp1 = inputComponents.getFirst();
        BaseSingleComponent comp2 = inputComponents.getSecond();
        StationInfo station = comp1.getStationInfo();
        double backAzimuth = EModel.getAzimuthWGS84(station.getLat(), station.getLon(), eventInfo.getLat(), eventInfo.getLon());
        if (comp1.getAzimuth() > comp2.getAzimuth() || comp1.getAzimuth() == 360 && comp2.getAzimuth() > 0) {
            BaseSingleComponent tmp = comp1;
            comp1 = comp2;
            comp2 = tmp;
        }
        comp1.rotationStatus = RotationStatus.FAILED_TO_ROTATE;
        comp2.rotationStatus = RotationStatus.FAILED_TO_ROTATE;

        double counterClockwiseRotationAngle = getRotationAngle(backAzimuth, comp1.getAzimuth());
        verifyComponentsCompatibleForRotation(comp1, comp2);

        StreamKey key = comp1.traceData.getStreamKey();

        BaseTraceData traceData1 = comp1.getTraceData();
        BaseTraceData traceData2 = comp2.getTraceData();
        float[] plotData1 = traceData1.getPlotData(); //Usually North
        float[] plotData2 = traceData2.getPlotData(); // Usually East
        Primitive32Matrix rotated = rotate(counterClockwiseRotationAngle, plotData2, plotData1);
        float[] plotRadial = getFloatArray(rotated, 1);
        float[] plotTransverse = getFloatArray(rotated, 0);

        float[] backData1 = traceData1.getPlotData(); //Usually North
        float[] backData2 = traceData2.getPlotData(); // Usually East
        rotated = rotate(counterClockwiseRotationAngle, backData2, backData1);
        float[] backRadial = getFloatArray(rotated, 1);
        float[] backTransverse = getFloatArray(rotated, 0);

        ComponentIdentifier radialIdentifier = new ComponentIdentifier(comp1.identifier.getBand(), comp1.identifier.getInstrument(), "R", comp1.identifier.getLocid());

        String radialChan = radialIdentifier.getChan();
        StreamKey radialKey = new StreamKey(key.getStationKey(), radialChan, key.getLocationCode());
        CssSeismogram radialSeis = new CssSeismogram(traceData1.getIdentifier(),
                                                     radialKey,
                                                     plotRadial,
                                                     traceData1.getSampleRate(),
                                                     traceData1.getTime(),
                                                     traceData1.getCalib(),
                                                     traceData1.getCalper());

        CssSeismogram radialBack = new CssSeismogram(traceData1.getIdentifier(),
                                                     radialKey,
                                                     backRadial,
                                                     traceData1.getSampleRate(),
                                                     traceData1.getTime(),
                                                     traceData1.getCalib(),
                                                     traceData1.getCalper());
        double orientation = createComponentOrientation(comp1, counterClockwiseRotationAngle);
        BaseTraceData radialTraceData = new BaseTraceData(radialSeis, radialBack, traceData1.getDataType(), traceData1.getDataUnits(), traceData1.getCurrentFilter());

        StreamEpochInfo aInfo = comp1.streamEpochInfo;
        StreamEpochInfo rInfo = new StreamEpochInfo(aInfo.getStreamEpochId(),
                                                    aInfo.getStreamInfo(),
                                                    aInfo.getBeginTime(),
                                                    aInfo.getEndTime(),
                                                    aInfo.getDepth(),
                                                    orientation,
                                                    aInfo.getDip(),
                                                    aInfo.getSamprate());
        BaseSingleComponent resultComp1 = new BaseSingleComponent(comp1.getStationInfo(), radialIdentifier, comp1.getTransferStatus(), RotationStatus.ROTATED, radialTraceData, rInfo);

        ComponentIdentifier transverseIdentifier = new ComponentIdentifier(comp1.identifier.getBand(), comp1.identifier.getInstrument(), "T", comp1.identifier.getLocid());

        String transChan = transverseIdentifier.getChan();
        StreamKey transKey = new StreamKey(key.getStationKey(), transChan, key.getLocationCode());
        CssSeismogram transverseSeis = new CssSeismogram(traceData2.getIdentifier(),
                                                         transKey,
                                                         plotTransverse,
                                                         traceData2.getSampleRate(),
                                                         traceData2.getTime(),
                                                         traceData2.getCalib(),
                                                         traceData2.getCalper());

        CssSeismogram transverseBack = new CssSeismogram(traceData2.getIdentifier(),
                                                         transKey,
                                                         backTransverse,
                                                         traceData2.getSampleRate(),
                                                         traceData2.getTime(),
                                                         traceData2.getCalib(),
                                                         traceData2.getCalper());
        orientation = createComponentOrientation(comp2, counterClockwiseRotationAngle);
        BaseTraceData transverseTraceData = new BaseTraceData(transverseSeis, transverseBack, traceData2.getDataType(), traceData2.getDataUnits(), traceData2.getCurrentFilter());
        aInfo = comp2.streamEpochInfo;
        rInfo = new StreamEpochInfo(aInfo.getStreamEpochId(), aInfo.getStreamInfo(), aInfo.getBeginTime(), aInfo.getEndTime(), aInfo.getDepth(), orientation, aInfo.getDip(), aInfo.getSamprate());

        BaseSingleComponent resultComp2 = new BaseSingleComponent(comp2.getStationInfo(), transverseIdentifier, comp2.getTransferStatus(), RotationStatus.ROTATED, transverseTraceData, rInfo);

        return new PairT<>(resultComp1, resultComp2);

    }

    private static double createComponentOrientation(BaseSingleComponent comp, double counterClockwiseRotationAngle) {
        double orientation = comp.getAzimuth() - counterClockwiseRotationAngle;
        if (orientation < 0) {
            orientation += 360;
        }
        if (orientation > 360) {
            orientation -= 360;
        }
        return orientation;
    }

    private static float[] getFloatArray(Primitive32Matrix rotated, int i) {
        float[] result = new float[rotated.getColDim()];
        for (int j = 0; j < result.length; ++j) {
            result[j] = rotated.get(i, j).floatValue();
        }
        return result;
    }

    private static Primitive32Matrix rotate(double rotationAngle, float[] xArray, float[] yArray) {
        if (xArray.length != yArray.length) {
            throw new IllegalStateException("Input arrays do not have the same length!");
        }

        double theta = Math.toRadians(rotationAngle);
        DenseReceiver P = Primitive32Matrix.FACTORY.makeDense(2, 2);
        P.set(0, 0, Math.cos(theta));
        P.set(0, 1, -Math.sin(theta));
        P.set(1, 0, Math.sin(theta));
        P.set(1, 1, Math.cos(theta));
        DenseReceiver from = Primitive32Matrix.FACTORY.makeDense(2, xArray.length);
        for (int j = 0; j < xArray.length; ++j) {
            from.set(0, j, xArray[j]);
            from.set(1, j, yArray[j]);
        }
        return P.get().multiply(from.get());
    }

    /**
     * This method determines the rotation angle in degrees that will rotate
     * component1 to the angle baz. That rotation will make component 1 be a
     * radial component oriented in the direction of travel along the GCP.
     *
     * @param backazimuth
     *            The angle (backAzimuth) to rotate to in degrees measured from
     *            North clockwise.
     * @param comp1Azimuth
     *            The comp1Azimuth angle of the first component measured in
     *            degrees clockwise from North.
     * @return The rotation angle in degrees measured clockwise.
     */
    private static double getRotationAngle(double backazimuth, double comp1Azimuth) {
        double thetaBaz = Math.toRadians(backazimuth);
        double theta1 = Math.toRadians(comp1Azimuth);
        double crossProductNorm = Math.sin(theta1) * Math.cos(thetaBaz) - Math.sin(thetaBaz) * Math.cos(theta1);
        double dotProduct = Math.sin(theta1) * Math.sin(thetaBaz) + Math.cos(theta1) * Math.cos(thetaBaz);
        double thetaOut = Math.acos(dotProduct) * Math.signum(crossProductNorm);
        return Math.toDegrees(thetaOut);
    }

    public ComponentOrientation getOrientation() {
        Double dip = getDip();
        Double azimuth = getAzimuth();
        if (dip != null) {
            if (Math.abs(Math.abs(dip) - 90) <= FDSN_DIP_TOLERANCE) {
                return ComponentOrientation.VERTICAL;
            } else if (Math.abs(dip) <= FDSN_DIP_TOLERANCE && azimuth != null) {
                return ComponentOrientation.HORIZONTAL;
            } else {
                return ComponentOrientation.UNDEFINED;
            }
        } else if (azimuth != null) {
            return ComponentOrientation.HORIZONTAL;
        }

        return ComponentOrientation.UNDEFINED;

    }

    public Double getAzimuth() {
        return streamEpochInfo.getAzimuth();
    }

    public Double getDip() {
        return streamEpochInfo.getDip();
    }

    public CssSeismogram getSeismogram() {
        return traceData.getSeismogram();
    }

    public ComponentIdentifier getIdentifier() {
        return identifier;
    }

    public float[] getSegment(double start, double duration) {
        return traceData.getSegment(start, duration);
    }

    public String getSta() {
        return traceData.getSta();
    }

    public long getWfid() {
        return traceData.getWfid();
    }

    public void updateFrom(BaseSingleComponent other) {
        identifier = new ComponentIdentifier(other.identifier);
        transferStatus = other.getTransferStatus();
        rotationStatus = other.rotationStatus;
        traceData = other.traceData.newCopy();
        streamEpochInfo = new StreamEpochInfo(other.streamEpochInfo);
    }

    public TransferStatus getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(TransferStatus transferStatus) {
        this.transferStatus = transferStatus;
    }

    public RotationStatus getRotationStatus() {
        return rotationStatus;
    }

    public void applyFilter(StoredFilter filter) {
        traceData.applyFilter(filter);
    }

    public void unApplyFilter() {
        traceData.unApplyFilter();
    }

    public void integrate() {
        traceData.integrate();
    }

    public void differentiate() {
        traceData.differentiate();
    }

    StoredFilter getCurrentFilter() {
        return traceData.getCurrentFilter();
    }

    public double estimatePickStdErr(double time) {
        return traceData.estimatePickStdErr(time);
    }

    public String getName() {
        return traceData.getName();
    }

    public boolean isBadTrace() {
        return traceData.isBad();
    }

    public void setDataUnits(WaveformDataUnits units) {
        traceData.setDataUnits(units);
    }

    public void setDataType(WaveformDataType type) {
        traceData.setDataType(type);
    }

    public WaveformDataType getDataType() {
        return traceData.getDataType();
    }

    public WaveformDataUnits getDataUnits() {
        return traceData.getDataUnits();
    }

    public boolean canRemoveInstrumentResponse() {
        return traceData.getDataType() == WaveformDataType.counts;
    }

    public boolean canIntegrate() {
        return traceData.canIntegrate();
    }

    public boolean canDifferentiate() {
        return traceData.canDifferentiate();
    }

    public BaseTraceData getTraceData() {
        return traceData;
    }

    public void setTraceData(BaseTraceData traceData) {
        this.traceData = traceData;
    }

    public void resample(double newRate) {
        traceData.resample(newRate);
    }

    public Integer getFilterid() {
        StoredFilter filter = traceData.getCurrentFilter();
        return filter != null ? filter.getFilterid() : null;
    }

    public Point3D getPoint3D() {
        Double tmp = station.getElevation();
        float elev = (float) (tmp != null ? tmp : -999.0);
        return new Point3D((float) station.getLon(), (float) station.getLat(), elev);
    }

    public boolean isPickAllowable() {
        return true;
    }

    public boolean isMatchingChannel(BaseSingleComponent component) {
        return this.getStationInfo().equals(component.getStationInfo()) && this.getIdentifier().equals(component.getIdentifier());
    }

    public String getShortName() {
        return traceData.getShortName();
    }

    private boolean isHorizontalComponent() {
        return this.getOrientation() == ComponentOrientation.HORIZONTAL;
    }

}
