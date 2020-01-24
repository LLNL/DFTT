/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package llnl.gnem.core.util.FileUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author dodge1
 */
  class PatternFilter implements FilenameFilter {

    private Pattern pattern;


    public PatternFilter( String regex ) {
      pattern = Pattern.compile( regex );
    }


    public boolean accept( File arg0, String arg1 ) {
      Matcher m = pattern.matcher( arg1 );
      return m.matches();
    }

  }

