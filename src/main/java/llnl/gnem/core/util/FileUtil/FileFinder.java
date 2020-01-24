package llnl.gnem.core.util.FileUtil;

import llnl.gnem.core.util.ApplicationLogger;

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
