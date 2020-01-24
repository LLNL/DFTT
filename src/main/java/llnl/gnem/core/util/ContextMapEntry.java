package llnl.gnem.core.util;

import net.jcip.annotations.ThreadSafe;

/**
 * Created by dodge1
 * Date: Jul 14, 2009
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */

@ThreadSafe
public class ContextMapEntry {
    private final int contextid;
    private final String name;
    private final String oldsta;
    private final String newsta;

    public ContextMapEntry(int contextid,
                           String name,
                           String oldsta,
                           String newsta)
    {
        this.contextid = contextid;
        this.name = name;
        this.oldsta = oldsta;
        this.newsta = newsta;
    }

    public int getContextid()
    {
        return contextid;
    }

    public String getName()
    {
        return name;
    }

    public String getOldsta()
    {
        return oldsta;
    }

    public String getNewsta()
    {
        return newsta;
    }

    @Override
    public String toString()
    {
        return String.format("%s has been mapped to %s in the %s context.", oldsta, newsta, name);
    }
}
