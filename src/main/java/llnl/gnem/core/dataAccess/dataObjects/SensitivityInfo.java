/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
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
