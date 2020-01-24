package llnl.gnem.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dodge1 Date: Dec 3, 2009 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class InternetAccessChecker {

    public boolean canAccessInternet() {
        URL networkUrl;

        try {
            networkUrl = new URL("http://www.google.com");
        } catch (MalformedURLException ex) {
            return false;
        }

        BufferedReader in = null;
        try {
            in = new BufferedReader(
                    new InputStreamReader(
                            networkUrl.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.indexOf("Google") > 0) {
                    return true;
                }
            }
        } catch (IOException e) {
            return false;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                }
            }
        }
        return false;
    }
}
