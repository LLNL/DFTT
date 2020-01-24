package llnl.gnem.core.metadata;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dodge1
 */
public enum OrientationCode {

    Z("Z"), N("N"), E("E"), A("A"), B("B"), C("C"), D("D"), T("T"), R("R"),
    U("U"), V("V"), W("W"), O("O"), I("I"), F("F"), H("H"), S("S"), ONE("1"),
    TWO("2"), THREE("3"), UNKNOWN("UNKNOWN");
    private final String code;

    OrientationCode(String code) {
        this.code = code;
    }

    static List<OrientationCode> getSeismometerCodes() {
        List<OrientationCode> result = new ArrayList<>();
        result.add(Z);
        result.add(N);
        result.add(E);
        result.add(A);
        result.add(B);
        result.add(C);
        result.add(T);
        result.add(R);
        result.add(ONE);
        result.add(TWO);
        result.add(THREE);
        result.add(U);
        result.add(V);
        result.add(W);

        return result;

    }

    static List<OrientationCode> getTiltCodes() {
        List<OrientationCode> result = new ArrayList<>();
        result.add(A);
        return result;
    }

    static List<OrientationCode> getUnknownCode() {
        List<OrientationCode> result = new ArrayList<>();
        result.add(UNKNOWN);
        return result;
    }

    static List<OrientationCode> getCalibrationCodes() {
        List<OrientationCode> result = new ArrayList<>();
        result.add(A);
        result.add(B);
        result.add(C);
        result.add(D);
        return result;
    }

    static List<OrientationCode> getPressureCodes() {
        List<OrientationCode> result = new ArrayList<>();
        result.add(O);
        result.add(I);
        result.add(D);
        result.add(F);
        result.add(H);
        result.add(U);
        result.add(B); // Not in FDSN seed manual, but occurs commonly
        return result;
    }

    static List<OrientationCode> getMagnetometerCodes() {
        List<OrientationCode> result = new ArrayList<>();
        result.add(Z);
        result.add(N);
        result.add(E);
        return result;
    }

    static List<OrientationCode> getHumidityCodes() {
        List<OrientationCode> result = new ArrayList<>();
        result.add(O);
        result.add(I);
        result.add(D);
        return result;
    }
    

    static List<OrientationCode> getTideCode() {
        List<OrientationCode> result = new ArrayList<>();
        result.add(Z);
        return result;
    }
 

    static List<OrientationCode> getWindCodes() {
        List<OrientationCode> result = new ArrayList<>();
        result.add(S);
        result.add(D);
        return result;
    }

   static List<OrientationCode> getBeamCodes() {
        List<OrientationCode> result = new ArrayList<>();
        result.add(I);
        result.add(C);
        result.add(F);
        result.add(O);
        return result;
    }
    
    
    @Override
    public String toString() {
        return code;
    }

    public static OrientationCode getEnumValue(String string) {
        if (string.equals("1")) {
            return OrientationCode.ONE;
        } else if (string.equals("2")) {
            return OrientationCode.TWO;
        } else if (string.equals("3")) {
            return OrientationCode.THREE;
        } else {
            for (OrientationCode code : OrientationCode.values()) {
                if (code.toString().equals(string)) {
                    return code;
                }
            }

            throw new IllegalArgumentException("Illegal value string: " + string);
        }
    }
}
