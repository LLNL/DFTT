/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.waveform.classification.peaks;

/**
 *
 * @author dodge1
 */
public class Peak extends BasePeak {

    private final int startIndex;
    private final int endIndex;
    private final double area;

    public Peak(int index, float value, double dt, int startIndex, int endIndex, double area) {
        super(index, value, dt);
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.area = area;
    }

    public Peak(BasePeak base, int startIndex, int endIndex, double area) {
        super(base);
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.area = area;
    }
    
    @Override
    public String toString()
    {
        return String.format("Peak of area %f and width %f starting at t = %f (idx = %d) with max at t = %f", 
                getArea(), getWidth(), this.getStartTime(), startIndex, this.getTime());
    }

    public double getStartTime()
    {
        return startIndex * getSampleInterval();
    }
    public double getEndTime() {
        return endIndex * getSampleInterval();
    }

    public int getEndIndex() {
        return endIndex;
    }

    /**
     * @return the startIndex
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * @return the area
     */
    public double getArea() {
        return area;
    }
    
    public double getWidth()
    {
        return (endIndex - startIndex) * getSampleInterval();
    }

}
