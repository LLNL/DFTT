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
public enum SensitivityInfoTypes {
	UNKNOWN("UNKNOWN", -1), UNSENSITIVE("UNSENSITIVE", 0), MIXED("MIXED_SENSITIVE", 1), SENSITIVE("SENSITIVE", 2);

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

	public static SensitivityInfoTypes getSensitivity(String sensitivityType) {
		if (sensitivityType != null && !sensitivityType.isEmpty()) {
			sensitivityType = sensitivityType.replaceAll(" ", "_");
			if (sensitivityType.equalsIgnoreCase(SensitivityInfoTypes.UNSENSITIVE.getType())) {
				return SensitivityInfoTypes.UNSENSITIVE;
			} else if (sensitivityType.equalsIgnoreCase(SensitivityInfoTypes.MIXED.getType())) {
				return SensitivityInfoTypes.MIXED;
			}
		}
		return SensitivityInfoTypes.SENSITIVE;
	}

}
