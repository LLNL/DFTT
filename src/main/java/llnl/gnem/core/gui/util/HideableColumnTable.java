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
package llnl.gnem.core.gui.util;

import org.jdesktop.swingx.JXTable;

import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * An extension of JTable that allows users to easily remove and later replace columns
 * from the display while retaining the columns in the data model. Operations on the
 * table such as sorting are applied to hidden columns so that when they are later made visible
 * the rows are correctly registered.
 * <p></p>
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2005 Lawrence Livermore National Laboratory.
 * User: dodge1
 * Date: Nov 30, 2005
 */
public class HideableColumnTable extends JXTable implements CustomRowSelectionTable {

    private Map<String, ColumnInfo> removedColumns;
    private int customSelectedRow = -1;

    public HideableColumnTable( TableSorter sorter )
    {
        super( sorter );
        removedColumns = new HashMap<String, ColumnInfo>();
    }

    public void clearCustomSelectedRow(){
         customSelectedRow = -1;
         repaint();
     }

    public void removeColumn( String columnName )
    {
        int numColumns = getColumnCount();
        for ( int j = 0; j < numColumns; ++j ){
            TableColumn column = getColumnModel().getColumn( j );
            if( column.getIdentifier().equals( columnName ) ){
                removeColumn( column );
                removedColumns.put( columnName, new ColumnInfo( j, column ) );
                return;
            }
        }
    }


    public int getCustomSelectedRow()
    {
        TableModel tableModel = this.getModel();
        if( tableModel instanceof TableSorter ){
            TableSorter sorter = (TableSorter) tableModel;
            int[] modelToView = sorter.getModelToView();
            if( customSelectedRow >= 0 && modelToView != null && modelToView.length > customSelectedRow ){
                return modelToView[customSelectedRow];
            }
            else
                return -1;
        }
        else
            return customSelectedRow;
    }

    public void setCustomSelectedRow( int row )
    {
        TableModel tableModel = this.getModel();
        if( tableModel instanceof TableSorter ){
            TableSorter sorter = (TableSorter) tableModel;
            row = sorter.modelIndex( row );
        }
        customSelectedRow = row;
        repaint();
    }


    public void replaceColumn( String columnName )
    {
        ColumnInfo aColumn = removedColumns.get( columnName );
        if( aColumn != null ){
            getColumnModel().addColumn( aColumn.column );
            int originalPosition = aColumn.position;
            int maxAvailablePosition = getColumnCount() - 1;
            if( originalPosition < maxAvailablePosition )
                this.moveColumn( maxAvailablePosition, originalPosition );
            removedColumns.remove( columnName );
        }
    }

    class ColumnInfo {
        public int position;
        TableColumn column;

        public ColumnInfo( int position, TableColumn column )
        {
            this.position = position;
            this.column = column;
        }
    }

    public Vector<String> getColumnNames()
    {
        TableModel tableModel = getModel();

        Vector<String> result = new Vector<String>();
        for ( int j = 0; j < tableModel.getColumnCount(); ++j ){
            result.add( tableModel.getColumnName( j ) );
        }
        return result;
    }

    public boolean isColumnVisible( String name )
    {
        return removedColumns.get( name ) == null;
    }
}
