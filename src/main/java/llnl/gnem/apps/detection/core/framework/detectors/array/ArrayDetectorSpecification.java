package llnl.gnem.apps.detection.core.framework.detectors.array;

import llnl.gnem.apps.detection.core.dataObjects.AbstractSpecification;
import llnl.gnem.apps.detection.core.dataObjects.ArrayConfiguration;
import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
import llnl.gnem.apps.detection.core.dataObjects.SlownessSpecification;
import llnl.gnem.apps.detection.core.dataObjects.TriggerPositionType;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import llnl.gnem.core.util.StreamKey;

public class ArrayDetectorSpecification extends AbstractSpecification implements Serializable {

    private static final long serialVersionUID = -7768540165481026013L;

    private final float                 STADuration;
    private final float                 LTADuration;
    private final float                 gapDuration;
    private final boolean               spawnOnTriggers;
    private final SlownessSpecification slownessSpecification;
    private final ArrayConfiguration    geometry;

    public static ArrayDetectorSpecification createFromDatabase( float                  threshold,
                                                                 float                  blackoutPeriod,
                                                                 ArrayList< StreamKey> staChanList,
                                                                 float                  STADuration,
                                                                 float                  LTADuration,
                                                                 float                  gapDuration,
                                                                 boolean                enableSpawning,
                                                                 float                  backAzimuth,
                                                                 float                  velocity,
                                                                 String                 arrayName,
                                                                 String                 siteTable,
                                                                 int                    jdate   ) throws IOException, ParseException, SQLException {
        ArrayConfiguration config = ArrayConfiguration.createFromDatabase(arrayName, siteTable, jdate);
        return new ArrayDetectorSpecification( threshold,
                                               blackoutPeriod,
                                               staChanList,
                                               STADuration, 
                                               LTADuration, 
                                               gapDuration, 
                                               enableSpawning,
                                               backAzimuth, 
                                               velocity, 
                                               config           );
    }

    public static ArrayDetectorSpecification createFromFileAndDatabase( String specFile, String siteTable, int jdate ) throws IOException, ParseException, SQLException {
        try (FileInputStream stream = new FileInputStream(specFile)) {
            return new ArrayDetectorSpecification(stream, true, siteTable, jdate);
        }

    }

    public ArrayDetectorSpecification(InputStream stream, 
            boolean createGeomFromDb,
            String siteTable,
            int jdate) throws IOException, ParseException, SQLException {

        super(stream);

        STADuration = Float.parseFloat(parameterList.getProperty("STADuration", "4.0"));
        LTADuration = Float.parseFloat(parameterList.getProperty("LTADuration", "40.0"));
        gapDuration = Float.parseFloat(parameterList.getProperty("gapDuration", "2.0"));

        spawnOnTriggers = Boolean.parseBoolean(parameterList.getProperty("enableSpawning", "true"));


       
        float baz = Float.parseFloat(parameterList.getProperty("backAzimuth", "0.0"));
        float vel = Float.parseFloat(parameterList.getProperty("velocity", "99999.0"));
        
        
        slownessSpecification = new SlownessSpecification(vel,baz);
        

        triggerPositionType = TriggerPositionType.THRESHOLD_EXCEED_POINT;
        detectorType = DetectorType.ARRAYPOWER;


        geometry = createGeomFromDb ? createFromDb(siteTable, jdate) : createGeometry();
        verifyChannelConsistency();
    }

    /*
     * Constructor to support instantiation of detectors stored in the database.
     */
    private ArrayDetectorSpecification( float                  threshold,
                                        float                  blackoutPeriod,
                                        ArrayList< StreamKey> staChanList,
                                        float                  STADuration,
                                        float                  LTADuration,
                                        float                  gapDuration,
                                        boolean                enableSpawning,
                                        float                  backAzimuth,
                                        float                  velocity,
                                        ArrayConfiguration     config ) throws IOException, ParseException {

        super( threshold, blackoutPeriod, staChanList );
        geometry = config;
        this.STADuration = STADuration;
        this.LTADuration = LTADuration;
        this.gapDuration = gapDuration;

        spawnOnTriggers = enableSpawning;


        slownessSpecification = new SlownessSpecification(velocity,backAzimuth);


        triggerPositionType = TriggerPositionType.THRESHOLD_EXCEED_POINT;
        detectorType = DetectorType.ARRAYPOWER;
    }

    public float getSTADuration() {
        return STADuration;
    }

    public float getLTADuration() {
        return LTADuration;
    }

    public float getGapDuration() {
        return gapDuration;
    }

    @Override
    public boolean spawningEnabled() {
        return spawnOnTriggers;
    }

    public float[] getSlownessVector() {
        return slownessSpecification.getSlownessVector();
    }

    public ArrayConfiguration getArrayConfiguration() {
        return geometry;
    }

    public static void printSpecificationTemplate(PrintStream ps) {

        AbstractSpecification.printSpecificationTemplate(ps);

        ps.println("STADuration    =  <duration (sec)>");
        ps.println("LTADuration    =  <duration (sec)>");
        ps.println("gapDuration    =  <duration (sec)>");
        ps.println("enableSpawning =  false");
        ps.println("backAzimuth    =  <backazimuth (degrees east of north)>");
        ps.println("velocity       =  <velocity (km/sec)>");
        ps.println("arrayDefinitions = <CSS_SiteFile> <arrayName> <jdate>");
        ps.println("detectorType     = ArrayPower");
    }

    @Override
    public void printSpecification(PrintStream ps) {

        super.printSpecification(ps);
        ps.println();

        ps.println("STADuration     = " + STADuration);
        ps.println("LTADuration     = " + LTADuration);
        ps.println("gapDuration     = " + gapDuration);
        ps.println("spawning enabled: " + spawnOnTriggers);
        ps.println("slowness vector:  " + slownessSpecification);
        ps.println("array geometry = " + geometry.getArrayName());
    }

    private ArrayConfiguration createGeometry() throws NumberFormatException, ParseException, IOException {
        String arrayDefinitions = parameterList.getProperty("arrayDefinitions");
        String[] tokens = arrayDefinitions.split("\\s+");
        String CSS_SiteFile = tokens[0].trim();
        String arrayName = tokens[1].trim();
        int jdate = Integer.parseInt(tokens[2]);


        return ArrayConfiguration.createFromFlatfile(arrayName, CSS_SiteFile, jdate);
    }

    private void verifyChannelConsistency() {
        for( StreamKey sck : this.getStaChanList()){
            if( !geometry.hasElement(sck.getSta())){
                throw new IllegalStateException("Element: "+sck+ " not found in geometry!");
            }
        }
    }

    private ArrayConfiguration createFromDb(String siteTable, int jdate) throws SQLException, IOException, ParseException {
        return ArrayConfiguration.createFromDatabase(getStaChanList(), siteTable, jdate);
    }

    public double getBackAzimuth() {
        return slownessSpecification.getBackAzimuth();
    }

    public double getVelocity() {
        return slownessSpecification.getVelocity();
    }

    SlownessSpecification getSlownessSpecification() {
        return slownessSpecification;
    }
    
    
    @Override
    public boolean isArraySpecification() {
        return true;
    }

}