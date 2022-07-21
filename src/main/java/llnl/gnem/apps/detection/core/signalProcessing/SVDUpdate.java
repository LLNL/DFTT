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
package llnl.gnem.apps.detection.core.signalProcessing;

import java.util.ArrayList;
import java.util.List;

import org.ojalgo.matrix.Primitive32Matrix;
import org.ojalgo.matrix.Primitive32Matrix.DenseReceiver;
import org.ojalgo.matrix.decomposition.SingularValue;

public class SVDUpdate {

    public static List<Primitive32Matrix> evaluate(Primitive32Matrix U, Primitive32Matrix S, Primitive32Matrix Y, double lambda) {

        int dim = U.getColDim();

        // compute update matrices

        Primitive32Matrix F1 = U.transpose().multiply(Y);
        Primitive32Matrix A = Y.subtract(U.multiply(F1));
        SingularValue<Double> svd = SingularValue.PRIMITIVE.make(A);

        Primitive32Matrix C = Primitive32Matrix.FACTORY.make(svd.getU());

        C = C.subtract(U.multiply(U.transpose().multiply(C))); // force orthogonality of C wrt U for cases where Y = Ua

        Primitive32Matrix F2 = C.transpose().multiply(Y);

        //  Merge matrices F1 and F2:
        //   | F1 |
        //   | F2 |

        int nr1 = F1.getRowDim();
        int nr2 = F2.getRowDim();
        int Fnr = nr1 + nr2;
        int nc = F1.getColDim();

        DenseReceiver Fa = Primitive32Matrix.FACTORY.makeDense(Fnr, nc);
        for (int ic = 0; ic < nc; ic++) {
            for (int ir = 0; ir < nr1; ir++) {
                Fa.set(ir, ic, F1.get(ir, ic));
            }
            for (int ir = 0; ir < nr2; ir++) {
                Fa.set(ir + nr1, ic, F2.get(ir, ic));
            }
        }
        Primitive32Matrix F = Fa.get();

        // square singular values - i.e. produce related correlation eigenvalues

        DenseReceiver tmp = S.copy();

        for (int i = 0; i < dim; i++) {
            double tmps = tmp.get(i, i);
            tmps *= lambda; // exponential age weight
            tmps *= tmps;
            tmp.set(i, i, tmps);
        }

        A = Primitive32Matrix.FACTORY.make(Fnr, Fnr);
        A.superimpose(tmp);
        A = A.add(F.multiply(F.transpose()));

        SingularValue<Double> svdA = SingularValue.PRIMITIVE.make(A);

        //  Merge matrices U and C
        //    | U  C |
        //

        int Anr = U.getRowDim();
        int nc1 = U.getColDim();
        int nc2 = C.getColDim();
        DenseReceiver Aa = Primitive32Matrix.FACTORY.makeDense(Anr, nc1 + nc2);
        for (int ir = 0; ir < Anr; ir++) {
            for (int ic = 0; ic < nc1; ic++) {
                Aa.set(ir, ic, U.get(ir, ic));
            }
            for (int ic = 0; ic < nc2; ic++) {
                Aa.set(ir, ic + nc1, C.get(ir, ic));
            }
        }

        A = Aa.get();

        List<Primitive32Matrix> retval = new ArrayList<>();
        retval.add(A.multiply(Primitive32Matrix.FACTORY.make(svdA.getU())));

        // square root of eigenvalues to get singular values

        DenseReceiver Snew = Primitive32Matrix.FACTORY.makeWrapper(svdA.getD()).copy();
        int ns = Snew.getRowDim();
        for (int i = 0; i < ns; i++) {
            Snew.set(i, i, Math.sqrt(Snew.get(i, i)));
        }

        retval.add(Snew.get());

        return retval;
    }

}
