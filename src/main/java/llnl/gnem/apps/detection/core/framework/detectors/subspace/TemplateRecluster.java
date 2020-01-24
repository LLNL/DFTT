/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.core.framework.detectors.subspace;

import java.util.ArrayList;
import llnl.gnem.apps.detection.cancellation.dendrogram.CompleteLink;
import llnl.gnem.apps.detection.cancellation.dendrogram.HierarchicalLinker;
import llnl.gnem.apps.detection.cancellation.dendrogram.SimilarityMeasure;
import llnl.gnem.apps.detection.cancellation.dendrogram.SingleLink;
import llnl.gnem.apps.detection.core.signalProcessing.Sequence;
import llnl.gnem.core.util.StreamKey;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import java.text.DecimalFormat;
import java.util.Arrays;
import llnl.gnem.apps.detection.cancellation.dendrogram.LinkageType;
import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;


/**
 *
 * @author harris2
 */
public class TemplateRecluster {
    
    private static final float ALMOST_ONE                 = 0.9999f;
    private static final double MIN_ALLOWABLE_CORRELATION = 0.2;
    private static final double eps                       = Math.pow(2, -52);
    
    private final int                           ntemplates;
    private final ArrayList< SubspaceTemplate > oldTemplates;
    private final LinkageType                   linkageType;
    private HierarchicalLinker                  linker;
    private ArrayList< Object[] >               clusters;
    
    
    
    public TemplateRecluster( ArrayList< SubspaceTemplate > oldTemplates, LinkageType linkageType ) {
        
        ntemplates        = oldTemplates.size();
        this.oldTemplates = oldTemplates;
        this.linkageType  = linkageType;
        
        linker = null;        
    }
    
    
    
    public ArrayList< SubspaceTemplate >  buildAllTemplates( float clusteringThreshold, double energyCaptureThreshold ) {
        
        linker        = cluster( clusteringThreshold );
        int nclusters = linker.getClusters( clusteringThreshold ).size();
        
        ArrayList< SubspaceTemplate> retval = new ArrayList<>( );
        
        for ( int ic = 0;  ic < nclusters;  ic++ ) {
            retval.add( buildTemplateFromCluster( ic, energyCaptureThreshold ) );
        }
        
        return retval;
    }
    
    
    
    // Cluster templates
    
    public HierarchicalLinker cluster( float threshold ) {
        
        // calculate pairwise projections
        
        ArrayList< SimilarityMeasure > measurements = new ArrayList<>();
        ArrayList< Object >            objectProxy  = new ArrayList<>();
        
        int maxLength = 0;
        for ( int i = 0;  i < ntemplates;  i++ )
            maxLength = Math.max( maxLength, oldTemplates.get(i).getTemplateLength() );
        
        for ( int i = 0;  i < ntemplates;  i++ ) {
            
            SubspaceTemplate oldTi = oldTemplates.get(i);
            objectProxy.add( oldTi );
            
            for ( int j = i+1;  j < ntemplates;  j++ ) {
                SubspaceTemplate oldTj = oldTemplates.get(j);
                Projection p = new Projection( oldTi, oldTj, maxLength );
                SimilarityMeasure SM = new SimilarityMeasure( oldTi, oldTj, p.getProjectionValue(), p.getDecimatedDelay() );
                measurements.add( SM );
            }
            
        }
        
        switch ( linkageType ) {
            case SingleLink:
                linker = new SingleLink( measurements, threshold, objectProxy );
                break;
            case CompleteLink:
                linker = new CompleteLink( measurements, threshold, objectProxy );
                break;
            default:
                throw new IllegalStateException( "Unknown linkage type: " + linkageType );
        }
        
        clusters  = linker.getClusters( threshold );
        
        return linker;        
    }
    
    
    
    // Construct new template from cluster
    
    
    public SubspaceTemplate buildTemplateFromCluster( int clusterIndex, double energyCaptureThreshold ) {
        
        Object[] cluster = clusters.get( clusterIndex );
        
        // Calculate delays and correlations from Projections
        
        int nT = cluster.length;
        
        Matrix S = new Matrix( nT, nT );
        Matrix C = new Matrix( nT, nT );
        
        for ( int i = 0;  i < nT;  i++ ) {
            SubspaceTemplate Ti = (SubspaceTemplate) cluster[i];
            for ( int j = 0;  j < nT;  j++ ) {
                SubspaceTemplate Tj = (SubspaceTemplate) cluster[j];
                Projection P = new Projection( Ti, Tj );
                S.set( i, j, P.getDecimatedDelay() );
                C.set( i, j, P.getProjectionValue() );
            }
        }
        
        Matrix delays = VanDecarCrosson( nT, S, C, false );
  
        
        // Calculate number of columns of data matrix and find maximum length of templates
        
        int   ncols             = 0;
        int   maxTemplateLength = 0;
       
        for ( Object O : cluster ) {
            SubspaceTemplate T = (SubspaceTemplate) O;
            ncols             +=  T.getdimension();
            maxTemplateLength  = Math.max( maxTemplateLength, T.getTemplateLength() );
        }
        
        // first template
        
        Object                 O                 = cluster[0];
        int                    delay             = -(int) Math.round( delays.get(0,0) );
        SubspaceTemplate       referenceTemplate = (SubspaceTemplate) O;
        ArrayList< StreamKey > channels          = referenceTemplate.getStaChanList();
        
        int nchannels = referenceTemplate.getnchannels();
        int nrows     = nchannels * maxTemplateLength;
        
        double[][] x   = new double[ nrows ][ ncols ];   
        float[]    tmp = new float[ maxTemplateLength ];
        
        // unpack first template
        
        int                  templateLength = referenceTemplate.getTemplateLength();
        ArrayList< float[] > t              = referenceTemplate.getMultiplexedRepresentation();
        int                  ndim           = referenceTemplate.getdimension();
        
        int cptr = 0;
        for ( int idim = 0;  idim < ndim;  idim++ ) {
            
            float[] xt = t.get(idim);
            
            for ( int ich = 0;  ich < nchannels;  ich++ ) {
                int src = ich*templateLength;
                int dst = ich*maxTemplateLength;
                Arrays.fill( tmp, 0.0f );
                System.arraycopy( xt, src, tmp, 0, templateLength );
                Sequence.zshift( tmp, delay );                                                          //  TODO:  check this
                for ( int i = 0;  i < maxTemplateLength;  i++ ) x[i + dst][cptr] = tmp[i];
            }

            cptr++;
        }
        
        // add representations from remainder of templates
        
        for ( int it = 1;  it < cluster.length;  it++ ) {
            
            SubspaceTemplate T                = (SubspaceTemplate) cluster[it];
            templateLength                    = T.getTemplateLength();
            delay                             = -(int) Math.round( delays.get(it, 0) );
            ArrayList< StreamKey > channels_i = T.getStaChanList();
            
            // check consistency
            
            if ( !referenceTemplate.consistent( T ) ) throw new IllegalStateException( "Templates are inconsistent" );
            
            // OK, consistent, now load representation into data matrix

            ndim = T.getdimension();
            t    = T.getMultiplexedRepresentation();
            
            for ( int idim = 0;  idim < ndim;  idim++ ) {
                float[] xt = t.get(idim);
                for ( int ich = 0;  ich < nchannels;  ich++ ) {
                    int src = channels_i.indexOf( channels.get(ich) ) * templateLength;
                    int dst = ich * maxTemplateLength;
                    Arrays.fill( tmp, 0.0f );
                    System.arraycopy( xt, src, tmp, 0, templateLength );
                    Sequence.zshift( tmp, -delay );                                                          //  TODO:  check this
                    for ( int i = 0;  i < maxTemplateLength;  i++ ) x[ dst + i ][ cptr ] = tmp[ i ];  // reordering of channels here, as necessary
                }
                cptr++;
            }
            
        }     
                           
        // svd and dimension estimation
            
        Matrix X = new Matrix( x );
        SingularValueDecomposition svd = X.svd();
        
        double[] s = svd.getSingularValues();
        
        double E = 0.0;
        for ( int i = 0;  i < s.length;  i++ ) {
            s[i] *= s[i];
            E    += s[i];
        }
        int dimension = 0;
        double sum = 0.0;
        while (sum / E < energyCaptureThreshold ) {
            sum += s[ dimension++ ];
        }
        
        System.out.println( "Template dimension estimate: " );
        DecimalFormat I3 = new DecimalFormat( "000" );
        DecimalFormat D  = new DecimalFormat( "0.00000" );
        sum = 0.0;
        for ( int i = 0;  i < s.length;  i++ ) {
            sum += s[i];
            if ( i == dimension )
                System.out.println( I3.format( i ) + "  " + D.format( sum/E ) + "    <---" );
            else
                System.out.println( I3.format( i ) + "  " + D.format( sum/E ) );
        }
        
        double[] sv = new double[ dimension ];
        ArrayList< float[] > rep = new ArrayList<>();
        Matrix U = svd.getU();
        for ( int id = 0;  id < dimension;  id++ ) {
            sv[id] = s[id];
            tmp = new float[ maxTemplateLength ];
            for ( int i = 0;  i < maxTemplateLength;  i++ ) tmp[i] = (float) U.get( i, id );
            rep.add( tmp );
        }
        
        // construct new SubspaceTemplate, taking specifications from the first (reference) template
        
        SubspaceSpecification spec   = (SubspaceSpecification) referenceTemplate.getSpecification();  // TODO:  check that components of spec are appropriate
        PreprocessorParams    params = referenceTemplate.getPreprocessingParameters();
        
        SubspaceTemplate retval = new SubspaceTemplate( spec, params, rep, sv );
          
        return retval;
    }
    
    
    
    private Matrix VanDecarCrosson( int nTraces, Matrix shifts, Matrix correlations, boolean FixToZeroShift ) {
        
        if (nTraces == 1) {
            return new Matrix(1, 1, 0);
        }
        if (nTraces == 2) {
            return shifts.getMatrix(0, 1, 0, 0);
        }
        int nt = (nTraces * (nTraces - 1)) / 2 + 1;

        float[] w = new float[nt];
        Arrays.fill(w, 1);
        int windex = 0;
        for (int j = 0; j < nTraces - 1; ++j) {
            for (int k = j + 1; k < nTraces; ++k) {
                float ccTmp = (float) correlations.get(j, k);
                if (ccTmp >= 1) {
                    ccTmp = ALMOST_ONE;
                }
                if (ccTmp < MIN_ALLOWABLE_CORRELATION) {
                    ccTmp = 0;
                }
                double term = ccTmp / (1 - ccTmp);
                w[windex++] = (float) (term * term);
            }
        }

        preventSingularMatrix(w);

        Matrix ATWA = buildATWAMatrix(nTraces, nt, w);
        Matrix ATWDT = buildATWDTMatrix(nTraces, w, shifts);
        Matrix result = pinv(ATWA).times(ATWDT);

//        Matrix result = ATWA.inverse().times(ATWDT);

        if (result.getRowDimension() > 1) {
            double shift0 = result.get(0, 0);
            for (int j = 0; j < result.getRowDimension(); ++j) {
                if (FixToZeroShift) {
                    result.set(j, 0, 0);
                } else {
                    result.set(j, 0, result.get(j, 0) - shift0);
                }

            }
        }
        return result;
    }

        
    
    private void preventSingularMatrix(float[] w) {
        
        int count = 0;
        for (int j = 0; j < w.length - 1; ++j) {
            if (w[j] > 0) {
                count++;
            }
        }
        if (count < 2) {
            Arrays.fill(w, 1);
        }
        
    }
    
    
    
    private static Matrix pinv(Matrix A) {
        
        SingularValueDecomposition svd = A.svd();
        Matrix S = svd.getS();
        Matrix U = svd.getU();
        Matrix V = svd.getV();
        double norm2 = svd.norm2();
        double[] sv = svd.getSingularValues();
        int maxA = Math.max(A.getRowDimension(), A.getColumnDimension());
        double tolerance = maxA * norm2 * eps;
        for (int j = 0; j < sv.length; ++j) {
            if (sv[j] >= tolerance) {
                S.set(j, j, 1 / sv[j]);
            } else {
                S.set(j, j, 0);
            }
        }
        
        return (U.times(S)).times(V.transpose());
    }
    
    
    
    private Matrix buildATWAMatrix(int nTraces, int nt, float[] w) {
        
        Matrix ATWA = new Matrix(nTraces, nTraces);
        for (int j = 0; j < nTraces; ++j) {
            for (int k = 0; k < nTraces; ++k) {
                ATWA.set(j, k, 0);
            }
        }

        int index = 1;
        for (int j = 0; j < nTraces - 1; ++j) {
            int len = nTraces - j - 1;
            int mm = j + 1;
            for (int k = index - 1; k < index + len - 1; ++k) {
                ATWA.set(j, mm++, -w[k]);
            }
            index += len;
        }

        for (int j = 0; j < nTraces; ++j) {
            for (int k = 0; k < nTraces; ++k) {
                ATWA.set(k, j, ATWA.get(j, k));
            }
        }

        for (int j = 0; j < nTraces; ++j) {
            double sum = 0;
            for (int k = 0; k < nTraces; ++k) {
                sum += ATWA.get(j, k);
            }
            ATWA.set(j, j, -sum);
        }
        for (int j = 0; j < nTraces; ++j) {
            for (int k = 0; k < nTraces; ++k) {
                ATWA.set(j, k, ATWA.get(j, k) + w[nt - 1]);
            }
        }
        return ATWA;
    }
    
    

    private Matrix buildATWDTMatrix(int nTraces, float[] w, Matrix CCshift) {
        
        Matrix ATWDT = new Matrix(nTraces, 1);
        int index = 0;
        for (int j = 0; j < nTraces - 1; ++j) {
            for (int k = j + 1; k < nTraces; ++k) {
                w[index] *= CCshift.get(j, k);
                ++index;
            }
        }

        index = 1;
        for (int j = 0; j < nTraces - 1; ++j) {
            int len = nTraces - j - 1;
            double sum = 0;
            for (int k = index - 1; k < index + len - 1; ++k) {
                sum += w[k];
            }
            ATWDT.set(j, 0, sum);
            index += len;
        }

        index = 0;
        for (int j = 0; j < nTraces - 1; ++j) {
            int start = j + 1;
            for (int k = start; k < nTraces; ++k) {
                ATWDT.set(k, 0, ATWDT.get(k, 0) - w[index]);
                ++index;
            }
        }
        
        return ATWDT;
    }    
    
}
