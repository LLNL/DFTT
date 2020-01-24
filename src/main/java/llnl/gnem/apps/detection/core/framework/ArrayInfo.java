/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.core.framework;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import java.sql.Connection;
import java.sql.SQLException;
import llnl.gnem.apps.detection.core.dataObjects.ArrayElement;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.database.column.CssVersion;
import llnl.gnem.core.database.dao.CssSiteDAO;
import llnl.gnem.core.metadata.site.core.CssSite;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;

/**
 *
 * @author dodge1
 */
public class ArrayInfo {

    private final Collection<CssSite> allSites;
    private final Map<String, SiteCollection> staSiteMap;
    private final Map<String, Array> staArrayMap;

    public ArrayInfo() {
        allSites = new ArrayList<>();
        staSiteMap = new HashMap<>();
        staArrayMap = new HashMap<>();
    }

    public void initialize(String cssSiteFile, CssVersion version) throws IOException, ParseException {
        allSites.clear();
        allSites.addAll(CssSiteDAO.getInstance().readSiteFile(cssSiteFile, version));
        populateMaps();
    }

    public void initialize(Collection<? extends StreamKey> keys, int jdate, String tableName) throws SQLException {
        allSites.clear();
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            for (StreamKey key : keys) {
                allSites.add(CssSiteDAO.getInstance().getSiteRow(key.getSta(), jdate, tableName, conn));
            }
            String refsta = getRefsta();
            if( refElementIsMissing(refsta)){
                
                allSites.add(CssSiteDAO.getInstance().getSiteRow(refsta, jdate, tableName, conn));
            }
            populateMaps();
        } finally {
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }

    }

    public void initialize(String refsta, int jdate, String tableName) throws SQLException {
        allSites.clear();
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            allSites.addAll(CssSiteDAO.getInstance().getArrayElements(refsta, jdate, tableName, conn));
            populateMaps();
        } finally {
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    public Collection< ArrayElement> getElements(String refsta, int jdate) {

        Array array = staArrayMap.get(refsta);
        if (array != null) {
            return array.getArrayElements(jdate);
        } else {
            return new ArrayList<>();
        }
    }

    public Collection<String> getArrayNames() {
        return staArrayMap.keySet();
    }

    public Collection<CssSite> getArrayElements(String refsta, int jdate) {
        Array array = staArrayMap.get(refsta);
        if (array != null) {
            return array.getElements(jdate);
        } else {
            return new ArrayList<>();
        }
    }

    public CssSite getSite(String sta, TimeT time) {
        return getSite(sta, time.getJdate());
    }

    public CssSite getSite(String sta, int jdate) {
        SiteCollection sc = staSiteMap.get(sta);
        if (sc != null) {
            return sc.getSite(jdate);
        } else {
            return null;
        }
    }

    public Collection<CssSite> getAll(String sta) {
        SiteCollection sc = staSiteMap.get(sta);
        if (sc != null) {
            return sc.getAll();
        } else {
            return new ArrayList<>();
        }
    }

    private void addToSiteCollection(CssSite site) {
        SiteCollection sc = staSiteMap.get(site.getSta());
        if (sc == null) {
            sc = new SiteCollection(site);
            staSiteMap.put(site.getSta(), sc);
        } else {
            sc.add(site);
        }
    }

    private void addAllArrayRefsta(CssSite site) {
        if (site.isArrayRefsta()) {
            Array array = staArrayMap.get(site.getSta());
            if (array == null) {
                array = new Array(site);
                staArrayMap.put(site.getSta(), array);
            } else {
                array.addRefstaEpoch(site);
            }
        }
    }

    private void addArrayElements() {
        for (CssSite site : allSites) {
            if (site.isNonRefstaArrayElement()) {
                String refsta = site.getRefsta();
                Array array = staArrayMap.get(refsta);
                if (array != null) {
                    array.addElement(site);
                }
            }
        }
    }

    private void populateMaps() {
        staSiteMap.clear();
        staArrayMap.clear();
        for (CssSite site : allSites) {
            addToSiteCollection(site);
            addAllArrayRefsta(site);
        }
        addArrayElements();
    }

    private String getRefsta() {
        String refsta = null;
        for(CssSite site : allSites){
            if( refsta == null){
                refsta = site.getRefsta();
            }
            else if( !refsta.equals(site.getRefsta())){
                throw new IllegalStateException("Not all sites have the same refsta!");
            }
        }
        return refsta;
    }

    private boolean refElementIsMissing(String refsta) {
        for (CssSite site : allSites) {
            if(site.getSta().equals(refsta)){
                return false;
            }
        }
        return true;
    }

    class SiteCollection {

        Collection<CssSite> members;

        public SiteCollection(CssSite site) {
            members = new ArrayList<>();
            members.add(site);
        }

        private void add(CssSite site) {
            members.add(site);
        }

        private CssSite getSite(int jdate) {
            for (CssSite site : members) {
                if (site.isEffective(jdate)) {
                    return site;
                }
            }
            return null;
        }

        private Collection<CssSite> getAll() {
            return new ArrayList<>(members);
        }
    }

    private class Array {

        private final Collection<ArrayEpoch> refstaEpochs;

        private Array(CssSite site) {
            refstaEpochs = new ArrayList<>();
            refstaEpochs.add(new ArrayEpoch(site));
        }

        private void addRefstaEpoch(CssSite site) {
            refstaEpochs.add(new ArrayEpoch(site));
        }

        private void addElement(CssSite site) {
            for (ArrayEpoch epoch : refstaEpochs) {
                if (epoch.isValidFor(site)) {
                    epoch.add(site);
                }
            }
        }

        private Collection<CssSite> getElements(int jdate) {
            for (ArrayEpoch epoch : refstaEpochs) {
                if (epoch.isValidFor(jdate)) {
                    return epoch.getElements();
                }
            }
            return new ArrayList<>();
        }

        private Collection<ArrayElement> getArrayElements(int jdate) {
            for (ArrayEpoch epoch : refstaEpochs) {
                if (epoch.isValidFor(jdate)) {
                    return epoch.getArrayElements();
                }
            }
            return new ArrayList<>();
        }

        private class ArrayEpoch {

            private final CssSite refsta;
            private final Collection<CssSite> elements;

            private ArrayEpoch(CssSite refsta) {
                this.refsta = refsta;
                elements = new ArrayList<>();
            }

            private boolean isValidFor(CssSite site) {
                return refsta.isEffective(site.getOndate()) && refsta.isEffective(site.getOffdate());
            }

            private void add(CssSite site) {
                elements.add(site);
            }

            private boolean isValidFor(int jdate) {
                return refsta.isEffective(jdate);
            }

            private Collection<CssSite> getElements() {
                Collection<CssSite> result = new ArrayList<>(elements);
                result.add(refsta);
                return result;
            }

            private Collection< ArrayElement> getArrayElements() {

                Collection< ArrayElement> retval = new ArrayList< >();
                for (CssSite site : elements) {
                    double dz = site.getElevation() - refsta.getElevation();
                    retval.add(new ArrayElement(site, dz));
                }

                return retval;
            }
        }
    }
}
