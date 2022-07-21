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
package llnl.gnem.apps.detection.core.cluster.dendrogram;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractLinker implements HierarchicalLinker {

    protected ArrayList<Leaf> leaves;
    protected HashMap<Object, Leaf> map;

    ArrayList<Node> getRoots(float threshold) {

        ArrayList<Node> roots = new ArrayList<>();

        for (Leaf leaf : leaves) {
            Node root = leaf.root(threshold);
            if (!roots.contains(root)) {
                roots.add(root);
            }
        }

        return roots;
    }

    @Override
    public ArrayList<Object[]> getClusters(float threshold) {

        // find roots
        ArrayList<Node> roots = getRoots(threshold);

        // create clusters
        ArrayList<Object[]> clusters = new ArrayList<>();

        for (Node root : roots) {
            ArrayList<Leaf> myLeaves = root.leaves();
            Object[] O = new Object[myLeaves.size()];
            for (int j = 0; j < myLeaves.size(); j++) {
                O[j] = myLeaves.get(j).O;
            }
            clusters.add(O);
        }

        return clusters;
    }

    @Override
    public float getDelay(Object O) {
        return map.get(O).delay;
    }

    @Override
    public void print(PrintStream ps, float threshold) {

        // find roots
        ArrayList<Node> roots = getRoots(threshold);

        // print each as a dendrogram (sort of)
        ps.println("\n---------------------------------------------------------------------------------------------------------\n");
        for (int i = 0; i < roots.size(); i++) {
            roots.get(i).print(ps, i + "    ");
            ps.println("\n---------------------------------------------------------------------------------------------------------\n");
        }

    }

}
