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
package llnl.gnem.dftt.core.gui.util;

import java.util.List;
import javax.swing.JComponent;
import javax.swing.JSplitPane;

/**
 *
 * @author addair1
 */
public class MultiSplitPane {
    public static JComponent createMultiSplitPane(List<JComponent> components, int height) {
        if (components.size() == 1) {
            return components.get(0);
        }

        int split = components.size() / 2;
        List<JComponent> list1 = components.subList(0, split);
        List<JComponent> list2 = components.subList(split, components.size());

        int splitHeight = height * list1.size() / components.size();
        JSplitPane newPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                createMultiSplitPane(list1, splitHeight),
                createMultiSplitPane(list2, height - splitHeight));
        newPane.setOneTouchExpandable(true);
        newPane.setDividerLocation(splitHeight);
        newPane.setResizeWeight(0.5);
        return newPane;
    }
}
