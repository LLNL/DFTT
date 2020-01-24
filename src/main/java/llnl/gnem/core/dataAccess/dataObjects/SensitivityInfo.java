/*
* Copyright (C) 2015 Lawrence Livermore National Laboratory (LLNL)
*/
package llnl.gnem.core.dataAccess.dataObjects;

/**
 * @author barno1
 *
 */
public class SensitivityInfo {
	private static final String NO = "NO";
	
	private final String sensitivityType;
	private final String distribution;
	private final String publication;

	public String getSensitivityType() {
		return sensitivityType;
	}
	public String getDistribution() {
		return distribution;
	}
	public String getPublication() {
		return publication;
	}
	
	/**
     * @param sensitivityType
	 * @param sType
	 * @param distribution
	 * @param publication
	 */
	public SensitivityInfo(String sensitivityType, String distribution, String publication) {
		this.sensitivityType = sensitivityType == null? SensitivityInfoTypes.UNKNOWN.getType() : sensitivityType;
		this.distribution = distribution == null? NO : distribution;
		this.publication = publication == null? NO : publication;
	}
	
	public static SensitivityInfo newDefault() {
		return new SensitivityInfo(SensitivityInfoTypes.UNSENSITIVE.getType(), "YES", "YES");
	}
}
