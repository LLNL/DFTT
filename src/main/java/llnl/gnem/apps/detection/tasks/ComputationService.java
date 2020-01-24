package llnl.gnem.apps.detection.tasks;


import llnl.gnem.apps.detection.core.dataObjects.CancellationData;
import llnl.gnem.apps.detection.core.dataObjects.Trigger;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import java.util.Collection;
import llnl.gnem.core.util.ApplicationLogger;

import java.util.concurrent.*;
import java.util.logging.Level;
import llnl.gnem.apps.detection.core.framework.DetectionStatistic;
import llnl.gnem.core.util.PairT;


/**
 * Created by dodge1
 * Date: Oct 14, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class ComputationService {

    private final ExecutorService exec;
    private final ExecutorService exec2;
    private final ExecutorService exec3;
    private final ExecutorService exec4;
    private final ExecutorService exec5;
    private final ExecutorService exec6;
    private final CompletionService<DetectionStatistic> detStatCompService;
    private final CompletionService<Void> processBlockCompService;
    private final CompletionService<Void> cancellationCompService;
    private final CompletionService<Void> retrieveDataCompService;
    private final CompletionService<PairT<Integer, Double>> getProjectionService;
    private final CompletionService<CancellationData> processCancellationDataService;
    

    public CompletionService<DetectionStatistic> getDetStatCompService() {
        return detStatCompService;
    }

    public CompletionService<Void> getProcessBlockCompService() {
        return processBlockCompService;
    }

    public CompletionService<Void> getCancellationCompService() {
        return cancellationCompService;
    }

    public CompletionService<Void> getRetrieveDataCompService() {
        return retrieveDataCompService;
    }

    public CompletionService<CancellationData> getProcessCancellationDataService() {
        return processCancellationDataService;
    }

    private static class DetectionStatComputationServiceHolder {

        private static final ComputationService instance = new ComputationService();
    }

    public CompletionService<PairT<Integer, Double>> getProjectionComputationService() {
        return getProjectionService;
    }

    public static ComputationService getInstance() {
        return DetectionStatComputationServiceHolder.instance;
    }

    private ComputationService() {
        int nthreads = ProcessingPrescription.getInstance().getNumberOfThreads();

        ApplicationLogger.getInstance().log(Level.INFO, String.format("Application is using %d threads per pool...", nthreads));
        exec = Executors.newFixedThreadPool(nthreads);
        exec2 = Executors.newFixedThreadPool(nthreads);
        exec3 = Executors.newFixedThreadPool(nthreads);
        exec4 = Executors.newFixedThreadPool(nthreads);
        exec5 = Executors.newFixedThreadPool(1);
        exec6 = Executors.newFixedThreadPool(nthreads);
        processBlockCompService = new ExecutorCompletionService<>(exec);
        detStatCompService = new ExecutorCompletionService<>(exec2);
        cancellationCompService = new ExecutorCompletionService<>(exec3);
        getProjectionService = new ExecutorCompletionService<>(exec4);
        retrieveDataCompService = new ExecutorCompletionService<>(exec5);
        processCancellationDataService = new ExecutorCompletionService<>(exec6);
    }
    
    public ExecutorService getBlockRetrievalExecutorService()
    {
        return exec5;
    }

    public void shutdown() throws InterruptedException {
        exec.shutdown();
        exec.awaitTermination(10, TimeUnit.SECONDS);
        ApplicationLogger.getInstance().log(Level.INFO, "Thread pool 1 shut down.");
        exec2.shutdown();
        exec2.awaitTermination(10, TimeUnit.SECONDS);
        ApplicationLogger.getInstance().log(Level.INFO, "Thread pool 2 shut down.");
        exec3.shutdown();
        exec3.awaitTermination(10, TimeUnit.SECONDS);
        ApplicationLogger.getInstance().log(Level.INFO, "Thread pool 3 shut down.");
        exec4.shutdown();
        exec4.awaitTermination(10, TimeUnit.SECONDS);
        ApplicationLogger.getInstance().log(Level.INFO, "Thread pool 4 shut down.");
        exec5.shutdown();
        exec5.awaitTermination(10, TimeUnit.SECONDS);
        ApplicationLogger.getInstance().log(Level.INFO, "Thread pool 5 shut down.");
        exec6.shutdown();
        exec6.awaitTermination(10, TimeUnit.SECONDS);
        ApplicationLogger.getInstance().log(Level.INFO, "Thread pool 6 (last) shut down.");
        System.exit(0);
    }
}
