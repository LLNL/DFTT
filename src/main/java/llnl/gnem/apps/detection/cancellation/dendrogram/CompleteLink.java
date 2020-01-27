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
package llnl.gnem.apps.detection.cancellation.dendrogram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class CompleteLink extends AbstractLinker {

    public ArrayList< NodeSimilarity> similarities;

    public CompleteLink(ArrayList< SimilarityMeasure> measurements, float threshold, ArrayList< Object> objects) {

    // make Leaves for objects and NodeSimilarities for leaves 
        leaves = new ArrayList<>();
        map = new HashMap<>(objects.size());
        for (Object O : objects) {
            Leaf L = new Leaf(null, O);
            leaves.add(L);
            map.put(O, L);
        }

        similarities = new ArrayList<>();

        for (SimilarityMeasure s : measurements) {
            NodeSimilarity ns = new NodeSimilarity((Leaf) map.get(s.getObject1()),
                    (Leaf) map.get(s.getObject2()),
                    s.getValue());
            similarities.add(ns);
        }

        Collections.sort(similarities);
        Collections.reverse(similarities);

    // construct dendrogram
        while (!similarities.isEmpty()) {

            NodeSimilarity ns = similarities.remove(0);

            if (ns.getValue() < threshold) {
                break;
            }

            Node n1 = ns.getNode1();
            Node n2 = ns.getNode2();

            Node newNode = new Node(null, n1, n2, ns.getValue());
            n1.parent = newNode;
            n2.parent = newNode;

      // replace references to n1 and n2 with reference to newNode in all NodeSimilarities
            ArrayList< NodeSimilarity> n1refs = new ArrayList<>();
            ArrayList< NodeSimilarity> n2refs = new ArrayList<>();

            for (NodeSimilarity tmp : similarities) {
                if (tmp.contains(n1)) {
                    n1refs.add(tmp);
                }
                if (tmp.contains(n2)) {
                    n2refs.add(tmp);
                }
            }

      // find minimum similarity for each node
            for (NodeSimilarity tmp1 : n1refs) {

                float newValue = tmp1.getValue();
                Node other = tmp1.otherNode(n1);

                for (NodeSimilarity tmp2 : n2refs) {
                    if (tmp2.contains(other)) {
                        newValue = Math.min(newValue, tmp2.getValue());
                        break;
                    }
                }

                similarities.add(new NodeSimilarity(newNode, other, newValue));
            }

            similarities.removeAll(n1refs);
            similarities.removeAll(n2refs);
            Collections.sort(similarities);
            Collections.reverse(similarities);
        }

    }

}
