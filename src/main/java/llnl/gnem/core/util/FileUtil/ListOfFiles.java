package llnl.gnem.core.util.FileUtil;

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
