package llnl.gnem.core.gui.map.stations;

import java.util.Collection;
import java.util.prefs.Preferences;
import llnl.gnem.core.gui.map.location.LocationColumn;
import llnl.gnem.core.gui.map.location.LocationModel;
import llnl.gnem.core.gui.waveform.StationNavigationModel;
import llnl.gnem.core.util.StationKey;

/**
 * Created by dodge1 Date: Feb 12, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 * @param <T>
 */
public class StationModel<T extends StationInfo> extends LocationModel<T> {

    private final StationNavigationModel navModel;
    private boolean autoCenterStationUponSelection;
    private final Preferences prefs;

    protected StationModel(StationNavigationModel navModel) {
        this.navModel = navModel;
        autoCenterStationUponSelection = true;
        prefs = Preferences.userNodeForPackage(this.getClass());
        autoCenterStationUponSelection = prefs.getBoolean("AUTO_CENTER_ON_STATION", false);
    }

    public StationNavigationModel getNavigationModel() {
        return navModel;
    }

    public boolean getAutoCenterStationUponSelection() {
        return autoCenterStationUponSelection;
    }

    public void setAutoCenterStationUponSelection(boolean value) {
        autoCenterStationUponSelection = value;
        prefs.putBoolean("AUTO_CENTER_ON_STATION", value);
    }

    @Override
    public void setCurrent(T info) {
        super.setCurrent(info);
        navModel.setSelectedStation(info);
    }

    public T getStationInfo(StationKey sta) {
        for (T station : getLocations()) {
            if (station.getStation().equals(sta)) {
                return station;
            }
        }
        return null;
    }

    public void setStations(Collection<T> newStations) {
        clear();
        addLocations(newStations);
        retrievalIsCompleted();
    }

    @Override
    public void createColumns() {
        addColumn(new LocationColumn<T>("SOURCE", String.class, false, -1) {
            @Override
            public Object getValue(T data) {
                return data.getStation().getSource();
            }
        });
        addColumn(new LocationColumn<T>("NETWORK", String.class, false, -1) {
            @Override
            public Object getValue(T data) {
                return data.getStation().getNet();
            }
        });
        addColumn(new LocationColumn<T>("NET_DATE", Integer.class, false, -1) {
            @Override
            public Object getValue(T data) {
                return data.getStation().getNetJdate();
            }
        });
        addColumn(new LocationColumn<T>("STA_CODE", String.class, false, -1) {
            @Override
            public Object getValue(T data) {
                return data.getStationCode();
            }
        });
        addColumn(new LocationColumn<T>("DESCRIPTION", String.class, false, 300) {
            @Override
            public Object getValue(T data) {
                return data.getDescription();
            }
        });
        addColumn(new LocationColumn<T>("LAT", Double.class, false, -1) {
            @Override
            public Object getValue(T data) {
                return data.getLat();
            }
        });
        addColumn(new LocationColumn<T>("LON", Double.class, false, -1) {
            @Override
            public Object getValue(T data) {
                return data.getLon();
            }
        });
        addColumn(new LocationColumn<T>("ELEV", Double.class, false, -1) {
            @Override
            public Object getValue(T data) {
                return data.getElevation();
            }
        });
    }
}
