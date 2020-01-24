package llnl.gnem.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Created by dodge1
 * Date: Mar 17, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public final class LogFormatter extends Formatter {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
    @Override
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();

        sb.append(new Date(record.getMillis()))
            .append(' ')
            .append(record.getLevel().getLocalizedName())
            .append(": ")
            .append(formatMessage(record))
            .append(LINE_SEPARATOR);

        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ex) {
                // ignore
            }
        }

        return sb.toString();
    }
}
