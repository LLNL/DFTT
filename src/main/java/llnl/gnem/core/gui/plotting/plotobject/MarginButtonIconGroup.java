package llnl.gnem.core.gui.plotting.plotobject;

import javax.swing.*;

/**
 * Created by dodge1
 * Date: Mar 31, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class MarginButtonIconGroup {

    private final ImageIcon[] images;

    public MarginButtonIconGroup( ImageIcon image0, ImageIcon image1, ImageIcon image2, ImageIcon image3)
    {
        images = new ImageIcon[4];
        images[0] = image0;
        images[1] = image1;
        images[2] = image2;
        images[3] = image3;
    }

    public ImageIcon getIcon(int idx )
    {
        return images[idx];
    }
}
