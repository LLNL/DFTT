package llnl.gnem.apps.detection.tasks;


import llnl.gnem.apps.detection.source.SourceData;
import java.util.concurrent.Callable;

/**
 * Created by dodge1
 * Date: Oct 14, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class RetrieveAllBlocksTask implements Callable<Void> {
   private final SourceData sourceData;
   private final boolean exitOnFileEnd;

    public RetrieveAllBlocksTask(SourceData processor, boolean exitOnFileEnd) {
        this.sourceData = processor;
        this.exitOnFileEnd = exitOnFileEnd;
    }

    @Override
    public Void call() throws Exception {
        sourceData.retrieveAllBlocks(exitOnFileEnd);
        return null;
    }
}