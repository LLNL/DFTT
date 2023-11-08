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
package llnl.gnem.dftt.core.metadata;

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
