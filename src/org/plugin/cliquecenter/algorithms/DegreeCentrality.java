/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.plugin.cliquecenter.algorithms;

import java.util.HashMap;

import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;

/**
 *
 * @author lakhyos
 */
public class DegreeCentrality {

    private double[] centralities;
    private boolean degree; //true pour in, false pour out 

    public DegreeCentrality(boolean degree) {
        this.degree = degree;
    }

    public void execute(Graph hgraph) {

        int N = hgraph.getNodeCount();
        hgraph.readLock();

        centralities = new double[N];

        HashMap<Integer, Node> indicies = new HashMap<Integer, Node>();
        HashMap<Node, Integer> invIndicies = new HashMap<Node, Integer>();

        //Initialisations des deux tables
        fillIndiciesMaps(hgraph,indicies, invIndicies);

        //Debut des cicles de calcul des centralitees
        start(hgraph, indicies, invIndicies);

        hgraph.readUnlock();
    }

    //Remplissage des tables indices
    public void fillIndiciesMaps(Graph hgraph, HashMap<Integer, Node> indicies,
            HashMap<Node, Integer> invIndicies) {

        if (indicies == null || invIndicies == null) {
            return;
        }

        //Initialisation des nodes et inversement
        int count = 0;
        for (Node u : hgraph.getNodes()) {
            //Association ( indice ----- noeud )
            indicies.put(count, u);
            //Association ( noeud ----- indice )
            invIndicies.put(u, count);
            //Initialisation des centralitees par 1 
            centralities[count] = 0;
            count++;
        }
    }

    public void start(Graph hgraph, HashMap<Integer, Node> indicies,
            HashMap<Node, Integer> invIndicies) {
        int N = hgraph.getNodeCount();

        for (int i = 0; i < N; i++) {
            //Creation d'un iterateur sur les noeuds
            EdgeIterable iter;
            //pour chaque noeud
            Node u = indicies.get(i);
            //extraire les arcs
            if (degree == true) {
                iter = ((DirectedGraph) hgraph).getInEdges(u);
            } else {
                iter = ((DirectedGraph) hgraph).getOutEdges(u);
            }
            //Pour chaque arcs
            for (Edge e : iter) {
                //on incremente la centralite du noeud courant
                centralities[i]++;
            }
        }
    }

    public double[] getCentralities() {
        //finale table of Centralities values
        return centralities;
    }

    public int getDegreeCentrality() {
        int centrality = 0;

        for (int i = 0; i < centralities.length; ++i) {
            if (centralities[centrality] < centralities[i]) {
                centrality = i;
            }
        }

        return centrality;
    }
}
