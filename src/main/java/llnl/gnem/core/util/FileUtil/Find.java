/**
 * Derived from https://docs.oracle.com/javase/tutorial/essential/io/find.html
 */
package llnl.gnem.core.util.FileUtil;

/**
 * Sample code that finds files that match the specified glob pattern. For more
 * information on what constitutes a glob pattern, see
 * https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
 *
 * The file or directories that match the pattern are printed to standard out.
 * The number of matches is also printed.
 *
 * When executing this application, you must put the glob pattern in quotes, so
 * the shell will not expand any wild cards: java Find . -name "*.java"
 */
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import static java.nio.file.FileVisitResult.*;


public class Find
{
    public static class Finder extends SimpleFileVisitor<Path>
    {

        private final PathMatcher matcher;
        private int numMatches = 0;

        Finder(String pattern)
        {
            matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
        }

        // Compares the glob pattern against
        // the file or directory name.
        void find(Path file)
        {

            Path name = file.getFileName();
            if (name != null && matcher.matches(name))
            {
                numMatches++;
                System.out.println(file);
            }
        }

        // Prints the total number of
        // matches to standard out.
        void done()
        {
            System.out.println("Matched: " + numMatches);
        }

        // Invoke the pattern matching
        // method on each file.
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
        {
            find(file);
            return CONTINUE;
        }

        // Invoke the pattern matching
        // method on each directory.
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
        {
            find(dir);
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc)
        {
            System.err.println(exc);
            return CONTINUE;
        }
    }

    static void usage()
    {
        System.err.println("java Find <path>  -name <glob_pattern>");//-name \"<glob_pattern>\"");
        System.exit(-1);
    }
}
