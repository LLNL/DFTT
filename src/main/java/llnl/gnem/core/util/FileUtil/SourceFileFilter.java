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
package llnl.gnem.core.util.FileUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by dodge1
 * Date: Jul 15, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class SourceFileFilter implements FileFilter {

    private final Collection<String> extensions;

    public SourceFileFilter( Collection<String> exts )
    {
        extensions = new ArrayList<String>();
        for (String ext :exts){

            int dotPos = ext.indexOf('.');
            if( dotPos == ext.length()-1 ){
                throw new IllegalArgumentException( "Supplied file extension " + ext + " ends with a '.'!");
            }
            String extension;
            if( dotPos >= 0 )
                extension = ext.substring(dotPos+1).toLowerCase().trim();
            else{
                extension = ext.toLowerCase().trim();
            }
            extensions.add(extension);
        }
    }

        public boolean accept(File file) {
            String thisExtension = getExtension(file);
            if (thisExtension != null) {
                for (String extension : extensions){
                    if (thisExtension.equals(extension))
                        return true;
                }
            }
            return false;
        }

        private static String getExtension(File f) {
            String ext = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');

            if (i > 0 && i < s.length() - 1) {
                ext = s.substring(i + 1).toLowerCase();
            }
            return ext;
        }

    }