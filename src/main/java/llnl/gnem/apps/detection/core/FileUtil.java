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
package llnl.gnem.apps.detection.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    public void writeArrayAsTextFile(float[] data, String filename){
        try(PrintWriter pw = new PrintWriter(filename)){
            for(float v : data){
                pw.println(v);
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
