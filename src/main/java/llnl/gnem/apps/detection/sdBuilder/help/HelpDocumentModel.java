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
package llnl.gnem.apps.detection.sdBuilder.help;

import javax.swing.JEditorPane;

public class HelpDocumentModel {

    private JEditorPane editorPane;

    private HelpDocumentModel() {
    }

    public void initialize() throws Exception {
        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        ClassLoader classLoader = getClass().getClassLoader();
        java.net.URL helpURL = classLoader.getResource(
                "BuilderHelp.htm");
        if (helpURL != null) {

            editorPane.setPage(helpURL);

        } else {
            throw new IllegalStateException("Could not find resource BuilderHelp.htm!");
        }

    }

    public JEditorPane getEditorPane() {
        return editorPane;
    }

    public static HelpDocumentModel getInstance() {
        return HelpDocumentModelHolder.INSTANCE;
    }

    private static class HelpDocumentModelHolder {

        private static final HelpDocumentModel INSTANCE = new HelpDocumentModel();
    }
}
