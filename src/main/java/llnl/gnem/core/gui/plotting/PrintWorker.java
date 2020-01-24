package llnl.gnem.core.gui.plotting;

import llnl.gnem.core.gui.util.ExceptionDialog;

import java.awt.print.PrinterJob;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

/**
 * Created by: dodge1 Date: Apr 1, 2005 COPYRIGHT NOTICE GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
public class PrintWorker extends SwingWorker<Void, Void> {

    private final PrinterJob job;

    public PrintWorker(PrinterJob job) {
        this.job = job;
    }

    public void finished() {
        try {
            get();
        } catch (InterruptedException | ExecutionException ex) {
            ExceptionDialog.displayError(ex);
        }
    }

    @Override
    protected Void doInBackground() throws Exception {
        job.print();
        return null;
    }

}
