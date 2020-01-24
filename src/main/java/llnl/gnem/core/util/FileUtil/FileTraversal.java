/*
 * Recursive file listing in java
 * 
 * reference: http vafer.org/blog/20071112204524/
 * 
 * It turns out that when you search on the web for recursive file java you 
 * only find horrible examples on how to implement directory traversal. 
 * Its such a simple algorithm but I feel obliged to provide a better example 
 * to the world. In fact directory traversal can be very elegantly be hidden by 
 * using anonymous classes. You just need to extend the following class
 * and open the template in the editor.
 * 
 */
package llnl.gnem.core.util.FileUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

public class FileTraversal
{
    public final void traverse(final File f) throws IOException
    {
        if (f.isDirectory())
        {
            onDirectory(f);
            final File[] filelist = f.listFiles();
            for (File file : filelist)
            {
                traverse(file);
            }
            return;
        }
        onFile(f);
    }

    public void onDirectory(final File d)
    {
    }

    public void onFile(final File f)
    {
    }

    public static void createRecursiveFileListing(final File startingdirectory, final String directoryfilter, final String filterstring)
    {
        try
        {
            //Preferences prefs = Preferences.getInstance();
            //final File directory = FileManager.selectDirectory(startingdirectory, null);

            // TODO parse the filterstring here and ensure that each file contains the required elements
            Vector<String> filters = getStringTokens(filterstring, "* \t\n\r\f");// use '*' as a wildcard
            StringBuffer filternamesb = new StringBuffer(".");
            for (String filter : filters)
            {
                filternamesb.append("_" + filter);
            }

            final String filternames = filternamesb.toString();
            File multiplelistfile = new File(startingdirectory, directoryfilter.trim() + filternames + ".Multiple.filelist");
            final BufferedWriter multiplefilelistwriter = FileManager.getBufferedWriter(multiplelistfile);

            new FileTraversal()
            {
                int prefix = 0;

                public void onDirectory(final File f)
                {
                    if (f.getName().equals(directoryfilter))
                    {
                        prefix = prefix + 1;
                        File outputfile = new File(startingdirectory, prefix + "." + directoryfilter.trim() + filternames + ".filelist");

                        try
                        {
                            final BufferedWriter filelistwriter = FileManager.getBufferedWriter(outputfile);
                            FileManager.writeLine(multiplefilelistwriter, outputfile.getPath());

                            final File[] fileslist = f.listFiles();
                            for (File file : fileslist)
                            {
                                if (!file.isDirectory())
                                {
                                    // TODO parse the filterstring here and ensure that each file contains the required elements
                                    Vector<String> filters = getStringTokens(filterstring, "* \t\n\r\f");// use '*' as a wildcard

                                    String filename = file.getName();
                                    boolean includefile = true;

                                    // ensure that the file contains the all the desired filters if any are listed
                                    for (String thisfilter : filters)
                                    {
                                        if ((includefile) && (filename.contains(thisfilter)))
                                        {
                                            // continue to check filters
                                            //System.out.println(thisfilter);
                                        }
                                        else
                                        {
                                            includefile = false;
                                        }
                                    }

                                    if (includefile)
                                    {
                                        System.out.println(file);
                                        FileManager.writeLine(filelistwriter, file.getAbsolutePath());
                                    }
                                }
                            }

                            filelistwriter.close();
                        }
                        catch (Exception e)
                        {
                        }
                    }
                }
            }.traverse(startingdirectory);

            multiplefilelistwriter.close();
            System.out.println(multiplelistfile.getAbsolutePath() + " written");
        }
        catch (Exception e)
        {
            System.out.println("Error thrown traversing file: " + e);
        }
    }

    /**
     * @param string
     */
    public static Vector<String> getStringTokens(String string)
    {
        return getStringTokens(string, " \t\n\r\f");
    }

    /**
     * @param string
     */
    public static Vector<String> getStringTokens(String string, String delimiters)
    {
        StringTokenizer st = new StringTokenizer(string, delimiters);
        Vector tokens = new Vector<String>();// Vector <String> tokens = new Vector <String>();

        int num = st.countTokens();
        for (int ii = 0; ii < num; ii++)
        {
            tokens.add(st.nextToken());
        }
        return tokens;
    }
}
