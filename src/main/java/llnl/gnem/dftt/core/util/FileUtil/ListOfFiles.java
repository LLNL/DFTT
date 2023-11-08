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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * from java.sun.com/docs/books/tutorial/essential/io/catstreams.html
 * Use to coordinate a list of files
 */
public class ListOfFiles implements Enumeration<FileInputStream>
{
    private String[] listOfFiles;
    private int current = 0;

    /**
     * creates a list of files given an Array of Strings
     * @param listOfFiles
     */
    public ListOfFiles(String[] listOfFiles)
    {
        this.listOfFiles = listOfFiles;
    }

    /**
     * creates a list of the file paths for a Vector of File objects
     * @param list - a Vector of File objects
     */
    public ListOfFiles(Vector<File> list)
    {
        int size = list.size();
        String [] listarray = new String[size];//= {{"test" }; {"test1"}};//new String[list.size()];

        for (int ii = 0; ii < listarray.length; ii++)
        {
            listarray[ii] = list.elementAt(ii).getAbsolutePath();
        }

        this.listOfFiles = listarray;
    }

    @Override
    public boolean hasMoreElements()
    {
        if (current < listOfFiles.length)
                return true;
        else
                return false;
    }

    @Override
    public FileInputStream nextElement()    // return a FileInputStream
    {
        FileInputStream in = null;

        if (!hasMoreElements())
            throw new NoSuchElementException("No more files.");
        else
        {
            String nextElement = listOfFiles[current];
            current++;
            try
            {
                in = new FileInputStream(nextElement);
            }
            catch (FileNotFoundException e)
            {
                System.err.println("ListOfFiles: Can't open " + nextElement);
            }
        }
        return in;
    }
}
