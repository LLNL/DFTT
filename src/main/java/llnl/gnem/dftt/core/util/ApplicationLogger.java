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
package llnl.gnem.dftt.core.util;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;
import llnl.gnem.dftt.core.gui.util.ExceptionDialog;
import llnl.gnem.dftt.core.gui.util.GuiHandler;
import llnl.gnem.dftt.core.gui.util.MessageDialog.PostDisplayAction;
import llnl.gnem.dftt.core.gui.util.ViewLogAction;

public class ApplicationLogger {

    private static final ApplicationLogger ourInstance = new ApplicationLogger();
    private final Logger logger;
    private LogFormatter formatter;
    private FileHandler fileHandler;
    private ConsoleHandler consoleHandler;
    private boolean guiEnabled;
    private final Map<String, Level> levelMap;

    public static ApplicationLogger getInstance() {
        return ourInstance;
    }

    private ApplicationLogger() {
        logger = Logger.getLogger("ApplicationLog");
        logger.setLevel(Level.ALL);
        levelMap = new HashMap<String, Level>();
        levelMap.put("ALL", Level.ALL);
        levelMap.put("SEVERE", Level.SEVERE);
        levelMap.put("WARNING", Level.WARNING);
        levelMap.put("INFO", Level.INFO);
        levelMap.put("CONFIG", Level.CONFIG);
        levelMap.put("FINE", Level.FINE);
        levelMap.put("FINER", Level.FINER);
        levelMap.put("FINEST", Level.FINEST);
        levelMap.put("OFF", Level.OFF);
        guiEnabled = true;
    }

    public static String getAllLevelsString() {
        return "ALL, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, OFF";
    }

    public Collection<String> getAvailableLevels() {
        return levelMap.keySet();
    }

    public void setLevel(String level) {
        Level alevel = levelMap.get(level.toUpperCase());
        if (alevel != null) {
            setLevel(alevel);
        } else {
            throw new IllegalArgumentException("Invalid Log level String: " + level);
        }
    }

    public void setLevel(Level level) {
        logger.setLevel(level);
        if (fileHandler != null) {
            fileHandler.setLevel(level);
        }
        if (consoleHandler != null) {
            consoleHandler.setLevel(level);
        }
    }

    public void setFileHandler(String appName, boolean usePID) throws IOException {
        String tmpDir = System.getProperty("user.home");
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        String tmp = usePID ? String.format("%s.%s", appName, pid): String.format("%s", appName);
        String pattern = tmpDir + File.separator + tmp + ".log%g";
        int limit = 10000000; // 10 mbyte
        int numLogFiles = 3;
        fileHandler = new FileHandler(pattern, limit, numLogFiles);
        logger.addHandler(fileHandler);
        formatter = new LogFormatter();
        fileHandler.setFormatter(formatter);
        logger.setUseParentHandlers(false);

    }

    public void setGuiWarnings(boolean guiOn) {
        guiEnabled = guiOn;
    }

    public void useConsoleHandler() {
        consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        consoleHandler.setLevel(Level.INFO);
        logger.addHandler(consoleHandler);
    }

    public void addHandler(Handler handler) {
        handler.setFormatter(formatter);
        logger.addHandler(handler);
    }

    public void log(Level level, String message) {
        logger.log(level, message);
    }

    public void log(Level level, String message, Exception e) {
        logger.log(level, message, e);
    }

    public void reportException(Exception e) {
        reportException("Unknown Source", e);
    }

    public void reportException(String source, Exception e) {
        if (hasGui()) {
            ExceptionDialog.displayError(e);
        } else {
            log(Level.WARNING, source, e);
        }
    }

    public boolean hasGui() {
        return guiEnabled;
    }

    /**
     * Read various flags and system properties to set logging of interesting
     * and/or bad happenings in a code.
     * 
     * @param appName
     * @throws IOException
     */
    public static void configureLogging(String appName) throws IOException {
        ApplicationLogger.getInstance().setFileHandler(appName, false);
        String aLevel = System.getProperty("LOGLEVEL");
        Level level = aLevel == null ? Level.INFO : Level.parse(aLevel);
        ApplicationLogger.getInstance().setLevel(level);
        String useGuiLogger = System.getProperty("USE_GUI_ERROR_LOGGER");
        if (useGuiLogger != null && useGuiLogger.equalsIgnoreCase("TRUE")) {
            ApplicationLogger.getInstance().addHandler(GuiHandler.getInstance());
            GuiHandler.getInstance().setApplicationName(appName);
            ViewLogAction.getInstance(GuiHandler.getInstance()).setEnabled(true);
        }

        String useConsoleLogger = System.getProperty("USE_CONSOLE_LOGGER");
        if (useConsoleLogger != null && useConsoleLogger.equalsIgnoreCase("TRUE")) {
            ApplicationLogger.getInstance().useConsoleHandler();
        }
        ExceptionDialog.setPostDisplayAction(PostDisplayAction.LOG_MESSAGE);
    }
}
