/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2023 Lawrence Livermore National Laboratory (LLNL)
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
package llnl.gnem.apps.detection.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import llnl.gnem.apps.detection.core.dataObjects.TemplateSerializationType;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;
import llnl.gnem.apps.detection.dataAccess.ApplicationRoleManager;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.interfaces.SubspaceTemplateDAO;
import llnl.gnem.dftt.core.dataAccess.DAOFactory;
import llnl.gnem.dftt.core.database.DbCommandLineParser;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.FileUtil.DriveMapper;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


public class BulkTemplateDumper {

    private DbCommandLineParser parser;
    private int runid;

    private void printUsage(Options options) {
        String usage = "BulkTemplateDumper login/password@instance  [options]";

        String footer = "\n";

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(usage, "", options, footer, false);
    }

    private void initializeConnection() throws Exception {
        DAOFactory.getInstance(parser.getCredentials().username, parser.getCredentials().password, parser.getCredentials().instance, new ApplicationRoleManager());
   }

    private void getCommandLineInfo(String[] args) throws IOException {

        Options options = new Options();
        Option runidOption = new Option("r", "Runid", true, "the runid that references the templates to dump. Any detector with at least one detection will be exported.");
        options.addOption(runidOption);
        runidOption.setRequired(true);
        runidOption.setType(Number.class);
        Option help = new Option("h", "help", false, "Show this message");
        options.addOption(help);

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
            if (cmd.hasOption(runidOption.getOpt())) {
                runid = ((Number) cmd.getParsedOptionValue(runidOption.getOpt())).intValue();
            }

        } catch (ParseException ex) {
            System.err.println(ex.getMessage());
            printUsage(options);
            System.exit(2);
        }
    }

    public BulkTemplateDumper(String[] args) throws IOException, Exception {
        getCommandLineInfo(args);
        initializeConnection();

    }

    public static void main(String[] args) {

        try {
            BulkTemplateDumper runner = new BulkTemplateDumper(args);
            DriveMapper.setupWindowsNFSDriveMap();
            runner.run();
        } catch (Exception e) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "General Failure!", e);
        }

    }

    private void run() throws Exception {

        TemplateSerializationType type = TemplateSerializationType.SACFILE;
        Connection conn = null;

        SubspaceTemplateDAO std = DetectionDAOFactory.getInstance().getSubspaceTemplateDAO();
        try {
            conn =  DAOFactory.getInstance().getConnections().checkOut();
            String sql = String.format("select distinct detectorid from detection where runid=%d", runid);
            try ( PreparedStatement stmt = conn.prepareStatement(sql)) {
                try ( ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int detectorid = rs.getInt(1);
                        System.out.println(detectorid);
                        SubspaceTemplate template = std.getSubspaceTemplate(detectorid);
                        template.serialize(".", detectorid, type);
                    }
                }
            }
        } finally {

            if (conn != null) {
                DAOFactory.getInstance().getConnections().checkIn(conn);
            }
        }
    }

}
