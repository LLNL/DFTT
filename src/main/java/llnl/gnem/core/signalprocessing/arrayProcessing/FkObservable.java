/**
 * Class used to allow interested Observers to register for progress notification
 * during computation of FK. Allows implementation of progress bar.
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2005 Lawrence Livermore National Laboratory.
 * User: dodge1
 * Date: Feb 22, 2006
 */
package llnl.gnem.core.signalprocessing.arrayProcessing;

import llnl.gnem.core.util.Pair;

import java.util.Observable;

public class FkObservable extends Observable {
    private static final FkObservable ourInstance = new FkObservable();

    public static FkObservable getInstance()
    {
        return ourInstance;
    }

    private FkObservable()
    {
    }

    public void notifyObserversCurrentProgress( int current, int total )
    {
        setChanged();
        notifyObservers( new Pair( current, total ) );
    }
}
