/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.metadata;

/**
 *
 * @author dodge1
 */
public enum BandCode {

    F("... ≥ 1000 to < 5000 ≥ 10 sec"), 
    G("... ≥ 1000 to < 5000 < 10 sec"), 
    D("... ≥ 250 to < 1000 < 10 sec"), 
    C("... ≥ 250 to < 1000 ≥ 10 sec"), 
    E("Extremely Short Period ≥ 80 to < 250 < 10 sec"), 
    S("Short Period ≥ 10 to < 80 < 10 sec"), 
    H("High Broad Band ≥ 80 to < 250 ≥ 10 sec"), 
    B("Broad Band ≥ 10 to < 80 ≥ 10 sec"), 
    M("Mid Period > 1 to < 10"), 
    L("Long Period ≈ 1"), 
    V("Very Long Period ≈ 0.1"), 
    U("Ultra Long Period ≈ 0.01"), 
    R("Extremely Long Period ≥ 0.0001 to < 0.001"), 
    P("On the order of 0.1 to 1 day ≥ 0.00001 to< 0.0001"), 
    T("On the order of 1 to 10 days ≥ 0.000001 to<0.00001"), 
    Q("Greater than 10 days < 0.000001"), 
    A("Administrative Instrument Channel variable NA"), 
    O("Opaque Instrument Channel variable NA");
    private final String description;

    BandCode(String descrip) {
        description = descrip;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}
