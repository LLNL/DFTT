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
package llnl.gnem.dftt.core.gui.plotting.beachball;

import java.util.ArrayList;
import java.util.Arrays;

import org.ojalgo.matrix.Primitive32Matrix;
import org.ojalgo.matrix.Primitive32Matrix.DenseReceiver;

import llnl.gnem.dftt.core.util.PairT;

/**
 * Created by dodge1 Date: Mar 4, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class BeachballOps {

    public static Primitive32Matrix getRotationMatrix(double strike, double dip, double rake) {
        double conv = Math.PI / 180;
        double phi = strike * conv;
        double delta = -(90 - dip) * conv;
        double lambda = rake * conv;
        double cp = Math.cos(phi);
        double sp = Math.sin(phi);
        double cd = Math.cos(delta);
        double sd = Math.sin(delta);
        double cl = Math.cos(lambda);
        double sl = Math.sin(lambda);

        Primitive32Matrix r3 = buildR3(cp, sp); // rotation around Z for strike
        Primitive32Matrix r2 = buildR2(cd, sd); // rotation around X for dip
        Primitive32Matrix r1 = buildR1(cl, sl); // rotation around Y for rake
        return r3.multiply(r2).multiply(r1);
    }

    private static Primitive32Matrix buildR1(double cl, double sl) {
        DenseReceiver r1 = Primitive32Matrix.FACTORY.makeDense(3, 3);
        r1.set(0, 0, cl);
        r1.set(0, 1, 0);
        r1.set(0, 2, sl);
        r1.set(1, 0, 0);
        r1.set(1, 1, 1);
        r1.set(1, 2, 0);
        r1.set(2, 0, -sl);
        r1.set(2, 1, 0);
        r1.set(2, 2, cl);
        return r1.get();
    }

    private static Primitive32Matrix buildR2(double cd, double sd) {
        DenseReceiver r2 = Primitive32Matrix.FACTORY.makeDense(3, 3);
        r2.set(0, 0, 1);
        r2.set(0, 1, 0);
        r2.set(0, 2, 0);
        r2.set(1, 0, 0);
        r2.set(1, 1, cd);
        r2.set(1, 2, -sd);
        r2.set(2, 0, 0);
        r2.set(2, 1, sd);
        r2.set(2, 2, cd);
        return r2.get();
    }

    private static Primitive32Matrix buildR3(double cp, double sp) {
        DenseReceiver r3 = Primitive32Matrix.FACTORY.makeDense(3, 3);
        r3.set(0, 0, cp);
        r3.set(0, 1, -sp);
        r3.set(0, 2, 0);
        r3.set(1, 0, sp);
        r3.set(1, 1, cp);
        r3.set(1, 2, 0);
        r3.set(2, 0, 0);
        r3.set(2, 1, 0);
        r3.set(2, 2, 1);
        return r3.get();
    }

    public static Boundaries getBoundaries(double strike, double dip, double rake, double x0, double y0, double radius) {
        double dip1 = dip;
        Primitive32Matrix R = getRotationMatrix(strike, dip1, rake);
        double conv = Math.PI / 180;

        // Handle special case of dip = 0;
        if (dip1 > 90) {
            dip1 = 90;
        }
        if (dip1 < .001) {
            dip1 = 0;
        }
        if (dip1 == 0) {
            double rot = rake - strike;
            double[] angle = new double[181];
            for (int j = 0; j < angle.length; ++j) {
                angle[j] = (j + rot + 180) * conv;
            }
            double[] x1 = new double[angle.length];
            double[] y1 = new double[angle.length];
            for (int j = 0; j < x1.length; ++j) {
                x1[j] = Math.cos(angle[j]) * radius + x0;
                y1[j] = Math.sin(angle[j]) * radius + y0;
            }
            double[] x2 = new double[0];
            double[] y2 = new double[0];

            PairT<double[], double[]> boundingCircle = getBoundingCircle(x0, y0, radius);

            // Get projection of P-axis
            PairT<double[], double[]> proj = getPaxisProjection(R);
            double[] xPaxis = proj.getFirst();
            double[] yPaxis = proj.getSecond();
            for (int j = 0; j < xPaxis.length; ++j) {
                xPaxis[j] = xPaxis[j] * radius + x0;
                yPaxis[j] = yPaxis[j] * radius + y0;
            }
            return new Boundaries(x1, y1, x2, y2, xPaxis, yPaxis, boundingCircle);
        } else {
            double[] angle = new double[181];
            for (int j = 0; j < angle.length; ++j) {
                angle[j] = j * conv;
            }

            double[] SI = new double[angle.length];
            double[] ZE = new double[angle.length];
            double[] CS = new double[angle.length];
            for (int j = 0; j < angle.length; ++j) {
                SI[j] = Math.sin(angle[j]);
                ZE[j] = 0.0;
                CS[j] = Math.cos(angle[j]);
            }

            double[] th2 = new double[361];
            for (int j = 0; j < th2.length; ++j) {
                th2[j] = j * conv;
            }
            double[] xb = new double[th2.length];
            double[] yb = new double[th2.length];
            for (int j = 0; j < th2.length; ++j) {
                xb[j] = Math.cos(th2[j]);
                yb[j] = Math.sin(th2[j]);
            }
            DenseReceiver VV = Primitive32Matrix.FACTORY.makeDense(3, xb.length);
            for (int j = 0; j < th2.length; ++j) {
                VV.set(0, j, xb[j]);
                VV.set(1, j, yb[j]);
                VV.set(2, j, 0);
            }

            Primitive32Matrix EqPlane = R.invert().multiply(VV.get());

            // plane 1
            DenseReceiver V = Primitive32Matrix.FACTORY.makeDense(3, SI.length); //create 1/2 circle in +x-z plane
            for (int j = 0; j < SI.length; ++j) {
                V.set(0, j, SI[j]);
                V.set(1, j, ZE[j]);
                V.set(2, j, CS[j]);
            }
            PairT<double[], double[]> proj = getProjection(V.get(), R);
            double[] xp1 = proj.getFirst();
            double[] yp1 = proj.getSecond();

            // plane 2
            for (int j = 0; j < SI.length; ++j) {
                V.set(0, j, ZE[j]);
                V.set(1, j, SI[j]);
                V.set(2, j, CS[j]);
            }
            proj = getProjection(V.get(), R);
            double[] xp2 = proj.getFirst();
            double[] yp2 = proj.getSecond();

            //  compressional part of equatorial plane connecting plane1 and plane2
            ArrayList<Integer> II = new ArrayList<>();
            for (int j = 0; j < EqPlane.getColDim(); ++j) {
                if (EqPlane.get(0, j) >= 0 && EqPlane.get(1, j) >= 0) {
                    II.add(j);
                }
            }
            VV = Primitive32Matrix.FACTORY.makeDense(3, II.size());
            for (int j = 0; j < II.size(); ++j) {
                int idx = II.get(j);
                VV.set(0, j, EqPlane.get(0, idx));
                VV.set(1, j, EqPlane.get(1, idx));
                VV.set(2, j, EqPlane.get(2, idx));
            }

            proj = getProjection2(VV.get(), R);
            double[] xxe = proj.getFirst();
            double[] yye = proj.getSecond();

            proj = Join(xp1, yp1, xp2, yp2, xxe, yye);
            double[] xp = proj.getFirst();
            double[] yp = proj.getSecond();

            double[] x1 = new double[xp.length];
            double[] y1 = new double[yp.length];
            for (int j = 0; j < x1.length; ++j) {
                x1[j] = radius * xp[j] + x0;
                y1[j] = radius * yp[j] + y0;
            }

            // plane 3
            for (int j = 0; j < SI.length; ++j) { //create 1/2 circle in -x-z plane
                V.set(0, j, -SI[j]);
                V.set(1, j, ZE[j]);
                V.set(2, j, CS[j]);
            }
            proj = getProjection(V.get(), R);
            double[] xp3 = proj.getFirst();
            double[] yp3 = proj.getSecond();

            // plane 4
            for (int j = 0; j < SI.length; ++j) { //create 1/2 circle in -y-z plane
                V.set(0, j, ZE[j]);
                V.set(1, j, -SI[j]);
                V.set(2, j, CS[j]);
            }
            proj = getProjection(V.get(), R);
            double[] xp4 = proj.getFirst();
            double[] yp4 = proj.getSecond();

            //  compressional part of equatorial plane connecting plane3 and plane4

            II = new ArrayList<>();
            for (int j = 0; j < EqPlane.getColDim(); ++j) {
                if (EqPlane.get(0, j) <= 0 && EqPlane.get(1, j) <= 0) {
                    II.add(j);
                }
            }
            VV = Primitive32Matrix.FACTORY.makeDense(3, II.size());
            for (int j = 0; j < II.size(); ++j) {
                int idx = II.get(j);
                VV.set(0, j, EqPlane.get(0, idx));
                VV.set(1, j, EqPlane.get(1, idx));
                VV.set(2, j, EqPlane.get(2, idx));
            }

            proj = getProjection2(VV.get(), R);
            xxe = proj.getFirst();
            yye = proj.getSecond();
            proj = Join(xp3, yp3, xp4, yp4, xxe, yye);
            double[] xxp = proj.getFirst();
            double[] yxp = proj.getSecond();

            double[] x2 = new double[xxp.length];
            double[] y2 = new double[yxp.length];
            for (int j = 0; j < x2.length; ++j) {
                x2[j] = radius * xxp[j] + x0;
                y2[j] = radius * yxp[j] + y0;
            }
            PairT<double[], double[]> boundingCircle = getBoundingCircle(x0, y0, radius);

            // Get projection of P-axis
            proj = getPaxisProjection(R);
            double[] xPaxis = proj.getFirst();
            double[] yPaxis = proj.getSecond();
            for (int j = 0; j < xPaxis.length; ++j) {
                xPaxis[j] = xPaxis[j] * radius + x0;
                yPaxis[j] = yPaxis[j] * radius + y0;
            }
            return new Boundaries(x1, y1, x2, y2, xPaxis, yPaxis, boundingCircle);

        }

    }

    private static PairT<double[], double[]> getPaxisProjection(Primitive32Matrix r) {
        PairT<double[], double[]> proj;
        DenseReceiver pAxis = Primitive32Matrix.FACTORY.makeDense(3, 2);
        pAxis.set(0, 0, -1);
        pAxis.set(0, 1, 1);
        pAxis.set(1, 0, 1);
        pAxis.set(1, 1, -1);
        pAxis.set(2, 0, 0);
        pAxis.set(2, 1, 0);
        proj = getProjection(pAxis.get(), r);
        return proj;
    }

    private static PairT<double[], double[]> getBoundingCircle(double x0, double y0, double radius) {

        double[] x = new double[361];
        double[] y = new double[361];
        for (int j = 0; j < 361; ++j) {
            double az = j * Math.PI / 180;
            x[j] = x0 + Math.cos(az) * radius;
            y[j] = y0 + Math.sin(az) * radius;
        }
        return new PairT<>(x, y);
    }

    private static PairT<double[], double[]> Join(double[] xp1, double[] yp1, double[] xp2, double[] yp2, double[] eqx, double[] eqy) {
        double[] eqy1 = eqy;
        double[] eqx1 = eqx;

        double[] xp;
        double[] yp;
        int N = xp1.length;
        int M = xp2.length;
        int L = eqx1.length;

        // First join the two fault planes forcing the joint at the
        // endpoints of smallest radius
        double[] r = buildR(xp1, yp1);

        if (r[0] > r[N - 1]) {
            xp = Arrays.copyOf(xp1, N);
            yp = Arrays.copyOf(yp1, N);
        } else {
            xp = flipud(xp1);
            yp = flipud(yp1);
        }
        r = buildR(xp2, yp2);

        if (r.length > 0) {
            if (r[0] > r[M - 1]) {
                xp = concat(xp, flipud(xp2));
                yp = concat(yp, flipud(yp2));
            } else {
                xp = concat(xp, xp2);
                yp = concat(yp, yp2);
            }
        }
        if (eqx1.length == 0) {
            return new PairT<>(xp, yp);
        } else {
            // sometimes eqx-eqy comes in as a closed curve, so check endpoints and
            // remove last if necessary
            double[] az = atan2(eqy1, eqx1);

            int[] II1 = find(az, 0, Math.PI / 2);
            int[] II3 = find(az, -Math.PI, -Math.PI / 2);
            int[] II4 = find(az, -Math.PI / 2, 0);

            if (II1.length == 0 || II4.length == 0) {
                for (int aII3 : II3) {
                    az[aII3] = 2 * Math.PI + az[aII3];
                }
                for (int aII4 : II4) {
                    az[aII4] = 2 * Math.PI + az[aII4];
                }
            }

            Arrays.sort(az);
            eqx1 = cos(az);
            eqy1 = sin(az);

            r = buildR2(eqx1, xp[0], eqy1, yp[0]);

            if (r[0] > r[L - 1]) {
                xp = concat(xp, eqx1);
                yp = concat(yp, eqy1);
            } else {
                xp = concat(xp, flipud(eqx1));
                yp = concat(yp, flipud(eqy1));
            }

            return new PairT<>(xp, yp);
        }
    }

    private static double[] buildR2(double[] eqx, double xp, double[] eqy, double yp) {
        double[] r = new double[eqx.length];
        for (int j = 0; j < eqx.length; ++j) {
            double tmp1 = eqx[j] - xp;
            double tmp2 = eqy[j] - yp;
            r[j] = Math.sqrt(tmp1 * tmp1 + tmp2 * tmp2);
        }
        return r;
    }

    private static double[] cos(double[] arg) {
        double[] result = new double[arg.length];
        for (int j = 0; j < arg.length; ++j) {
            result[j] = Math.cos(arg[j]);
        }
        return result;
    }

    private static double[] sin(double[] arg) {
        double[] result = new double[arg.length];
        for (int j = 0; j < arg.length; ++j) {
            result[j] = Math.sin(arg[j]);
        }
        return result;
    }

    private static int[] find(double[] az, double geVal, double ltVal) {
        ArrayList<Integer> tmp = new ArrayList<>();
        for (int j = 0; j < az.length; ++j) {
            if (az[j] >= geVal && az[j] < ltVal) {
                tmp.add(j);
            }
        }
        int[] result = new int[tmp.size()];
        for (int j = 0; j < tmp.size(); ++j) {
            result[j] = tmp.get(j);
        }
        return result;
    }

    private static double[] atan2(double[] yvalue, double[] xvalue) {
        double[] result = new double[yvalue.length];
        for (int j = 0; j < yvalue.length; ++j) {
            result[j] = Math.atan2(yvalue[j], xvalue[j]);
        }
        return result;
    }

    private static double[] concat(double[] array1, double[] array2) {
        double[] result = new double[array1.length + array2.length];
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    private static double[] buildR(double[] xvalue, double[] yvalue) {
        int n = xvalue.length;
        double[] r = new double[xvalue.length];
        for (int j = 0; j < n; ++j) {
            r[j] = Math.sqrt(xvalue[j] * xvalue[j] + yvalue[j] * yvalue[j]);
        }
        return r;
    }

    private static double[] flipud(double[] input) {
        int n = input.length;
        double[] result = new double[n];
        for (int j = 0; j < n; ++j) {
            result[j] = input[n - j - 1];
        }
        return result;
    }

    private static PairT<double[], double[]> getProjection(Primitive32Matrix V, Primitive32Matrix R) {

        Primitive32Matrix VP = R.multiply(V);
        ArrayList<Integer> I = new ArrayList<>();
        int n = VP.getColDim();
        for (int j = 0; j < n; ++j) {
            if (VP.get(2, j) >= 0) {
                I.add(j);
            }
        }
        if (I.isEmpty()) {
            return new PairT<>(null, null);
        } else {
            DenseReceiver VPP = Primitive32Matrix.FACTORY.makeDense(3, I.size());
            for (int j = 0; j < I.size(); ++j) {
                int idx = I.get(j);
                VPP.set(0, j, VP.get(0, idx));
                VPP.set(1, j, VP.get(1, idx));
                VPP.set(2, j, VP.get(2, idx));
            }

            double[] r = new double[I.size()];
            for (int j = 0; j < I.size(); ++j) {
                r[j] = Math.sqrt(VPP.get(0, j) * VPP.get(0, j) + VPP.get(1, j) * VPP.get(1, j));
            }
            double[] inc = new double[r.length];
            Arrays.fill(inc, Math.PI / 2);
            ArrayList<Integer> II = new ArrayList<>();
            for (int j = 0; j < I.size(); ++j) {
                if (VPP.get(2, j) != 0) {
                    II.add(j);
                }
            }
            if (!II.isEmpty()) {
                for (Integer aII : II) {
                    inc[aII] = Math.atan(r[aII] / VPP.get(2, aII));
                }
            }
            double[] thet = new double[I.size()];
            for (int j = 0; j < thet.length; ++j) {
                thet[j] = Math.atan2(VPP.get(1, j), VPP.get(0, j));
            }

            double[] R0 = new double[inc.length];
            double sqrt2 = Math.sqrt(2);
            for (int j = 0; j < R0.length; ++j) {
                R0[j] = sqrt2 * Math.sin(inc[j] / 2);
            }
            double[] xp = new double[R0.length];
            double[] yp = new double[xp.length];
            for (int j = 0; j < xp.length; ++j) {
                xp[j] = R0[j] * Math.sin(thet[j]);
                yp[j] = R0[j] * Math.cos(thet[j]);
            }
            return new PairT<>(xp, yp);
        }
    }

    private static PairT<double[], double[]> getProjection2(Primitive32Matrix V, Primitive32Matrix R) {

        Primitive32Matrix VP = R.multiply(V); // rotate to strike-dip-rake
        //These points are guaranteed to be on the equator...
        double[] thet = new double[VP.getColDim()];
        for (int j = 0; j < thet.length; ++j) {
            thet[j] = Math.atan2(VP.get(1, j), VP.get(0, j));
        }

        double R0 = 1;
        double[] xp = new double[thet.length];
        double[] yp = new double[xp.length];
        for (int j = 0; j < xp.length; ++j) {
            xp[j] = R0 * Math.sin(thet[j]);
            yp[j] = R0 * Math.cos(thet[j]);
        }
        return new PairT<>(xp, yp);
    }
}
