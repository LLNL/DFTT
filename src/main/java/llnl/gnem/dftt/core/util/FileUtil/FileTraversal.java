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
