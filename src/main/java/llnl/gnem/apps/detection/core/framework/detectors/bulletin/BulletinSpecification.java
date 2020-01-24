package llnl.gnem.apps.detection.core.framework.detectors.bulletin;

import java.io.IOException;
import java.util.ArrayList;

import llnl.gnem.apps.detection.core.dataObjects.AbstractSpecification;
import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
import llnl.gnem.apps.detection.core.dataObjects.TriggerPositionType;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import llnl.gnem.core.util.StreamKey;

public class BulletinSpecification extends AbstractSpecification implements Serializable {

    private static final long serialVersionUID = -6247840179189171384L;

    private final boolean spawnOnTriggers;
    private final Bulletin bulletin;

    public static BulletinSpecification getSpecificationFromFile(String filename) throws FileNotFoundException, IOException {
        try (FileInputStream stream = new FileInputStream(filename)) {
            return new BulletinSpecification(stream);
        }
    }

    public BulletinSpecification(InputStream stream) throws IOException {

        super(stream);

        spawnOnTriggers = Boolean.parseBoolean(parameterList.getProperty("enableSpawning", "true"));

        triggerPositionType = TriggerPositionType.THRESHOLD_EXCEED_POINT;
        detectorType = DetectorType.BULLETIN;
        String bulletinFile = parameterList.getProperty("BulletinFile", "");
        if (bulletinFile != null && !bulletinFile.isEmpty()) {
            try{
            bulletin = readBulletinFile(bulletinFile);
            }
            catch(Exception ex){
                System.out.println("");
                throw(ex);
            }
        } else {
            throw new IllegalStateException("No valid bulletin file name was specified!");
        }
    }

    /*
     * Constructor to support instantiation of detectors stored in the database.
     */

    public BulletinSpecification(float threshold,
            float blackoutPeriod,
            ArrayList< StreamKey> staChanList,
            Bulletin bulletin,
            boolean enableSpawning) {

        super(threshold, blackoutPeriod, staChanList);

        spawnOnTriggers = enableSpawning;

        triggerPositionType = TriggerPositionType.THRESHOLD_EXCEED_POINT;
        detectorType = DetectorType.BULLETIN;
        this.bulletin = bulletin;
    }

    @Override
    public boolean spawningEnabled() {
        return spawnOnTriggers;
    }

    public static void printSpecificationTemplate(PrintStream ps) {

        AbstractSpecification.printSpecificationTemplate(ps);

        ps.println("enableSpawning = false");
        ps.println("detectorType   = BULLETIN");
    }

    @Override
    public void printSpecification(PrintStream ps) {

        super.printSpecification(ps);

        ps.println();
        ps.println("spawning enabled: " + spawnOnTriggers);
    }

    /**
     * @return the bulletin
     */
    public Bulletin getBulletin() {
        return bulletin;
    }

    private Bulletin readBulletinFile(String bulletinFile) throws IOException {
        Path path = Paths.get(bulletinFile);

        ArrayList<BulletinRecord> records = new ArrayList<>();

        Stream<String> lines = Files.lines(path);
        lines.forEach((String str) -> {
            processLine(str, records);
        });
        return new Bulletin(records);
    }

    private void processLine(String str, ArrayList<BulletinRecord> records) {
        String[] tokens = str.trim().split("\\s+");
        if (tokens.length != 7) {
            throw new IllegalStateException("Expected line with 7 tokens but got: " + str);
        }
        int j = 0;
        int evid = Integer.parseInt(tokens[j++]);
        double lat = Double.parseDouble(tokens[j++]);
        double lon = Double.parseDouble(tokens[j++]);
        double time = Double.parseDouble(tokens[j++]);
        double depth = Double.parseDouble(tokens[j++]);
        double mw = Double.parseDouble(tokens[j++]);
        double ptime = Double.parseDouble(tokens[j++]);
        records.add(new BulletinRecord(evid, lat, lon, time, depth, mw, ptime));
    }

    @Override
    public boolean isArraySpecification() {
        return false;
    }

}