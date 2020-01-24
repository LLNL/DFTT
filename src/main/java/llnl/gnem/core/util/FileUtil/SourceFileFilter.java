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