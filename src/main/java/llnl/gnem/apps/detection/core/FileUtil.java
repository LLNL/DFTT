package llnl.gnem.apps.detection.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;


/**
 *
 * @author dodge1
 */
public class FileUtil {

    public static byte[] readBytesFromFile(String filename) throws FileNotFoundException, IOException {
        FileInputStream infile = null;
        long length = new File(filename).length();
        byte[] array = new byte[(int) length];
        try {
            infile = new FileInputStream(filename);
            long offset = 0;

            int count = infile.read(array, 0, (int) (length - offset));
            if (count != length) {
                throw new IllegalStateException("Failed to read completely file: " + filename);
            }
            return array;
        } finally {
            if (infile != null) {
                infile.close();
            }
        }
    }

    public File createTestFileFromResource(String resourceName) throws IOException, FileNotFoundException {
        InputStream in = null;
        FileOutputStream out = null;
        try {
            File tmp = new File(resourceName);
            String name = tmp.getName();
            in = getClass().getResourceAsStream(resourceName);
            String tmpDir = System.getProperty("java.io.tmpdir");
            File file = new File(tmpDir, name);
            if (file.exists()) {
                file.delete();
            }
            out = new FileOutputStream(file);
            IOUtils.copy(in, out);
            return file;
        } finally {
            in.close();
            out.close();
        }

    }
}
