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
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 * @author matzel1
 */
public class ResourceUtil
{
    
    /**
     * List directory contents for a resource folder. Not recursive. This is
     * basically a brute-force implementation. Works for regular files and also
     * JARs.
     *
     * @author Greg Briggs
     * @param clazz Any java class that lives in the same place as the resources
     * you want.
     * @param path Should end with "/", but not start with one.
     * @return Just the name of each member item, not the full paths.
     * @throws URISyntaxException
     * @throws IOException
     */
    public String[] getResourceListing(Class clazz, String path) throws URISyntaxException, IOException
    {
        URL dirURL = clazz.getResource(path);

        if (dirURL != null && dirURL.getProtocol().equals("file"))
        {
            System.out.println(" A file path: easy enough " + dirURL.toString() + "\t" + dirURL.getProtocol());
            return new File(dirURL.toURI()).list();
        }

        if (dirURL == null)
        {
            System.out.println("/* "
                    + "* In case of a jar file, we can't actually find a directory."
                    + "* Have to assume the same jar as clazz."
                    + "*/");
            System.out.println(clazz.getName());
            String me = "/llnl/gnem/core/util/FileUtil/TestResources/";
            //String me = clazz.getName().replace(".", "/") + ".class";
            dirURL = clazz.getResource(me);
        }

        if (dirURL.getProtocol().equals("jar"))
        {
            //TODO need to identify the specific resource
            System.out.println("/* A JAR path */ " + dirURL.toString() + "\t" + dirURL.getProtocol());
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
            System.out.println("//strip out only the JAR file\t" + jarPath);
            JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
            Set<String> result = new HashSet<String>(); //avoid duplicates in case it is a subdirectory
            while (entries.hasMoreElements())
            {
                String name = entries.nextElement().getName();
                System.out.println("name\t" + name);
                if (name.startsWith(path))
                {
                    System.out.println("//filter according to the path");
                    String entry = name.substring(path.length());
                    int checkSubdir = entry.indexOf("/");
                    if (checkSubdir >= 0)
                    {
                        System.out.println("// if it is a subdirectory, we just return the directory name");
                        entry = entry.substring(0, checkSubdir);
                        System.out.println(entry);
                    }
                    result.add(entry);
                }
            }
            return result.toArray(new String[result.size()]);
        }

        throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
    }

    public void debugGetResources()
    {
        Class clazz = this.getClass();
        String path = "TestResources";

        try
        {
            String[] a = getResourceListing(clazz, path);
            System.out.println("wait");
        }
        catch (Exception e)
        {
            System.out.println(e);
        }

        path = "/llnl/gnem/core/util/FileUtil/TestResources/";
        try
        {
            String[] a = getResourceListing(clazz, path);
            System.out.println("wait");

        }
        catch (Exception e)
        {
            System.out.println(e);
        }


        path = "/llnl/gnem/core/util/FileUtil/TestResources/test.file1.txt";
        try
        {
            String[] a = getResourceListing(clazz, path);
            System.out.println("wait");

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
    
}
