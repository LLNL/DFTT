package llnl.gnem.core.util.FileUtil;

/*************************************************************************
 * File Name: WildcardSearch.java
 * Date: Jan 9, 2004
 *
 * This class will search all files in a directory using the
 * asterisk (*) and/or question mark (?) as wildcards which may be
 * used together in the same file name.  A File [] is returned containing
 * an array of all files found that match the wildcard specifications.
 *
 * Command line example:
 * c:\>java WildcardSearch c:\windows s??t*.ini
 * New sWild: s.{1}.{1}t.*.ini
 * system.ini
 *
 * Command line break down: Java Program = java WildcardSearch
 *                          Search Directory (arg[0]) = C:\Windows
 *                          Files To Search (arg[1]) = s??t*.ini
 *
 * Note:  Some commands will not work from the command line for arg[1]
 *        such as *.*, however, this will work if you if it is passed
 *        within Java (hard coded)
 *
 * @author kmportner
 **************************************************************************/
import java.io.File;
import java.io.FilenameFilter;

public class WildcardSearch
{
    private static String sWild = "";


    /**
     * Checks for * and ? in the wildcard variable and replaces them correct
     * pattern characters.
     *
     * @param wild - Wildcard name containing * and ?
     * @return - String containing modified wildcard name
     */
    private static String replaceWildcards(String wild)
    {
        StringBuffer buffer = new StringBuffer();

        char[] chars = wild.toCharArray();

        for (int i = 0; i < chars.length; ++i)
        {
            if (chars[i] == '*')
                buffer.append(".*");
            else if (chars[i] == '?')
                buffer.append(".");
            else if ("+()^$.{}[]|\\".indexOf(chars[i]) != -1)
                buffer.append('\\').append(chars[i]);
            else
                buffer.append(chars[i]);
        }

        return buffer.toString();

    }// end replaceWildcards method

    public static File[] getFiles(File fileDir, String sWild)
    {
        File[] test = fileDir.listFiles();
        final String ssWild = replaceWildcards(sWild);
        File[] arrFile = fileDir.listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {                
                //return (name.toLowerCase().matches(ssWild));
                return (name.matches(ssWild));
            }
        });

        return arrFile;
    }
}
