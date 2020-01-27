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

import java.awt.*;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 *  FlowLayout subclass that fully supports wrapping of components.
 */
public class WrapLayout extends FlowLayout
{
	private Dimension preferredLayoutSize;

	/**
	* Constructs a new WrapLayout with a left
	* alignment and a default 5-unit horizontal and vertical gap.
	*/
	public WrapLayout()
	{
		super();
	}

	/**
	* Constructs a new <code>FlowLayout</code> with the specified
	* alignment and a default 5-unit horizontal and vertical gap.
	* The value of the alignment argument must be one of
	* WrapLayout
	* @param align the alignment value
	*/
	public WrapLayout(int align)
	{
		super(align);
	}

	/**
	* Creates a new flow layout manager with the indicated alignment
	* and the indicated horizontal and vertical gaps.
	* <p>
	* The value of the alignment argument must be one of
	*/
	public WrapLayout(int align, int hgap, int vgap)
	{
		super(align, hgap, vgap);
	}

	/**
	* Returns the preferred dimensions for this layout given the
	*/
	@Override
	public Dimension preferredLayoutSize(Container target)
	{
		return layoutSize(target, true);
	}

	/**
	* Returns the minimum dimensions needed to layout the <i>visible</i>
	* components contained in the specified target container.
	*/
	@Override
	public Dimension minimumLayoutSize(Container target)
	{
		Dimension minimum = layoutSize(target, false);
		minimum.width -= (getHgap() + 1);
		return minimum;
	}

	/**
	* Returns the minimum or preferred dimension needed to layout the target
	* container.
	*
	*/
	private Dimension layoutSize(Container target, boolean preferred)
	{
	synchronized (target.getTreeLock())
	{
		//  Each row must fit with the width allocated to the containter.
		//  When the container width = 0, the preferred width of the container
		//  has not yet been calculated so lets ask for the maximum.

		int targetWidth = target.getSize().width;

		if (targetWidth == 0)
			targetWidth = Integer.MAX_VALUE;

		int hgap = getHgap();
		int vgap = getVgap();
		Insets insets = target.getInsets();
		int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
		int maxWidth = targetWidth - horizontalInsetsAndGap;

		//  Fit components into the allowed width

		Dimension dim = new Dimension(0, 0);
		int rowWidth = 0;
		int rowHeight = 0;

		int nmembers = target.getComponentCount();

		for (int i = 0; i < nmembers; i++)
		{
			Component m = target.getComponent(i);

			if (m.isVisible())
			{
				Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

				//  Can't add the component to current row. Start a new row.

				if (rowWidth + d.width > maxWidth)
				{
					addRow(dim, rowWidth, rowHeight);
					rowWidth = 0;
					rowHeight = 0;
				}

				//  Add a horizontal gap for all components after the first

				if (rowWidth != 0)
				{
					rowWidth += hgap;
				}

				rowWidth += d.width;
				rowHeight = Math.max(rowHeight, d.height);
			}
		}

		addRow(dim, rowWidth, rowHeight);

		dim.width += horizontalInsetsAndGap;
		dim.height += insets.top + insets.bottom + vgap * 2;

		//	When using a scroll pane or the DecoratedLookAndFeel we need to
		//  make sure the preferred size is less than the size of the
		//  target containter so shrinking the container size works
		//  correctly. Removing the horizontal gap is an easy way to do this.

		Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);

		if (scrollPane != null)
		{
			dim.width -= (hgap + 1);
		}

		return dim;
	}
	}

	/*
	 *  A new row has been completed. Use the dimensions of this row
	 *  to update the preferred size for the container.
	 *
	 *  @param dim update the width and height when appropriate
	 *  @param rowWidth the width of the row to add
	 *  @param rowHeight the height of the row to add
	 */
	private void addRow(Dimension dim, int rowWidth, int rowHeight)
	{
		dim.width = Math.max(dim.width, rowWidth);

		if (dim.height > 0)
		{
			dim.height += getVgap();
		}

		dim.height += rowHeight;
	}
}
