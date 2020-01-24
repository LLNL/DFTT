package llnl.gnem.core.gui.plotting.colormap;

import java.awt.Color;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * This class allows the user to define the Color Map by defining the Color at
 * specific values Colors for undefined values will be interpolated
 * <p>
 * </p>
 * See main for an example
 * <p>
 * </p>
 * User: Eric Matzel Date: Nov 12, 2007
 *
 * Will not inspect for: MagicNumber
 */
public class DefinedColormap implements Colormap {

    private double min = Double.MAX_VALUE;
    private double max = -min;
    private Color defaultColor = new Color(0.f, 0.f, 0.f);

    private final TreeMap<Double, Color> treemap = new TreeMap<>();

    enum InstalledStandardMap {

        NONE, COPPER, JET, WARM, COOL, SPRING, SUMMER, AUTUMN, WINTER
    }

    private InstalledStandardMap currentMap = InstalledStandardMap.NONE;

    @Override
    public double getMin() {
        return min;
    }

    @Override
    public double getMax() {
        return max;
    }

    /**
     * Get the color associated with the data value
     *
     * @param value
     * @return a Color object
     */
    @Override
    public Color getColor(double value) {
        if ((value < min) || (value > max) || treemap.isEmpty()) {
            return defaultColor; // Any entry that's out of bounds will return the default Color
        }

        // check special cases
        if (treemap.containsKey(value)) {
            return treemap.get(value);
        }
        // if value  < the smallest defined value - return min defined color
        if (value <= treemap.firstKey()) {
            return treemap.get(treemap.firstKey());
        }
        // if value > the largest defined value - return max defined color
        if (value >= treemap.lastKey()) {
            return treemap.get(treemap.lastKey());
        }

        // If none of the initial checks have been passed, interpolate a Color based on the defined model
        return interpolateColor(value);
    }

    /**
     * Set the minimum and maximum range Note there's no requirement to set the
     * min and max fields, nor do they have to be different numbers
     *
     * @param min
     * @param max
     */
    @Override
    public void setMinMax(double min, double max) {
        this.min = min;
        this.max = max;
        switch (currentMap) {
            case JET:
                installJetMap();
                break;
            case COPPER:
                installCopperMap();
                break;
            case WARM:
                installWarmMap();
                break;
            case COOL:
                installCoolMap();
                break;
            case SPRING:
                installSpringMap();
                break;
            case SUMMER:
                installSummerMap();
                break;
            case AUTUMN:
                installAutumnMap();
                break;
            case WINTER:
                installWinterMap();
                break;
        }

    }

    /**
     * This method allows you to define the default color for non-defined values
     * (e.g. values below the minimum, exceeding the maximum etc.) This allows
     * the user to define whether those should be drawn as black (the initial
     * default) or as white, transparent, blue .... etc.
     *
     * @param c - the default Color to return for undefined values
     */
    public void setDefaultColor(Color c) {
        this.defaultColor = c;
        currentMap = InstalledStandardMap.NONE;
    }

    /**
     * Defines the color at a specific value intermediate values will be
     * interpolated
     *
     * @param value
     * @param color
     */
    public void defineColorAt(double value, Color color) {
        if (value < min) {
            min = value;
        }
        if (value > max) {
            max = value;
        }

        treemap.put(value, color);
        currentMap = InstalledStandardMap.NONE;
    }

    /**
     * This method redefines values already in the Color Map by shifting the
     * nearest Color entries until the desired Color is tied to a preferred
     * value. other Map values will be compressed or expanded as needed Note:
     * the number of entries in the treemap will not change, but the keySet will
     *
     * @param value
     * @param color
     */
    public void redefineColorValue(double value, Color color) {
        currentMap = InstalledStandardMap.NONE;
    }

    /**
     * For undefined values between the min and max - interpolate a Color value
     *
     * @param value
     * @return the interpolated Color
     */
    private Color interpolateColor(double value) {
        Color color1 = new Color(0.f, 0.f, 0.f);
        Color color2 = new Color(0.f, 0.f, 0.f);

        double x1 = 0.;
        double x2 = 1.;

        Iterator it = treemap.keySet().iterator();

        boolean done = false;

        while ((!done) && (it.hasNext())) {
            Double key = (Double) it.next();

            if (key > value) {
                color2 = treemap.get(key);
                done = true;

                x2 = key - value;
            } else {
                color1 = treemap.get(key);
                x1 = value - key;
            }
        }

        int red1 = color1.getRed();
        int green1 = color1.getGreen();
        int blue1 = color1.getBlue();
        int alpha1 = color1.getAlpha();

        int red2 = color2.getRed();
        int green2 = color2.getGreen();
        int blue2 = color2.getBlue();
        int alpha2 = color2.getAlpha();

        double dx = x1 + x2;

        int red = (int) ((red1 * x2) / dx + (red2 * x1) / dx);
        int green = (int) ((green1 * x2) / dx + (green2 * x1) / dx);
        int blue = (int) ((blue1 * x2) / dx + (blue2 * x1) / dx);
        int alpha = (int) ((alpha1 * x2) / dx + (alpha2 * x1) / dx);

        return new Color(red, green, blue, alpha);

    }

    /**
     * switches all the red and blue values in the current color map the green
     * and alpha values remain unchanged e.g. Color(200,100,50) TO
     * Color(50,100,200) etc.
     */
    public void reverse() {
        for (Double aDouble : treemap.keySet()) {
            Color color = treemap.get(aDouble);

            int blue = color.getRed();
            int green = color.getGreen();
            int red = color.getBlue();
            int alpha = color.getAlpha();

            treemap.put(aDouble, new Color(red, green, blue, alpha));
        }
        currentMap = InstalledStandardMap.NONE;
    }

    //----------------SPECIFIC MAP TYPES BELOW-----------------------------------------------
    /**
     * A cool colored map note: min and max must be defined before this will
     * work
     */
    public void installCoolMap() {
        if (treemap.isEmpty()) {
            createDefaultMap(2); // only 2 treemap points are needed for this map (max and min)
        }

        for (Double key : treemap.keySet()) {
            float r = (float) ((key - min) / (max - min));
            float g = (float) ((max - key) / (max - min));
            float b = 1.f;

            r = ensureColorValue(r);
            g = ensureColorValue(g);

            defineColorAt(key, new Color(r, g, b)); // this should overwrite the previous Color value
        }
        currentMap = InstalledStandardMap.COOL;
    }

    /**
     * A warm colored map note: min and max must be defined before this will
     * work
     */
    public void installWarmMap() {
        installCoolMap();// create a cool map
        reverse();// reverse the red and blue values
        currentMap = InstalledStandardMap.WARM;
    }

    /**
     * A specific case: define an Autumn Map note: min and max must be defined
     * before this will work
     */
    public void installAutumnMap() {
        //If the treemap is empty, create values with default entries
        if (treemap.isEmpty()) {
            createDefaultMap(2); // only 2 treemap points are needed for this map (max and min)
        }

        for (Double key : treemap.keySet()) {
            float r = 1.f;
            float g = (float) ((max - key) / (max - min));
            float b = 0.f;

            g = ensureColorValue(g);

            defineColorAt(key, new Color(r, g, b)); // this should overwrite the previous Color value
        }
        currentMap = InstalledStandardMap.AUTUMN;
    }

    /**
     * A specific case: define a Winter Map note: min and max must be defined
     * before this will work
     */
    public void installWinterMap() {
        //If the treemap is empty, create values with default entries
        if (treemap.isEmpty()) {
            createDefaultMap(2); // only 2 treemap points are needed for this map (max and min)
        }

        for (Double key : treemap.keySet()) {
            float r = 0.f;
            float g = (float) ((max - key) / (max - min));
            float b = (float) ((key - min) / (max - min)) / 2.f + 0.5f;

            g = ensureColorValue(g);
            b = ensureColorValue(b);

            defineColorAt(key, new Color(r, g, b)); // this should overwrite the previous Color value
        }
        currentMap = InstalledStandardMap.WINTER;
    }

    /**
     * A specific case: define a Spring Map note: min and max must be defined
     * before this will work
     */
    public void installSpringMap() {
        //If the treemap is empty, create values with default entries
        if (treemap.isEmpty()) {
            createDefaultMap(2); // only 2 treemap points are needed for this map (max and min)
        }

        for (Double aDouble : treemap.keySet()) {
            float r = 1.f;
            float g = (float) ((max - aDouble) / (max - min));
            float b = (float) ((aDouble - min) / (max - min));

            g = ensureColorValue(g);
            b = ensureColorValue(b);

            defineColorAt(aDouble, new Color(r, g, b)); // this should overwrite the previous Color value
        }
        currentMap = InstalledStandardMap.SPRING;
    }

    /**
     * A specific case: define a Summer Map note: min and max must be defined
     * before this will work
     */
    public void installSummerMap() {
        //If the treemap is empty, create values with default entries
        if (treemap.isEmpty()) {
            createDefaultMap(2); // only 2 treemap points are needed for this map (max and min)
        }

        for (Double key : treemap.keySet()) {
            float r = (float) ((key - min) / (max - min));
            float g = (float) ((key - min) / (max - min)) / 2.f + 0.5f;
            float b = 0.4f;

            r = ensureColorValue(r);
            g = ensureColorValue(g);

            defineColorAt(key, new Color(r, g, b)); // this should overwrite the previous Color value
        }
        currentMap = InstalledStandardMap.SUMMER;
    }

    /**
     * A specific case : define a Jet Map note: min and max must be defined
     * before this will work the current map will be cleared out before the Jet
     * map is created
     */
    public void installJetMap() {
        if (max > min) {
            treemap.clear(); // start with an empty treemap

            double range = max - min;

            //the jet map is defined at each 1/8th step including both min and max (9 points total)
            defineColorAt(0 * range + min, new Color(0f, 0f, 0.6f));
            defineColorAt(.125 * range + min, new Color(0f, 0f, 1f));
            defineColorAt(.250 * range + min, new Color(0f, 0.5f, 1f));
            defineColorAt(.375 * range + min, new Color(0f, 1f, 1f));
            defineColorAt(.500 * range + min, new Color(0.5f, 1f, 0.5f));
            defineColorAt(.625 * range + min, new Color(1f, 1f, 0f));
            defineColorAt(.750 * range + min, new Color(1f, 0.5f, 0f));
            defineColorAt(.875 * range + min, new Color(1f, 0f, 0f));
            defineColorAt(1 * range + min, new Color(0.5f, 0f, 0f));
        }
        currentMap = InstalledStandardMap.JET;
    }

    /**
     * A specific case : define a Copper Map note: min and max must be defined
     * before this will work the current map will be cleared out before the
     * Copper map is created
     */
    public void installCopperMap() {
        treemap.clear(); // start with an empty treemap

        defineColorAt(min, new Color(0f, 0f, 0f));
        defineColorAt(max, new Color(1f, 0.8f, 0.5f));

        defineColorAt(0.875 * (max - min) + min, new Color(1f, .66f, .4f)); // the point at which red = 1.f
        currentMap = InstalledStandardMap.COPPER;
    }

    /**
     * A utility method for empty treesets only creates a Default Map in which
     * all entries are the defaultColor Used before defining specific maps note:
     * min and max must be defined before this will work
     *
     * @param npts - the number of points in the Treemap - allows the user to
     * define only the minimum number of points necessary to define the map
     */
    private void createDefaultMap(int npts) {
        if (treemap.isEmpty()) {
            // create initial treemap entries
            if ((max > min) && npts > 1) {
                double dx = (max - min) / (npts - 1);

                for (int ii = 0; ii < npts; ii++) {
                    double key = min + ii * dx;
                    treemap.put(key, defaultColor);
                }
            }
        }
        currentMap = InstalledStandardMap.NONE;
    }

    /**
     * Creates an array of the rgb colors for use with other tools
     *
     * @param npts the number of elements desired (e.g. 64)
     * @return an array containing the rgb values
     */
    int[][] createArray(int npts) {
        if (!treemap.isEmpty()) {
            if ((max > min) && npts > 1) {
                double dx = (max - min) / (npts - 1);

                int[][] result = new int[npts][3];

                for (int ii = 0; ii < npts; ii++) {
                    double value = min + ii * dx;
                    Color color = getColor(value);
                    result[ii][0] = color.getRed();
                    result[ii][1] = color.getGreen();
                    result[ii][2] = color.getBlue();
                }

                return result;
            }
        }

        // If the method hasn't already returned an array - return the null array
        return null;
    }

    /**
     * ensure that the r,g,b,a values are valid (fall between 0.f and 1.f) TODO
     * make this an in-place replacement
     */
    private float ensureColorValue(float value) {
        if (value < 0.f) {
            value = 0.f;
        } else if (value > 1.f) {
            value = 1.f;
        }

        return value;
    }
}
