/*
* Copyright (C) 2015 Lawrence Livermore National Laboratory (LLNL)
*/
package llnl.gnem.core.dataAccess.dataObjects;

/**
 * @author barno1
 *
 */
public enum SensitivityInfoTypes {
	UNKNOWN("UNKNOWN", -1), UNSENSITIVE("UNSENSITIVE", 0);

	private final String type;
	private final Integer level;

	/**
	 * @param type
	 * @param level
	 */
	private SensitivityInfoTypes(String type, Integer level) {
		this.type = type;
		this.level = level;
	}

	public String getType() {
		return type;
	}

	public Integer getLevel() {
		return level;
	}

	public boolean equals(String potentialType) {
		return (potentialType == null) ? false : type.equalsIgnoreCase(potentialType);
	}

}
