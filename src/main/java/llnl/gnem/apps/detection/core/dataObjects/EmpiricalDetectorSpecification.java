package llnl.gnem.apps.detection.core.dataObjects;
// Interface specifying information required to construct an empirical detector ( SubspaceDetector)
// from event waveform data
public interface EmpiricalDetectorSpecification extends DetectorSpecification {

    public String[]               getEventDirectoryList();          // String[] containing names of events used to construct detector template

    public double                 getOffsetSecondsToWindowStart();         // Start time of template as offset (seconds) from start of files

    public double                 getWindowDurationSeconds();      // Duration of template (seconds)
}
