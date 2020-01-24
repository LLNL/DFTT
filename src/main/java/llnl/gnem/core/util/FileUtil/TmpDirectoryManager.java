package llnl.gnem.core.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.util.FileSystemException;
import llnl.gnem.core.util.FileUtils;
import llnl.gnem.core.util.TimeT;

/**
 * Created by dodge1 Date: Aug 30, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class TmpDirectoryManager {

    private String wfdiscDir;

    public String createWfdiscDir(String tmpDir, String seedFileName) throws IOException {
        wfdiscDir = makeWfdiscDirName(tmpDir, seedFileName);
        return wfdiscDir;
    }

    /**
     * Creates a directory under tmpDir based on a concatenation of the seed
     * file name and the current time
     *
     * @param tmpDir The name of the scratch directory where rdseed will unpack
     * data.
     * @param seedFileName The name of the seed file being processed
     * @return The complete path name
     * @throws IOException Thrown on creation error.
     */
    private static String makeWfdiscDirName(String tmpDir, String seedFileName) throws IOException {
        TimeT time = new TimeT();
        String y = String.valueOf(time.getYear());
        String d = String.valueOf(time.getDayOfYear());
        String h = String.valueOf(time.getHour());
        String m = String.valueOf(time.getMinute());
        StringBuilder newName = new StringBuilder();
        newName.append(tmpDir).append(System.getProperty("file.separator"));
        newName.append(seedFileName);
        newName.append(y).append(d).append(h).append(m);
        File newDir = new File(newName.toString());
        if (newDir.exists()) {
            FileUtils.deleteDirectory(newDir);
        }

        if (!newDir.mkdir()) {
            throw new FileSystemException("Failed creating tmp directory!");
        }
        return newName.toString();
    }

    public boolean deleteWfdiscDirectory() throws IOException {
        File path = new File(wfdiscDir);
        return FileUtils.deleteDirectory(path);
    }

    /**
     * Returns the fully-qualified names of all the wfdisc files created by
     * rdseed
     *
     * @return A collection containing the fully-qualified names of the wfdisc
     * files to process.
     * @throws FileNotFoundException When no wfdisc files have been produced by
     * rdseed.
     */
    public Collection<String> getWfdiscFileList() throws FileNotFoundException {
        Collection<String> extensions = new ArrayList<String>();
        extensions.add("wfdisc");
        SourceFileFilter filter = new SourceFileFilter(extensions);
        return FileFinder.getFileListing(new File(wfdiscDir), filter);
    }

    public String getWfdiscDir() {
        return wfdiscDir;
    }
}
