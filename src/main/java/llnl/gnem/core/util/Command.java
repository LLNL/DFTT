package llnl.gnem.core.util;

/**
 * Created by dodge1
 * Date: Mar 24, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public interface Command {

    boolean execute();

    boolean unexecute();

    boolean isAllowable();

    boolean isReversible();

    boolean isRunInNewThread();

}
