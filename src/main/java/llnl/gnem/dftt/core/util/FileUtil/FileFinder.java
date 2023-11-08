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
package llnl.gnem.dftt.core.util.FileUtil;

import llnl.gnem.dftt.core.util.ApplicationLogger;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by dodge1 Date: May 11, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class FileFinder {

    private FileFinder() {
    }

    public static Collection<String> findMatchingFiles(String fileName, Collection<String> extension, File directory) throws FileNotFoundException {
        Collection<String> matchingFiles = new ArrayList<String>();
        if (fileName.equals("$")) {
            ApplicationLogger.getInstance().log(Level.INFO, String.format("Looking for files below directory %s...", directory.getAbsolutePath()));
            SourceFileFilter filter = new SourceFileFilter(extension);
            matchingFiles.addAll(getFileListing(directory, filter));
            if (matchingFiles.isEmpty()) {
                ApplicationLogger.getInstance().log(Level.SEVERE, "No files found below current directory!");
            }
        } else {
            matchingFiles.add(fileName);
        }
        return matchingFiles;
    }

    public static Collection<String> findMatchingFilesUsingRegex(String regex, File directory) throws FileNotFoundException {
        Collection<String> matchingFiles = new ArrayList<>();
        ApplicationLogger.getInstance().log(Level.INFO, String.format("Looking for files below directory %s...", directory.getAbsolutePath()));
        FilenameFilter filter = new PatternFilter(regex);
        matchingFiles.addAll(getFileListing(directory, filter));
        if (matchingFiles.isEmpty()) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "No files found below current directory!");
        }

        return matchingFiles;
    }

    static public List<String> getFileListing(File startDir, FileFilter filter) throws FileNotFoundException {
        String path = startDir.getAbsolutePath();
        if (path.endsWith(".")) {
            path = path.substring(0, path.length() - 1);
        }
        File trimmedFile = new File(path);
        validateDirectory(trimmedFile);
        List<File> result = getFileListingNoSort(trimmedFile);

        List<String> filtered = new ArrayList<>();
        for (File file : result) {
            if (file.isFile() && filter.accept(file)) {
                filtered.add(file.getAbsolutePath());
            }
        }
        return filtered;
    }

    static public List<String> getFileListing(File startDir, FilenameFilter filter) throws FileNotFoundException {
        String path = startDir.getAbsolutePath();
        File trimmedFile = new File(path);
        validateDirectory(trimmedFile);
        List<File> result = getFileListingNoSort(trimmedFile);

        List<String> filtered = new ArrayList<>();
        for (File file : result) {
            if (file.isFile()) {
                String parent = file.getParent();
                String name = file.getName();
                if (filter.accept(new File(parent), name)) {
                    filtered.add(file.getAbsolutePath());
                }
            }
        }
        return filtered;
    }

    static private List<File> getFileListingNoSort(File aStartingDir) throws FileNotFoundException {
        List<File> result = new ArrayList<>();
        File[] filesAndDirs = aStartingDir.listFiles();
        if (filesAndDirs != null && filesAndDirs.length > 0) {
            List<File> filesDirs = Arrays.asList(filesAndDirs);
            for (File file : filesDirs) {
                result.add(file); //always add, even if directory
                if (!file.isFile()) {
                    //must be a directory
                    //recursive call!
                    List<File> deeperList = getFileListingNoSort(file);
                    result.addAll(deeperList);
                }
            }
        }
        return result;
    }

    static private void validateDirectory(File directory) throws FileNotFoundException {
        if (directory == null) {
            throw new IllegalArgumentException("Directory should not be null.");
        }
        if (!directory.exists()) {
            throw new FileNotFoundException("Directory does not exist: " + directory);
        }
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Is not a directory: " + directory);
        }
        if (!directory.canRead()) {
            throw new IllegalArgumentException("Directory cannot be read: " + directory);
        }
    }
}
