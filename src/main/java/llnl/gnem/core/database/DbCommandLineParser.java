package llnl.gnem.core.database;

import java.util.List;
import llnl.gnem.core.database.login.DbCredentials;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 *
 * @author addair1
 */
public class DbCommandLineParser extends PosixParser {

    private DbCredentials dbCredentials = null;

    @Override
    public CommandLine parse(Options options, String[] args) throws ParseException {
        CommandLine cmd = super.parse(options, args);

        List<String> targets = cmd.getArgList();
        if (targets.isEmpty()) {
            throw new ParseException("No database credentials provided!");
        }
        dbCredentials = new DbCredentials(targets.get(0));

        return cmd;
    }

    public DbCredentials getCredentials() {
        return dbCredentials;
    }

    @Override
    public String toString() {
        return "DbCommandLineParser [dbCredentials=" + dbCredentials + "]";
    }

}
