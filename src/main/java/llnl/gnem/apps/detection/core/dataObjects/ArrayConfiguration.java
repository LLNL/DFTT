package llnl.gnem.apps.detection.core.dataObjects;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import llnl.gnem.apps.detection.core.framework.ArrayInfo;
import llnl.gnem.core.database.column.CssVersion;
import llnl.gnem.core.util.StreamKey;

public class ArrayConfiguration {

    private final String arrayName;
    private final Map< String, ArrayElement> elements;

    public static ArrayConfiguration createFromDatabase(Collection<? extends StreamKey> staChanList, String siteTable, int jdate) throws SQLException, IOException, ParseException {
        ArrayInfo info = new ArrayInfo();
        info.initialize(staChanList, jdate, siteTable);
        ArrayList<String> refstas = new ArrayList<>(info.getArrayNames());
        if (refstas.size() == 1) {
            String refsta = refstas.get(0);
            return new ArrayConfiguration(refsta, info, jdate);
        } else {
            throw new IllegalStateException("Elements of sta-chan list do not belong to a single array!");
        }
    }

    public static ArrayConfiguration createFromFlatfile(String arrayName, String CSSFileName, int jdate) throws IOException, ParseException {
        ArrayInfo info = new ArrayInfo();
        info.initialize(CSSFileName, CssVersion.Css30);

        return new ArrayConfiguration(arrayName, info, jdate);
    }

    public static ArrayConfiguration createFromDatabase(String arrayName, String siteTableName, int jdate) throws IOException, ParseException, SQLException {
        ArrayInfo info = new ArrayInfo();
        info.initialize(arrayName, jdate, siteTableName);
        return new ArrayConfiguration(arrayName, info, jdate);
    }

    private ArrayConfiguration(String arrayName, ArrayInfo info, int jdate) throws IOException, ParseException {

        this.arrayName = arrayName;
        elements = new HashMap< >();


        Collection< ArrayElement> sites = info.getElements(arrayName, jdate);

        for (ArrayElement AE : sites) {
            elements.put(AE.getSta(), AE);
        }
    }

    // delay calculations - slowness vectors point back toward the source (in the local coordinate frame)
    public double[] delaysInSeconds(ArrayList<? extends StreamKey> channels, float sn, float se, float sz) {

        double[] retval = new double[channels.size()];
        int i = 0;
        for (StreamKey stachan : channels) {
            retval[i++] = elements.get(stachan.getSta().trim()).delayInSeconds(sn, se, sz);
        }

        return retval;
    }

    public double[] delaysInSeconds(ArrayList<? extends StreamKey> channels, float sn, float se) {

        double[] retval = new double[channels.size()];
        int i = 0;
        for (StreamKey stachan : channels) {
            retval[i++] = elements.get(stachan.getSta().trim()).delayInSeconds(sn, se);
        }

        return retval;
    }

    public double[] delaysInSeconds(Collection<? extends StreamKey> channels, SlownessSpecification s) {

        double[] retval = new double[channels.size()];
        int i = 0;
        for (StreamKey stachan : channels) {
            retval[i++] = elements.get(stachan.getSta().trim()).delayInSeconds(s.getSlownessVector());
        }

        return retval;
    }

    public ArrayElement getElement(String name) {
        return elements.get(name.trim());
    }

    public int getNelements() {
        return elements.size();
    }

    public String getArrayName() {
        return arrayName;
    }

    public void print(PrintStream ps) {
        Collection< ArrayElement> V = elements.values();
        for (ArrayElement AE : V) {
            System.out.println(AE);
        }
    }

    public boolean hasElement(String sta) {
        return elements.get(sta) != null;
    }

    public Map<StreamKey, ArrayElement> getElements(Collection<? extends StreamKey> channels) {
        Map<StreamKey, ArrayElement> result = new HashMap<>();
        for (StreamKey stachan : channels) {
            result.put(stachan, elements.get(stachan.getSta().trim()));
        }
        return result;
    }
}
