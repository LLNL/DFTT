// Copyright (c) 2014 Deschutes Signal Processing LLC
// Author:  Dave Harris

package llnl.gnem.apps.detection.cancellation.dendrogram;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractLinker implements HierarchicalLinker {

    protected ArrayList< Leaf> leaves;
    protected HashMap< Object, Leaf> map;

    ArrayList< Node> getRoots(float threshold) {

        ArrayList< Node> roots = new ArrayList<>();

        for (Leaf leaf : leaves) {
            Node root = leaf.root(threshold);
            if (!roots.contains(root)) {
                roots.add(root);
            }
        }

        return roots;
    }

    @Override
    public ArrayList< Object[]> getClusters(float threshold) {

    // find roots
        ArrayList< Node> roots = getRoots(threshold);

    // create clusters
        ArrayList< Object[]> clusters = new ArrayList<>();

        for (Node root : roots) {
            ArrayList< Leaf> myLeaves = root.leaves();
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
        return ((Leaf) map.get(O)).delay;
    }

    @Override
    public void print(PrintStream ps, float threshold) {

    // find roots
        ArrayList< Node> roots = getRoots(threshold);

    // print each as a dendrogram (sort of)
        ps.println("\n---------------------------------------------------------------------------------------------------------\n");
        for (int i = 0; i < roots.size(); i++) {
            roots.get(i).print(ps, i + "    ");
            ps.println("\n---------------------------------------------------------------------------------------------------------\n");
        }

    }

}
