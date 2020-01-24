package llnl.gnem.core.gui.plotting.colormap;

/**
 * An implementation of the Colormap interface that mimics the Matlab Hot
 * colormap.
 *
 */
public class HotColormap extends ArrayColormap {

    /**
     * The constructor for the HotColormap. This constructor maps the supplied
     * min and max values to the base and top of the color table respectively.
     *
     * @param min The minimum value to be mapped to the color table base.
     * @param max The maximum value to be mapped to the color table top.
     */
    public HotColormap(double min, double max) {
        red = new int[]{11, 21, 32, 43, 53, 64, 74, 85, 96, 106, 117, 128, 138, 149, 159, 170, 181, 191, 202,
            213, 223, 234, 244, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
            255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
            255, 255, 255, 255, 255, 255, 255};
        green = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 11, 21, 32, 43, 53, 64, 74, 85, 96, 106, 117, 128, 138, 149,
            159, 170, 181, 191, 202, 213, 223, 234, 244, 255, 255, 255, 255, 255, 255, 255, 255, 255,
            255, 255, 255, 255, 255, 255, 255, 255};
        blue = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 32, 48, 64, 80, 96,
            112, 128, 143, 159, 175, 191, 207, 223, 239, 255};

        setMinMax(min, max);
    }

}
