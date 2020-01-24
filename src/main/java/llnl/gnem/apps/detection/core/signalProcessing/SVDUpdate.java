package llnl.gnem.apps.detection.core.signalProcessing;

import java.util.ArrayList;
import java.util.Random;

import Jama.Matrix;
import Jama.SingularValueDecomposition;




public class SVDUpdate {
  
  
  public static ArrayList< Object > evaluate ( Matrix U, Matrix S, Matrix Y, double lambda ) {
    
    int dim = U.getColumnDimension();
    
    // compute update matrices
    
    Matrix                     F1  = U.transpose().times(Y); 
    Matrix                     A   = Y.minus( U.times( F1 ) );
    SingularValueDecomposition svd = new SingularValueDecomposition( A );
    Matrix                     C   = svd.getU();
    
    C = C.minus( U.times( U.transpose().times(C) ) );  // force orthogonality of C wrt U for cases where Y = Ua
    
    Matrix F2 = C.transpose().times( Y );
    
    //  Merge matrices F1 and F2:
    //   | F1 |
    //   | F2 |
    
    int nr1 = F1.getRowDimension();
    int nr2 = F2.getRowDimension();
    int Fnr = nr1 + nr2;
    int nc  = F1.getColumnDimension();
    
    double[][] Fa  = new double[Fnr][nc];
    double[][] F1a = F1.getArray();
    double[][] F2a = F2.getArray();
    for ( int ic = 0;  ic < nc;  ic++ ) {
      for ( int ir = 0;  ir < nr1;  ir++ )  Fa[ir][ic]     = F1a[ir][ic];
      for ( int ir = 0;  ir < nr2;  ir++ )  Fa[ir+nr1][ic] = F2a[ir][ic];
    }
    Matrix F = new Matrix( Fa );
    
    // square singular values - i.e. produce related correlation eigenvalues
    
    Matrix tmp = new Matrix( S );
    
    for ( int i = 0;  i < dim;  i++ ) {
      double tmps = tmp.get(i,i);
      tmps *= lambda;                           // exponential age weight
      tmps *= tmps;
      tmp.set( i, i, tmps );
    }
    
    A = new Matrix( Fnr, Fnr );
    A.setMatrix( 0, dim-1, 0, dim-1, tmp );
    A = A.plus( F.times( F.transpose() ) );
   
    SingularValueDecomposition svdA = new SingularValueDecomposition( A );
    
    //  Merge matrices U and C
    //    | U  C |
    //
    
    int Anr = U.getRowDimension();
    int nc1 = U.getColumnDimension();
    int nc2 = C.getColumnDimension();
    double[][] Aa = new double[Anr][nc1+nc2];
    double[][] Ua = U.getArray();
    double[][] Ca = C.getArray();
    for ( int ir = 0;  ir < Anr;  ir++ ) {
      for ( int ic = 0;  ic < nc1;  ic++ ) Aa[ir][ic]     = Ua[ir][ic];
      for ( int ic = 0;  ic < nc2;  ic++ ) Aa[ir][ic+nc1] = Ca[ir][ic];
    }
    
    A = new Matrix( Aa );
    
    ArrayList< Object > retval = new ArrayList< Object >();
    retval.add( A.times( svdA.getU() ) );
    
    // square root of eigenvalues to get singular values
    
    Matrix Snew = svdA.getS();
    int    ns   = Snew.getRowDimension();
    for ( int i = 0;  i < ns;  i++ ) Snew.set( i, i, Math.sqrt( Snew.get(i,i) ) );

    retval.add( Snew );
    
    return retval;
  }
  

}
