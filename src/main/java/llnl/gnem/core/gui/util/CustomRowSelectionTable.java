package llnl.gnem.core.gui.util;

/**
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2005 Lawrence Livermore National Laboratory.
 * User: dodge1
 * Date: Feb 8, 2006
 */
public interface CustomRowSelectionTable {
    int getCustomSelectedRow();
    void clearCustomSelectedRow();
    void setCustomSelectedRow( int row );
}
