package llnl.gnem.core.waveform.components;

import java.awt.Color;
import java.util.*;
import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author addair1
 */
@ThreadSafe
public  class ComponentType implements Comparable<ComponentType> {

    private final char band;
    private final char instrument;
    private static final Map<ComponentType, Integer> PRIORITIES;
    private static final Map<String, ComponentType> STRING_TO_TYPE;


    static {
        String[] typeIds = {"BH", "SH", "HH", "LH", "EH", "BL", "SL", "HL", "LL"};
        Map<ComponentType, Integer> workPriorities = new LinkedHashMap<>();
        Map<String, ComponentType> workLookup = new HashMap<>();
        for (int i = 0; i < typeIds.length; i++) {
            ComponentType type = new ComponentType(typeIds[i]);
            workPriorities.put(type, typeIds.length - i - 1);
            workLookup.put(typeIds[i], type);
        }
        PRIORITIES = Collections.unmodifiableMap(workPriorities);
        STRING_TO_TYPE = workLookup;
    }

    public ComponentType(String id) {
        this.band = id.charAt(0);
        this.instrument = id.charAt(1);
    }

    public String getBand() {
        return "" + band;
    }

    public String getInstrument() {
        return "" + instrument;
    }

    public String getId() {
        return ("" + band + instrument).trim().toUpperCase();
    }

    public Color getForeground() {
        return Color.black;
    }

    public Color getBackground() {
        return Color.white;
    }

    public boolean pairedWith(ComponentType other) {
        return getId().equals(other.getId());
    }

    @Override
    public String toString() {
        return getBand()+getInstrument();
//        String objectId = Integer.toHexString(System.identityHashCode(this));
//        return String.format("(ComponentType(%s) %s)", objectId, getId());
    }

    @Override
    public int compareTo(ComponentType other) {
        if (this == other) {
            return 0;
        }
        if (other == null) {
            return -1;
        }

        int thisPriority = PRIORITIES.containsKey(this) ? PRIORITIES.get(this) : -1;
        int otherPriority = PRIORITIES.containsKey(other) ? PRIORITIES.get(other) : -1;
        if (thisPriority != otherPriority) {
            return (new Integer(thisPriority)).compareTo(otherPriority) * -1;
        } else if (!this.getId().equals(other.getId())) {
            return this.getId().compareTo(other.getId());
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ComponentType other = (ComponentType) o;
        return isCompatibleWith(other);
    }

    public boolean isCompatibleWith(ComponentType other) {
        return band == other.band && instrument == other.instrument;
    }

    @Override
    public int hashCode() {
        int result = ("" + band).hashCode();
        result = 31 * result + ("" + instrument).hashCode();
        result = 31 * result;
        return result;
    }

    public  ComponentType (char band, char instrument) {
        this.band = band;
        this.instrument = instrument;
    }
    
    public ComponentType( ComponentType other )
    {
        this.band = other.band;
        this.instrument = other.instrument;
    }

 
    public static List<ComponentType> orderedList() {
        List<ComponentType> values = new ArrayList<>(STRING_TO_TYPE.values());
        Collections.sort(values);
        return values;
    }
}
