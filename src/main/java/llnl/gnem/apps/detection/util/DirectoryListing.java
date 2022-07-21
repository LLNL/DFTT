/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
package llnl.gnem.apps.detection.util;
import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirectoryListing {

    private String directoryName;
    private ArrayList<String> subdirectories;
    private ArrayList<String> files;

    class PatternFilter implements FilenameFilter {

        private Pattern pattern;

        public PatternFilter(String regex) {
            pattern = Pattern.compile(regex);
        }

        @Override
        public boolean accept(File arg0, String arg1) {
            Matcher m = pattern.matcher(arg1);
            return m.matches();
        }
    }

    /**
     * @param path
     *            Fully qualified path to directory of interest
     * @param regex
     *            String specifying regular expression to define
     */
    public DirectoryListing(String path, String regex) {

        directoryName = path;

        subdirectories = new ArrayList<>();
        files = new ArrayList<>();

        File directory = new File(path);
        String[] directoryItems = directory.list(new PatternFilter(regex));
        if (directoryItems != null) {
            for (String item : directoryItems) {
                File itemFile = new File(path, item);
                if (itemFile.isDirectory()) {
                    subdirectories.add(item);
                } else if (itemFile.isFile()) {
                    files.add(item);
                }
            }
        }

    }

    public int nSubdirectories() {
        return subdirectories.size();
    }

    public int nFiles() {
        return files.size();
    }

    public String subDirectory(int index) {
        return subdirectories.get(index);
    }

    public String file(int index) {
        return files.get(index);
    }

    public void print(PrintStream ps) {
        ps.println("Directory:  " + directoryName + "\n");
        ps.println("  Subdirectories: ");
        for (String name : subdirectories) {
            ps.println("    " + name);
        }
        ps.println();
        ps.println("  Files: ");
        for (String name : files) {
            ps.println("    " + name);
        }

    }

}