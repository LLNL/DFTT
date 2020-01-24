package llnl.gnem.core.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that provides utility methods for loading arrays of primitive types
 * for files. Files are assumed to have one value per line and to have no
 * extraneous lines ( comments etc. ).
 */
public class FileInputArrayLoader {

    /**
     * Read the specified text file and return an array of ints with one element
     * per line in the file. The file is assumed to have one int value per line
     * and no empty lines or lines with characters not interpretable as an int.
     *
     * @param filename The name of the file to be read.
     * @return The array of ints read from the file.
     * @throws IOException Exception thrown if there is an error reading the
     * file.
     */
    public static int[] fillIntsFromFile(final String filename) throws IOException {
        String[] intStrings = fillStrings(filename);
        int[] result = new int[intStrings.length];
        for (int j = 0; j < intStrings.length; ++j) {
            result[j] = Integer.parseInt(intStrings[j].trim());
        }

        return result;
    }

    public static String[] fillStrings(InputStream stream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        return fillStrings(in, true);
    }

    /**
     * Read the specified text file and return an array of Strings with one
     * element per line in the file.
     *
     * @param filename The name of the file to be read.
     * @return The array of Strings read from the file.
     * @throws IOException Exception thrown if there is an error reading the
     * file.
     */
    public static String[] fillStrings(final String filename) throws IOException {
        boolean discardEmptyLines = true;
        return fillStrings(filename, discardEmptyLines);
    }

    public static String[] fillStrings(final String filename, boolean discardEmptyLines) throws IOException {
        BufferedReader input = null;
        FileReader file = null;
        try {
            file = new FileReader(filename);
            input = new BufferedReader(file);
            return fillStrings(input, discardEmptyLines);
        } finally {
            if (input != null) {
                input.close();
            }
            if (file != null) {
                file.close();
            }
        }

    }

    private static String[] fillStrings(BufferedReader input, boolean discardEmptyLines) throws IOException {
        List<String> lines = new ArrayList<>();
        try {
            String line;
            while ((line = input.readLine()) != null) {
                if (line.trim().length() > 0 || !discardEmptyLines) {
                    lines.add(line);
                }
            }
            return lines.toArray(new String[0]);
        } catch (IOException x) {
            input.close();
            throw x;
        }
    }
}
