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

import javax.swing.*;
import java.util.Vector;

/**
 * Utility method to create a JLabeled set of JTextfields
 *
 * User: matzel
 * Date: Jun 8, 2005
 * Time: 2:40:18 PM
 */
public class LabeledTextField
{
    /**
     * This creates a single label with a single textfield
     * @param labelstring
     */
    public LabeledTextField(String labelstring)
    {
        this.label = new JLabel(labelstring);
        this.textfieldvector = new Vector<JTextField>();
        textfieldvector.addElement(new JTextField(columns));   
    }

    /**
     * This combines a single label with a series of textfield objects
     * @param labelstring the text of the JLabel for this object
     * @param nfields  the number of fields to be created
     */
    public LabeledTextField(String labelstring, int nfields)
    {
        this.label = new JLabel(labelstring);
        this.textfieldvector = new Vector<JTextField>();

        for (int ii = 0; ii < nfields; ii++)
        {
            textfieldvector.add(new JTextField(columns));
        }
    }

    /**
     * Creates a LabeledTextField with a number of textfield objects defined by the number of values input
     * e.g. LabeledTextField("Example", {"A", "B", "C"}) would create a label and 3 textfields filled with values A, B and C
     * @param labelstring the text of the JLabel for this object
     * @param values    an array of Strings representing initial values for each TextField
     */
    public LabeledTextField(String labelstring, String [] values)
    {
        this.label = new JLabel(labelstring);
        this.textfieldvector = new Vector<JTextField>();

        for (int ii = 0; ii < values.length; ii++)
        {
            textfieldvector.add(new JTextField(values[ii], columns));
        }
    }

    public JLabel getLabel()
    {
        return label;
    }

    /*public int getSize()
    {
        return textfieldvector.size();
    }*/

    public JTextField getTextField(int index)
    {
        return textfieldvector.elementAt(index);
    }

    public Vector <JTextField> getTextFieldVector()
    {
        return textfieldvector;
    }

    public void setColumns(int n)
    {
        this.columns = n;
    }

    private JLabel label;
    private Vector<JTextField> textfieldvector;

    private int columns = 5;
}
