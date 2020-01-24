package llnl.gnem.core.util.MathFunctions;
/**
 * User: matzel
 * Date: Oct 16, 2006
 * Time: 2:50:45 PM
 */
public class SphericalHarmonic extends MathFunction
{
    /**
     * Spherical Harmonic Ymn(theta,phi)
     *
     *  |m| <= n
     *
     public static Complex Y(int n, int m, double theta, double phi)
     {
     Complex result = new Complex(0., 0.);

     if (n <= Math.abs(m)) return result;

     try
     {
     double numerator = (2 * n + 1) * factorial(n - m);
     double denominator = 4 * Math.PI * (n + m);

     Complex imag = new Complex(0, 1.);
     Complex eimphi = Complex.exp(imag.times(m * phi)); // e^(i*m*phi)
     Complex Pmn = Legendre.P(Math.cos(theta));

     result = eimphi.times(Pmn).times((numerator / denominator));

     }
     catch (Exception e)
     {
     }

     return result;
     }*/
}
