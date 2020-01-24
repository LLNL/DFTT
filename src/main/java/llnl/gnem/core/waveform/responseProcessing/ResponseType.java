package llnl.gnem.core.waveform.responseProcessing;

/*

 *  COPYRIGHT NOTICE

 *  RBAP Version 1.0

 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.

 */
/**
 * A type-safe enum class for response types.
 *
 * @author Doug Dodge
 */
public enum ResponseType {

    DIS("dis"), NONE("none"), VEL("vel"), ACC("acc"), EVRESP("evresp"), SACPZF("sacpzf"), PAZ("paz"), FAP("fap"), PAZFIR(
                    "pazfir"),PAZFAP("pazfap");

    private final String dbValue;

    ResponseType(String dbvalue) {
        this.dbValue = dbvalue;
    }

    public boolean isNDCType() {
        return this == PAZ || this == FAP || this == PAZFIR;
    }

    public String getDbValue() {
        return dbValue;
    }

    /**
     * Utility method to convert a String representation of the response type. This methods supports aliases in addition
     * to the 'name' for some of the ResponseTypes.
     * 
     * @param type String representing the response type
     * @return ResponseType enum
     */
    public static ResponseType getResponseType(String type) {
        type = type.toLowerCase();

        if (type.equals("displacement") || type.equals("dis")) {
            return ResponseType.DIS;
        } else if (type.equals("velocity") || type.equals("vel")) {
            return ResponseType.VEL;
        } else if (type.equals("acceleration") || type.equals("acc")) {
            return ResponseType.ACC;
        } else if (type.equals("evresp") || type.equals("resp")) {
            return ResponseType.EVRESP;
        } else if (type.equals("sacpzf") || type.equals("sacpz") || type.equals("polezero") || type.equals("pz")) {
            return ResponseType.SACPZF;
        } else if (type.equals("paz")) {
            return ResponseType.PAZ;
        } else if (type.equals("fap")) {
            return ResponseType.FAP;
        } else if (type.equals("pazfir")) {
            return ResponseType.PAZFIR;
        } else
            return ResponseType.NONE;
    }
}
