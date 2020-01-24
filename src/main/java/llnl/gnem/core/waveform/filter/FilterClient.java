/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package llnl.gnem.core.waveform.filter;

/**
 * User: Doug
 * Date: Feb 7, 2009
 * Time: 7:43:52 PM
 */
public interface FilterClient {

    void applyFilter( StoredFilter filter );

    void unApplyFilter();

}
