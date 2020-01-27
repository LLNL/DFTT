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
package llnl.gnem.core.io.SAC;

/**
 * User: dodge1
 * Date: Oct 19, 2006
 * Time: 9:39:56 AM
 * To change this template use File | Settings | File Templates.
 */
public enum Iztype {
    IUNKN("Unknown", 5),
    IB("Begin Time", 9),
    IDAY("Midnight of reference GMT day", 10),
    IO("Event origin time", 11),
    IA("First arrival time", 12),
    IT0("User pick t0 time", 13),
    IT1("User pick t1 time", 14),
    IT2("User pick t2 time", 15),
    IT3("User pick t3 time", 16),
    IT4("User pick t4 time", 17),
    IT5("User pick t5 time", 18),
    IT6("User pick t6 time", 19),
    IT7("User pick t7 time", 20),
    IT8("User pick t8 time", 21),
    IT9("User pick t9 time", 22);


    int code;
    String value;

    Iztype(String value, int code) {
        this.value = value;
        this.code = code;
    }

    public String toString() {
        return value;
    }

    public int getCode() {
        return code;
    }

    public static Iztype getIztype( int v )
    {
        for( Iztype type : Iztype.values() ){
            if( type.getCode() == v )
                return type;
        }
        return Iztype.IUNKN;
    }
}
