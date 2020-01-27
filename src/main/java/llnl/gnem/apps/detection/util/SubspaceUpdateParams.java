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
package llnl.gnem.apps.detection.util;

/**
 *
 * @author dodge1
 */
public class SubspaceUpdateParams {

    private boolean updateOnDetection;
    private double updateThreshold = 0.8;
    private double lambda = 0.9;
    private double energyCapture = 0.8;

    private SubspaceUpdateParams() {
    }

    public static SubspaceUpdateParams getInstance() {
        return SubspaceUpdateParamsHolder.INSTANCE;
    }

    public void setUpdateOnDetection(boolean value) {
        updateOnDetection = value;
    }

    public boolean isUpdateOnDetection() {
        return updateOnDetection;
    }

    public double getUpdateThreshold() {
        return updateThreshold;
    }

    /**
     * @param updateThreshold the updateThreshold to set
     */
    public void setUpdateThreshold(double updateThreshold) {
        this.updateThreshold = updateThreshold;
    }

    /**
     * @return the lambda
     */
    public double getLambda() {
        return lambda;
    }

    /**
     * @param lambda the lambda to set
     */
    public void setLambda(double lambda) {
        this.lambda = lambda;
    }

    /**
     * @return the energyCapture
     */
    public double getEnergyCapture() {
        return energyCapture;
    }

    /**
     * @param energyCapture the energyCapture to set
     */
    public void setEnergyCapture(double energyCapture) {
        this.energyCapture = energyCapture;
    }

    private static class SubspaceUpdateParamsHolder {

        private static final SubspaceUpdateParams INSTANCE = new SubspaceUpdateParams();
    }
}
