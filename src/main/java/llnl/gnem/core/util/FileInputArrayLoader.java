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
