/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.util.waveformLoading;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.database.DbCommandLineParser;
import llnl.gnem.core.database.column.CssVersion;
import llnl.gnem.core.database.row.ColumnSet;

import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.FileUtil.FileFinder;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.waveform.Wfdisc;
import llnl.gnem.core.waveform.io.BinaryData;
import llnl.gnem.core.waveform.io.BinaryDataReader;
import llnl.gnem.core.waveform.io.css.PathType;
import llnl.gnem.core.waveform.io.css.WfdiscReader;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author dodge
 */
public class CssWfdiscLoader {

    private static final int BLOCK_SIZE_SECONDS = 7200;
    private PathType pathType = PathType.FilePathPlusRel;
    private String userPath = null;

    private DuplicateWaveformAction duplicateAction = DuplicateWaveformAction.skip;
    private String wfdiscTableName = "continuous_wfdisc";
    private String regExString;
    private String networkString = "-";
    private String baseDir;
    private DbCommandLineParser parser;

    private void run() throws FileNotFoundException {
        ColumnSet.setVersion(CssVersion.Css30);
        File curDir = new File(".");
        Collection<String> files = FileFinder.findMatchingFilesUsingRegex(regExString, curDir);
        for (String string : files) {
            System.out.println("Loading file: " + string + " ...");
            try {
                loadThisFile(string);
            } catch (Exception ex) {
                ApplicationLogger.getInstance().log(Level.SEVERE, "Failed loading: " + string, ex);
            }
        }
        System.out.println("Done.");
    }

    private void printUsage(Options options) {
        String usage = "CssWfdiscLoader login/password@instance  [options]";

        StringBuilder footer = new StringBuilder("\n");
        footer.append("Note: PathType = FilePath means that the path to the wfdisc file is the same as the path to the .w files.");
        footer.append("\n\tPathType = FilePathPlusRel means that the path to the .w files is the wfdisc file path + the relative path in the wfdisc file.");
        footer.append("\n\tPathType = Internal means that the dir in the wfdisc file will be used. This requires that dir be fully-qualified.");

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(usage, "", options, footer.toString(), false);
    }

    private void initializeConnection() throws Exception {
        ConnectionManager.getInstance(parser.getCredentials().username, parser.getCredentials().password, parser.getCredentials().instance);
    }

    public CssWfdiscLoader(String[] args) throws Exception {
        getCommandLineInfo(args);
        initializeConnection();

    }

    public static void main(String[] args) {

        CssWfdiscLoader runner;
        try {
            runner = new CssWfdiscLoader(args);
            runner.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getCommandLineInfo(String[] args) throws IOException {

        try {
            Option help = new Option("h", "help", false, "Show this message");
            Option regexOption = new Option("w", "WfdiscFileRegex", true, "A regular expression to match CSS WFDISC file names e.g. '.+wfdisc'");
            Option pathTypeOption = new Option("p", "PathType", true, "Specifies how WFDISC DIR will be set. One of (Internal, FilePath, UserSpecified,FilePathPlusRel ) (Defaults to Internal)");
            Option userPathOption = new Option("u", "UserPath", true, "When PathType is UserSpecified then the fully-qualified path to the .w files is specified here.");
            Option netOption = new Option("n", "Network", true, "A network code to use for the data being loaded");
            Option baseDirOption = new Option("b", "BaseDirectory", true, "The directory below which data files will be written.");
            Option duplicateActionOption = new Option("d", "duplicateAction", true,
                    "Specifies the action to take if an existing waveform for same channel and time is found. Available actions are: {Ignore, skip, replaceExisting} (Defaults to " + DuplicateWaveformAction.skip + ")");

            regexOption.setRequired(true);
            pathTypeOption.setRequired(false);
            userPathOption.setRequired(false);
            netOption.setRequired(false);

            Options options = new Options();
            options.addOption(help);
            options.addOption(regexOption);
            options.addOption(pathTypeOption);
            options.addOption(userPathOption);
            options.addOption(netOption);

            baseDirOption.setRequired(true);
            baseDirOption.setType(String.class);
            options.addOption(baseDirOption);
            duplicateActionOption.setRequired(false);
            options.addOption(duplicateActionOption);

            if (args.length == 0 || args[0].trim().isEmpty()) {
                printUsage(options);
                System.exit(2);
            }

            parser = new DbCommandLineParser();
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption(help.getOpt())) {
                printUsage(options);
                System.exit(0);
            }
            baseDir = cmd.getOptionValue(baseDirOption.getOpt());
            regExString = cmd.hasOption(regexOption.getOpt()) ? cmd.getOptionValue(regexOption.getOpt()) : ".+wfdisc";
            networkString = cmd.hasOption(netOption.getOpt()) ? cmd.getOptionValue(netOption.getOpt()) : "-";
            if (cmd.hasOption(pathTypeOption.getOpt())) {
                String tmp = cmd.getOptionValue(pathTypeOption.getOpt());
                if (tmp != null) {
                    pathType = PathType.valueOf(tmp);
                }
            }

            String tmp = cmd.getOptionValue(duplicateActionOption.getOpt());
            if (tmp != null) {
                duplicateAction = DuplicateWaveformAction.valueOf(tmp);
            }
            if (pathType == PathType.UserSpecified) {
                if (cmd.hasOption(userPathOption.getOpt())) {
                    tmp = cmd.getOptionValue(userPathOption.getOpt());
                    if (tmp == null) {
                        System.err.println("User-supplied path option was chosen, but failed to specify path!");
                        printUsage(options);
                        System.exit(1);
                    } else {
                        userPath = tmp;
                    }
                }
            }
        } catch (ParseException ex) {
            Logger.getLogger(CssWfdiscLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void loadThisFile(String wfdiscFlatFileName) throws Exception {
        Path inputFile = Paths.get(wfdiscFlatFileName).normalize();
        Path parent = inputFile.getParent();

        ArrayList<Wfdisc> rows = WfdiscReader.readSpaceDelimitedWfdiscFile(wfdiscFlatFileName);
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            for (Wfdisc row : rows) {
                String dfile = WfdiscReader.getFullyQualifiedDfileName(row, pathType, parent, userPath);
                int[] data = getSeismogramDataAsIntArray(dfile, row.getFoff(), row.getNsamp(), row.getDatatype());
                if (data.length < 2) { //one or zero samples indicates a problem. Don't try loading.
                    continue;
                }
                Double calib = row.getCalib();
                Double calper = row.getCalper();
                double beginTime = row.getTime();
                double sampRate = row.getSamprate();
                int nsamps = row.getNsamp();
                StreamKey key = new StreamKey(networkString, row.getSta(), row.getChan(), "--");

                int blockSizeSeconds = BLOCK_SIZE_SECONDS;
                Utility.writeDataBlocks(sampRate,
                        blockSizeSeconds,
                        nsamps,
                        baseDir,
                        beginTime,
                        data,
                        key,
                        calib,
                        calper,
                        duplicateAction, wfdiscTableName, conn);
            }

        } finally {

            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    public static int[] getSeismogramDataAsIntArray(String fname, int foff, int nsamp, String datatype) throws Exception {
        BinaryDataReader bdr = BinaryDataReader.getReader(datatype);
        if (bdr != null) {
            BinaryData bd = bdr.readData(fname, foff, nsamp);
            return bd.getIntData();
        } else {
            throw new IllegalStateException("No BinaryDataReader was instantiated. Could not read data.");
        }

    }

}
