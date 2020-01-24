package llnl.gnem.core.util.MathFunctions;

/**
 * User: matzel
 * Date: Oct 16, 2006
 * Time: 3:12:46 PM
 */
public class Legendre
{
    /**
     * Pn(z) == 1/2^n  * SUM[k=0:n/2]  [ -1^k  * binomial(n,k) * binomial (2n - 2k, n) * z ^ (n-2*k)
     */
    public static double P(int n, double z)
    {
        double prefix = 1 / Math.pow(2, n);
        double sum = 0.;
        for (int k = 0; k <= n / 2; k++)
        {
            try
            {

                double sign = Math.pow(-1, k);
                double b1 = MathFunction.binomial(n, k);
                double b2 = MathFunction.binomial((2 * n - 2 * k), n);
                double zn2k = Math.pow(z, n - 2 * k);

                sum = sum + sign * b1 * b2 * zn2k;

            }
            catch (Exception e)
            {
            }
        }

        return prefix * sum;
    }

    /**
     * Pmu,n(z)
     *
     public static double P(int n, double mu, double z)
     {

     for (int k = 0; k <= n; k++)
     {

     }
     }*/

}
