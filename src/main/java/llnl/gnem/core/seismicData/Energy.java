package llnl.gnem.core.seismicData;

import net.jcip.annotations.Immutable;

/**
 *
 * @author addair1
 */
@Immutable
public class Energy {
    public static final double JOULES_PER_KG_TNT = 4.184e6;
    private final double joules;
    
    public Energy(double joules) {
        this.joules = joules;
    }
    
    public double getJoules() {
        return joules;
    }
    
    public double getYield() {
        return joules / JOULES_PER_KG_TNT;
    }
    
    /*
     * @param yield in kilogram of TNT
     */
    public static Energy fromYield(double yield) {
        return new Energy(yield * JOULES_PER_KG_TNT);
    }
}
