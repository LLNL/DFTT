package llnl.gnem.core.util;

import java.io.IOException;

/**
 * Created to fill in for java.nio.file.FileSystemException unitl we start using 1.7 when the nio package becomes available.
 * Created by dodge1
 * Date: Apr 26, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class FileSystemException extends IOException {
    public FileSystemException( String message )
    {
        super(message);
    }
}
