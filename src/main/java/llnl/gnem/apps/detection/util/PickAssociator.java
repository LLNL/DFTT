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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import llnl.gnem.dftt.core.dataAccess.DAOFactory;
import llnl.gnem.dftt.core.dataAccess.database.oracle.OracleDBUtil;
import llnl.gnem.dftt.core.database.DbCommandLineParser;
import llnl.gnem.dftt.core.database.GenericRoleManager;
import llnl.gnem.dftt.core.database.Role;
import llnl.gnem.dftt.core.util.TimeT;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
// import java.util.Scanner;

/**
 *
 * @author dodge1
 */
public class PickAssociator {

    private DbCommandLineParser parser;
    private Double maxPickError = 3.0;
    private int minPhases = 4;
    private String phaseToUse = null;
    private String outputFile;

    private PickAssociator(String[] args) throws IOException, Exception {
        getCommandLineInfo(args);
        initializeConnection();
    }

    private void printUsage(Options options) {
        String usage = "PickAssociator login/password@instance  [options]";

        String footer = "\nThis program is for use in creating Bayesloc input from phase picks.";

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(usage, "", options, footer, false);
    }

    private void initializeConnection() throws Exception {
        Collection<Role> roles = new ArrayList<>();
        GenericRoleManager grm = new GenericRoleManager(roles);

        DAOFactory.getInstance(parser.getCredentials().username,
                parser.getCredentials().password,
                parser.getCredentials().instance,
                grm);
    }

    private void getCommandLineInfo(String[] args) throws IOException {

        Options options = new Options();

        Option help = new Option("h", "help", false, "Show this message");
        Option filenameOption = new Option("f", "FileName", true, "The name of the file to which picks will be written..");
        Option restrictToPhaseOption = new Option("r", "RestrictToPhase", true, "Only accept the specified phase");
        Option maxPickErrorOption = new Option("m", "MaxPickError", true, "Only picks with uncertainty <= this value are accepted.(Default = 3.0)");
        Option minPhasesOption = new Option("n", "MinPhasesPerEvent", true, "At least this many phases must be present in valid event (default = 4)");

        restrictToPhaseOption.setType(String.class);
        maxPickErrorOption.setType(Number.class);
        filenameOption.setType(String.class);
        minPhasesOption.setType(Number.class);

        filenameOption.setRequired(true);
        restrictToPhaseOption.setRequired(false);
        maxPickErrorOption.setRequired(false);
        minPhasesOption.setRequired(false);

        options.addOption(help);
        options.addOption(filenameOption);
        options.addOption(restrictToPhaseOption);
        options.addOption(maxPickErrorOption);
        options.addOption(minPhasesOption);

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

            outputFile = cmd.getOptionValue(filenameOption.getOpt());
            if (cmd.hasOption(restrictToPhaseOption.getOpt())) {
                phaseToUse = cmd.getOptionValue(restrictToPhaseOption.getOpt());
            }

            if (cmd.hasOption(maxPickErrorOption.getOpt())) {
                maxPickError = ((Number) cmd.getParsedOptionValue(maxPickErrorOption.getOpt())).doubleValue();
            }

            if (cmd.hasOption(minPhasesOption.getOpt())) {
                minPhases = ((Number) cmd.getParsedOptionValue(minPhasesOption.getOpt())).intValue();
            }

        } catch (ParseException ex) {
            System.err.println(ex.getMessage());
            printUsage(options);
            System.exit(2);
        }
    }

    public static void main(String[] args) {

        try {
            PickAssociator du = new PickAssociator(args);
            du.run();
        } catch (Exception ex) {
            Logger.getLogger(PickAssociator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() throws Exception {
        NumberFormat fmt4 = new DecimalFormat("0.####");

        Connection conn = null;
        try (PrintWriter fileWriter = new PrintWriter(outputFile)) {

            fileWriter.println("ev_id sta_id phase time");

            Event currentEvent = null;
            Map<Integer, Collection<PickData>> evidPickMap = new HashMap<>();
            Collection<Event> events = new ArrayList<>();
            conn = DAOFactory.getInstance().getConnections().checkOut();
            String sql = buildPrimaryQuery();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int jdx = 1;
                        String sta = rs.getString(jdx++);
                        int detectionid = rs.getInt(jdx++);
                        String phase = rs.getString(jdx++);
                        double time = rs.getDouble(jdx++);
                        double std = rs.getDouble(jdx++);
                        PickData pd = new PickData(sta, detectionid, phase, time, std);
                        if (currentEvent == null) {
                            int evid = (int) OracleDBUtil.getNextId(conn, "eventid");
                            currentEvent = new Event(evid);
                            events.add(currentEvent);
                            currentEvent.addPick(pd);
                        } else {
                            if (currentEvent.isCompatible(pd)) {
                                currentEvent.addPick(pd);
                            } else {
                                int evid = (int) OracleDBUtil.getNextId(conn, "eventid");
                                currentEvent = new Event(evid);
                                events.add(currentEvent);
                                currentEvent.addPick(pd);
                            }
                        }
                    }
                }
            }

            int criterionMatchCount = 0;
            for (Event event : events) {
                if (event.getNumPicks() >= minPhases) {
                    System.out.println(event);
                    System.out.println("");
                    if (event.picks != null) {
                        for (PickData pd : event.picks) {
                            fileWriter.println(event.getEvid() + " " + pd.getSta() + " " + pd.getPhase() + " " + fmt4.format(pd.getTime()));
                        }
                    }
                    criterionMatchCount++;
                }
            }

            System.out.println(criterionMatchCount + " met criterion.");

            fileWriter.flush();
            try (PrintWriter fw2 = new PrintWriter("CC_0.8.2.txt")) {
                for (Event event : events) {
                    if (event.getNumPicks() >= minPhases) {
                        PickData pd = event.getPickForStationPhase("DESD", "P");
                        TimeT refTime = new TimeT(pd.getTime());
                        double refYear = refTime.getYear() + refTime.getJDay() / 365.25;
                        double elepTime = event.getPickForStationPhase("ELEP", "P").getTime();
                        double desdSTime = event.getPickForStationPhase("DESD", "S").getTime();
                        double touoTime = event.getPickForStationPhase("TOUO", "P").getTime();
                        double mlodTime = event.getPickForStationPhase("MLOD", "P").getTime();
                        double hoveTime = event.getPickForStationPhase("HOVE", "P").getTime();
                        String line = String.format("%f %f %f %f %f %d %f",
                                (elepTime - refTime.getEpochTime()),
                                (desdSTime - refTime.getEpochTime()),
                                (touoTime - refTime.getEpochTime()),
                                (mlodTime - refTime.getEpochTime()),
                                (hoveTime - refTime.getEpochTime()),
                                event.getEvid(), refYear);
                        fw2.println(line);

                    }
                }
            }
            try (PrintWriter fw2 = new PrintWriter("CC_0.8.avg.txt")) {
                for (Event event : events) {
                    if (event.getNumPicks() >= minPhases) {
                        PickData pd = event.getPickForStationPhase("DESD", "P");
                        TimeT refTime = new TimeT(pd.getTime());
                        double refYear = refTime.getYear() + refTime.getJDay() / 365.25;
                        double desdTime = event.getPickForStationPhase("DESD", "P").getTime();
                        double elepTime = event.getPickForStationPhase("ELEP", "P").getTime();
                        double desdSTime = event.getPickForStationPhase("DESD", "S").getTime();
                        double touoTime = event.getPickForStationPhase("TOUO", "P").getTime();
                        double mlodTime = event.getPickForStationPhase("MLOD", "P").getTime();
                        double hoveTime = event.getPickForStationPhase("HOVE", "P").getTime();
                        double avgTime = (desdTime +elepTime+desdSTime+touoTime+mlodTime+hoveTime)/6;
                        String line = String.format("%f %f %f %f %f %f %d %f",
                                (desdTime - avgTime),
                                (elepTime - avgTime),
                                (desdSTime - avgTime),
                                (touoTime - avgTime),
                                (mlodTime - avgTime),
                                (hoveTime - avgTime),
                                event.getEvid(), refYear);
                        fw2.println(line);

                    }
                }
            }

            try (PrintWriter pw3 = new PrintWriter("phase.dat")) {
                for (Event event : events) {
                    if (event.getNumPicks() >= minPhases) {
                        PickData pd = event.getEarliest();
                        if (pd != null) {
                            //      double hypoTime = pd.getTime()-4.0;  
                            TimeT hypoTime = new TimeT(pd.getTime() - 4.0);
                            int year = hypoTime.getYear();
                            int month = hypoTime.getMonth();
                            int day = hypoTime.getDayOfMonth();
                            int hour = hypoTime.getHour();
                            int minute = hypoTime.getMinute();
                            double second = hypoTime.getSecond();
                            double lat = 19.260 + (Math.random()-.5)/100;
                            double lon = -155.414 + (Math.random()-.5)/100;
                            double depth = 35 + (Math.random()-.5);
                            String line = String.format("# %d  %d %d  %d %d %5.2f  %7.4f %8.4f    %5.2f 1.0  0.0  0.0  0.0      %d",
                                    year,
                                    month,
                                    day,
                                    hour,
                                    minute,
                                    second,
                                    lat,
                                    lon,
                                    depth,
                                    event.getEvid());
                            pw3.println(line);
                            for (PickData pd2 : event.picks) {
                                double tt = pd2.getTime() - hypoTime.getEpochTime();
                                line = String.format("%s       %f   1.000   %s", pd2.getSta(), tt, pd2.getPhase());
                                pw3.println(line);

                            }
                        }
                    }
                }
            }
        } finally {
            DAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private String buildPrimaryQuery() {
        String phaseRestrict = phaseToUse == null ? "" : (" and phase = '" + phaseToUse + "'");

        String sql = "select distinct a.station_code,detectionid,phase,round(time,4) time, "
                + "round(pick_std,4) std from phase_pick a, group_station_data b\n"
                + "where a.configid = b.CONFIGID" + phaseRestrict + " and pick_std <= " + maxPickError + " \n"
                + "order by time";
        return sql;
    }

    private static class Event {

        double minPickTime = Double.MAX_VALUE;
        private static final double coincidenceWindow = 10.0;
        private final int evid;
        private final List<PickData> picks;

        public Event(int evid) {
            this.evid = evid;
            picks = new ArrayList<>();
        }

        public int getEvid() {
            return evid;
        }

        public PickData getPickForStationPhase(String sta, String phase) {
            for (PickData pd : picks) {
                if (pd.getSta().equals(sta) && pd.getPhase().equals(phase)) {
                    return pd;
                }
            }
            return null;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 29 * hash + this.evid;
            hash = 29 * hash + Objects.hashCode(this.picks);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Event other = (Event) obj;
            if (this.evid != other.evid) {
                return false;
            }
            if (!Objects.equals(this.picks, other.picks)) {
                return false;
            }
            return true;
        }

        private void addPick(PickData pd) {
            if (pd.getTime() < minPickTime) {
                minPickTime = pd.getTime();
            }
            picks.add(pd);
        }

        private boolean isCompatible(PickData pd) {
            if (pd.getTime() > minPickTime + coincidenceWindow) {
                return false;
            }
            for (PickData pdd : picks) {
                if (pdd.getDetectionid() == pd.getDetectionid() && pdd.getPhase().equals(pd.getPhase())) {
                    return false;
                }
                if (pdd.getSta().equals(pd.getSta()) && pdd.getPhase().equals(pd.getPhase())) {
                    return false;
                }
            }
            return true;
        }

        private int getNumPicks() {
            return picks.size();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("EVID: ");
            sb.append(evid);
            sb.append("\n");
            for (PickData pd : picks) {
                sb.append("\t");
                sb.append(pd.toString());
                sb.append("\n");
            }
            return sb.toString();
        }

        private PickData getEarliest() {
            double minTime = Double.MAX_VALUE;
            PickData result = null;
            for (PickData pd : picks) {
                if (pd.getTime() < minTime) {
                    minTime = pd.getTime();
                    result = pd;
                }
            }
            return result;
        }

    }

    private static class PickData {

        private final String sta;
        private final Integer detectionid;
        private final String phase;
        private final double time;
        private final double std;

        public PickData(String sta, Integer detectionid, String phase, double time, double std) {
            this.sta = sta;
            this.detectionid = detectionid;
            this.phase = phase;
            this.time = time;
            this.std = std;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 23 * hash + Objects.hashCode(this.sta);
            hash = 23 * hash + Objects.hashCode(this.detectionid);
            hash = 23 * hash + Objects.hashCode(this.phase);
            hash = 23 * hash + (int) (Double.doubleToLongBits(this.time) ^ (Double.doubleToLongBits(this.time) >>> 32));
            hash = 23 * hash + (int) (Double.doubleToLongBits(this.std) ^ (Double.doubleToLongBits(this.std) >>> 32));
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final PickData other = (PickData) obj;
            if (Double.doubleToLongBits(this.time) != Double.doubleToLongBits(other.time)) {
                return false;
            }
            if (Double.doubleToLongBits(this.std) != Double.doubleToLongBits(other.std)) {
                return false;
            }
            if (!Objects.equals(this.sta, other.sta)) {
                return false;
            }
            if (!Objects.equals(this.phase, other.phase)) {
                return false;
            }
            if (!Objects.equals(this.detectionid, other.detectionid)) {
                return false;
            }
            return true;
        }

        public String getSta() {
            return sta;
        }

        public Integer getDetectionid() {
            return detectionid;
        }

        public String getPhase() {
            return phase;
        }

        public double getTime() {
            return time;
        }

        public double getStd() {
            return std;
        }

        @Override
        public String toString() {
            return "PickData{" + "sta=" + sta + ", detectionid=" + detectionid + ", phase=" + phase + ", time=" + new TimeT(time).toString() + ", std=" + std + '}';
        }

    }

}
