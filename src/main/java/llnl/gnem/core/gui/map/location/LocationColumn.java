package llnl.gnem.core.gui.map.location;

/**
 *
 * @author addair1
 */
public abstract class LocationColumn<T extends LocationInfo> {
    private final String name;
    private final Class className;
    private final boolean editable;
    private final int columnWidth;

    public LocationColumn(String name, Class className, boolean editable, int columnWidth) {
        this.name = name;
        this.className = className;
        this.editable = editable;
        this.columnWidth = columnWidth;
    }

    public boolean isEditable() {
        return editable;
    }

    public int getColumnWidth() {
        return columnWidth;
    }

    public Class getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return name;
    }

    public abstract Object getValue(T data);
}
