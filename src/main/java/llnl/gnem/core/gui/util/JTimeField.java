package llnl.gnem.core.gui.util;

import javax.swing.*;
import javax.swing.text.MaskFormatter;

/**
 * User: dodge1
 * Date: May 13, 2004
 * Time: 1:50:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class JTimeField extends JFormattedTextField {

    public JTimeField()
    {
        super( createFormatter() );
    }

    protected static MaskFormatter createFormatter()
    {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter( "####/##/##_##:##:##.###" );
            formatter.setPlaceholderCharacter( '0' );
            formatter.setPlaceholder( "1970/01/01_00:00:00.000" );
            formatter.setAllowsInvalid( false );
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        return formatter;
    }


}
