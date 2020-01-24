package llnl.gnem.apps.detection.statistics.fileWriting;


import java.io.File;
import llnl.gnem.core.util.FileSystemException;



public abstract class BaseFileWriter implements StatFileWriter{

	protected final File file;
	public BaseFileWriter( File file ){
		this.file  = file;
	}
    @Override
    public void initialize() throws FileSystemException {
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                throw new FileSystemException(String.format("Failed to delete pre-existing file (%s)!", file.getAbsolutePath()));
            }
        }
    }
	
}
