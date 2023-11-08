/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2023 Lawrence Livermore National Laboratory (LLNL)
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
package llnl.gnem.apps.detection.sdBuilder.waveformViewer.autoTraceSelector;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.IntStream;
import org.apache.commons.math3.util.CombinatoricsUtils;


public class CombinationGenerator {
    public static int counter = 0;
    public static void main(String[] args) {
        int n = 22;
        IntStream stream = IntStream.range(1, n);
        stream.parallel().forEach(r-> generate(n,r));
 //      stream.forEach(r-> generate(n,r));
        
    }
    
    public static void generate(int n, int r) {
    Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(n, r);
    while (iterator.hasNext()) {
        final int[] combination = iterator.next();
        System.out.println(Arrays.toString(combination));
    }
}
}
