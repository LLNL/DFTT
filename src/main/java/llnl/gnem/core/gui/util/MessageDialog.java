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
package llnl.gnem.core.gui.util;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 * COPYRIGHT NOTICE GnemUtils Version 1.0 Copyright (C) 2005 Lawrence Livermore
 * National Laboratory. User: dodge1 Date: Mar 17, 2006
 */
public class MessageDialog extends JFrame {

    public static final String DEFAULT_USER_TO_ASSIGN_BUGS_TO = "dodge1";
    protected String title = "WARNING MESSAGE";
    protected int Type = JOptionPane.WARNING_MESSAGE; // 2

    public static enum PostDisplayAction {

        NOTHING, WRITE_TO_STDERR, LOG_MESSAGE
    }
    private static PostDisplayAction postDisplayAction = PostDisplayAction.WRITE_TO_STDERR;
    /**
     * Creates a new instance of ExceptionDialog
     */
    protected boolean OverrideTitle = true;

    public static void setPostDisplayAction(PostDisplayAction action) {
        postDisplayAction = action;
    }

    public void SetTitle(String t) {
        this.title = t;      // must adjust the frame size if this gets too long..
        OverrideTitle = false;
    }

    public void SetMessageType(int msgType) {
        this.Type = msgType;
        if (OverrideTitle) {
            switch (Type) {
                case JOptionPane.ERROR_MESSAGE:
                    title = "ERROR MESSAGE";
                    break;
                case JOptionPane.PLAIN_MESSAGE:
                    title = "MESSAGE";
                    break;
                case JOptionPane.INFORMATION_MESSAGE:
                    title = "INFORMATIONAL MESSAGE";
                    break;
                case JOptionPane.WARNING_MESSAGE:
                    title = "WARNING MESSAGE";
                    break;
                default:
                    title = "WARNING MESSAGE";
            }
        }
    }

    public void DisplayMessage(String s) {
        String[] msgs = new String[1];
        msgs[0] = s;
        String[] msg = parse(msgs);
        JOptionPane.setRootFrame(this);
        JOptionPane.getRootFrame().toFront();

        JOptionPane.showMessageDialog(this, msg, this.title, this.Type);

    }

    public void DisplayMessage(String[] msgs) {
        String[] msg = parse(msgs);
        this.toFront();
        JOptionPane.getRootFrame().toFront();
        JOptionPane.showMessageDialog(this, msg, this.title, this.Type);

    }

    public void displayException(Exception e) {
        this.toFront();
        if (!OverrideTitle) {
            SetTitle("Exception"); // set this unless user explicitly set it earlier.
        }
        String[] msgs = new String[1];
        msgs[0] = e.toString();
        if (msgs != null) {
            String msg[] = parse(msgs);
            boolean allowSubmission = isEnableJiraSubmission();

            String[] options = getOptionStrings(allowSubmission);
            int option = JOptionPane.showOptionDialog(this,
                    msg, title,
                    JOptionPane.YES_NO_OPTION,
                    Type,
                    null,
                    options,
                    options[0]);

        }
        switch (postDisplayAction) {
            case NOTHING:
                return;
            case WRITE_TO_STDERR:
                e.printStackTrace();
                return;
            case LOG_MESSAGE:
                llnl.gnem.core.util.ApplicationLogger.getInstance().log(Level.WARNING, "Exception Caught!", e);
        }

    }

    public static boolean isEnableJiraSubmission() {
        String answer = System.getProperty("EnableJiraSubmission");
        boolean allowSubmission = answer != null && answer.toLowerCase().equals("true");
        return allowSubmission;
    }

    private String[] getOptionStrings(boolean allowSubmission) {
        if (allowSubmission) {
            return new String[]{"OK", "Submit JIRA"};
        } else {
            return new String[]{"OK"};
        }
    }


    public static String[] parse(String[] input) {
        int size = input.length;
        String temp;
        String token;
        List<String> result = new ArrayList<String>();
        StringTokenizer st;
        for (int i = 0; i < size; i++) {
            st = new StringTokenizer(input[i]);
            temp = "";
            while (st.hasMoreTokens()) {
                token = st.nextToken();
                if ((temp.length()) + (token.length()) > 80) {
                    result.add(temp);
                    temp = token + " "; // keep processing this string...
                } else {
                    temp = temp.concat(token);
                    temp = temp.concat(" ");
                }
            }
            if (temp.length() > 0) {
                result.add(temp);
            }
        }
        return result.toArray(new String[0]);
    }
}
