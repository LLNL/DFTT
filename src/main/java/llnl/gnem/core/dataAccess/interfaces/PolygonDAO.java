/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.interfaces;

import java.util.Collection;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.polygon.Polygon;
import llnl.gnem.core.polygon.PolygonSet;
import llnl.gnem.core.polygon.PolygonSetType;

/**
 *
 * @author dodge1
 */
public interface PolygonDAO {

    int insertPolygon(final int polygonSetId, Polygon polygon) throws DataAccessException;

    void updatePolygonSetBounds(PolygonSet set) throws DataAccessException;

    void deletePolygonSet(PolygonSet set) throws DataAccessException;

    Integer polygonExistsInPolygonSet(int polygonSetId, Polygon poly)
            throws DataAccessException;

    PolygonSet retrievePolyset(int polysetid) throws DataAccessException;

    PolygonSet insertPolygonSet(PolygonSetType type, String name, Collection<Polygon> polygons)
            throws DataAccessException;

    public void deletePolygon(int polyid)
            throws DataAccessException;

    public Collection<PolygonSet> getAllPolygonSets() throws DataAccessException;
}
