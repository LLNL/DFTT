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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;

import llnl.gnem.dftt.core.database.ConnectionManager;
import llnl.gnem.dftt.core.database.DbCommandLineParser;
import llnl.gnem.dftt.core.io.SAC.SACFile;
import llnl.gnem.dftt.core.io.SAC.SACHeader;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.FileUtil.DriveMapper;
import llnl.gnem.dftt.core.util.FileUtil.FileFinder;
import llnl.gnem.dftt.core.util.StreamKey;
import llnl.gnem.dftt.core.util.TimeT;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 *
 * @author dodge
 */
public class SacFileLoader {

    private String wfdiscName = "continuous_wfdisc";
    private String regExString;

    private static final int BLOCK_SIZE_SECONDS = 7200;
    private DuplicateWaveformAction duplicateAction = DuplicateWaveformAction.skip;
    private String networkString = "-";
    private String baseDir;

    private DbCommandLineParser parser;

    private void run() throws Exception {

        File curDir = new File(".");
        Collection<String> files = FileFinder.findMatchingFilesUsingRegex(regExString, curDir);
        for (String string : files) {
            loadSingleSacFile(string);
        }
    }

    private void printUsage(Options options) {
        String usage = "SacFileLoader login/password@instance  [options]";

        StringBuilder footer = new StringBuilder("\n");

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(usage, "", options, footer.toString(), false);
    }

    private void initializeConnection() throws Exception {

        ConnectionManager.getInstance(parser.getCredentials().username, parser.getCredentials().password, parser.getCredentials().instance);
    }

    public SacFileLoader(String[] args) throws Exception {

        getCommandLineInfo(args);
        initializeConnection();

    }

    public static void main(String[] args) {

        DriveMapper.setupWindowsNFSDriveMap();

        SacFileLoader runner;
        try {
            runner = new SacFileLoader(args);
            runner.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getCommandLineInfo(String[] args) throws IOException {

        Option help = new Option("h", "help", false, "Show this message");
        Option regexOption = new Option("r", "SAC file Regex", true, "A regular expression to match SAC file names e.g. '.+sac'");
        Option netOption = new Option("n", "Network", true, "A network code to use for the data being loaded");
        Option baseDirOption = new Option("b", "BaseDirectory", true, "The directory below which data files will be written.");
        Option duplicateActionOption = new Option("d", "duplicateAction", true,
                "Specifies the action to take if an existing waveform for same channel and time is found. Available actions are: {Ignore, skip, replaceExisting} (Defaults to " + DuplicateWaveformAction.skip + ")");

        regexOption.setRequired(true);

        Options options = new Options();
        options.addOption(help);

        options.addOption(regexOption);
        netOption.setRequired(false);
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
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption(help.getOpt())) {
                printUsage(options);
                System.exit(0);
            }

            regExString = cmd.hasOption(regexOption.getOpt()) ? cmd.getOptionValue(regexOption.getOpt()) : ".+sac";
            baseDir = cmd.getOptionValue(baseDirOption.getOpt());
            networkString = cmd.hasOption(netOption.getOpt()) ? cmd.getOptionValue(netOption.getOpt()) : "-";

            String tmp = cmd.getOptionValue(duplicateActionOption.getOpt());
            if (tmp != null) {
                duplicateAction = DuplicateWaveformAction.valueOf(tmp);
            }

        } catch (org.apache.commons.cli.ParseException ex) {
            System.err.println(ex.getMessage());
            printUsage(options);
            System.exit(2);
        }
    }

    private void loadSingleSacFile(String fileName) throws IOException, ParseException, SQLException, DataAccessException {

        Connection conn = null;

        try {
            conn = ConnectionManager.getInstance().checkOut();
            File file = new File(fileName);
            System.out.println(fileName);
            SACFile sacfile = new SACFile(file);
            SACHeader header = sacfile.getHeader();
            float[] floatData = sacfile.getData();
            int[] data = Utility.convertToInts(floatData);
            if (data.length < 2) { //one or zero samples indicates a problem. Don't try loading.
                return;
            }
            Double calib = null;
            Double calper = null;
            double beginTime = header.getBeginTime().getEpochTime();
            double sampRate = 1.0 / header.delta;
            int nsamps = data.length;
            StreamKey key = makeStreamKey(header);
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
                    duplicateAction, wfdiscName, conn);

        } finally {

            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }

        }

    }

    private StreamKey makeStreamKey(SACHeader header) {
        String sta = header.kstnm.trim();
        if (sta.isEmpty() || sta.equals("-12345")) {
            throw new IllegalStateException("KSTNM must be set!");
        }
        String chan = header.kcmpnm.trim();
        if (chan.isEmpty() || chan.equals("-12345")) {
            throw new IllegalStateException("KCMPNM must be set!");
        }
        String khole = header.khole.trim();
        if (khole.isEmpty() || khole.equals("-12345")) {
            khole = "--";
        }
        return new StreamKey(networkString, sta, chan, khole);
    }

}
