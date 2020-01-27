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
package llnl.gnem.core.database.login;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author dodge1
 */
public class DbCredentials {

    public final String username;
    public final String password;
    public final String instance;

    public DbCredentials(String login) throws ParseException {
        String[] credentials = StringUtils.splitPreserveAllTokens(login, "/");
        username = credentials[0];

        String inputPassword = "";
        String inputInstance = "";
        if (credentials.length != 2) {
            if (inputPassword.isEmpty()) {
                Console cons;
                char[] passwd;
                if ((cons = System.console()) != null && (passwd = cons.readPassword("%s ", "Password:")) != null) {
                    inputPassword = new String(passwd);
                    java.util.Arrays.fill(passwd, ' ');
                }
            }
        } else {
            credentials = StringUtils.splitPreserveAllTokens(credentials[1], "@");
            inputPassword = credentials[0];
            inputInstance = credentials.length > 1 ? credentials[1] : "";

            // Special escape character indicating to use password file
            if (inputPassword.equals("-")) {
                inputPassword = readPasswordFile(username);
                if (inputPassword.isEmpty()) {
                    inputPassword = "-";
                }
            } else if (inputPassword.isEmpty()) {
                Console cons;
                char[] passwd;
                if ((cons = System.console()) != null && (passwd = cons.readPassword("%s ", "Password:")) != null) {
                    inputPassword = new String(passwd);
                    java.util.Arrays.fill(passwd, ' ');
                }
            }
        }

        password = inputPassword;
        instance = inputInstance;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return "DbCredentials [username=" + username + ", instance=" + instance + "]";
    }

    private static Scanner getScanner(File file) {
        try {
            return new Scanner(file);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    private static String readPasswordFile(String username) {
        String password = "";
        File passwordFile = new File(getPasswordFilename());
        Scanner sc;
        if (passwordFile.canRead() && (sc = getScanner(passwordFile)) != null) {
            while (sc.hasNextLine() && password.isEmpty()) {
                StringTokenizer tokens = new StringTokenizer(sc.nextLine(), ":");
                String user = tokens.nextToken();
                String pass = tokens.nextToken();
                if (user.equalsIgnoreCase(username)) {
                    password = pass;
                }
            }
            sc.close();
        }
        return password;
    }

    // TODO make this platform independent, as it does not currently provide any security on Windows
    private static void writePassword(String username, String password) {
        if (!System.getProperty("os.name").startsWith("Windows")) {
            List<String> lines = new ArrayList<String>();
            boolean exists = false;

            File passwordFile = new File(getPasswordFilename());
            Scanner sc;
            if (passwordFile.canRead() && (sc = getScanner(passwordFile)) != null) {
                while (sc.hasNextLine()) {
                    StringTokenizer tokens = new StringTokenizer(sc.nextLine());
                    String user = tokens.nextToken();
                    String pass = tokens.nextToken();
                    if (user.equalsIgnoreCase(username)) {
                        pass = password;
                        exists = true;
                    }

                    lines.add(user + ":" + pass);
                }
                sc.close();
            }

            if (!exists) {
                lines.add(username + ":" + password);
            }

            try {
                FileUtils.writeLines(passwordFile, lines);
                Runtime.getRuntime().exec("chmod 600 " + getPasswordFilename());
            } catch (IOException e) {
                // Failed writing password file, ignore the error
            }
        }
    }

    private static String getPasswordFilename() {
        return System.getProperty("user.home") + "/.gnemdb";
    }

}
