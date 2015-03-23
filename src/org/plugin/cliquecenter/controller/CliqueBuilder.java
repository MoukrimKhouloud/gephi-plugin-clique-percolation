/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.plugin.cliquecenter.controller;

import org.plugin.cliquecenter.algorithms.EigenvectorCentrality;
import org.plugin.cliquecenter.algorithms.DegreeCentrality;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Node;
import org.openide.util.Lookup;

/**
 *
 * @author lakhyos
 */
public class CliqueBuilder {

    private double[] centralities;
    HashMap<Integer, Node> Nodes = new HashMap<Integer, Node>();
    HashMap<Node, Integer> invNodes = new HashMap<Node, Integer>();
    Graph graph;

    public CliqueBuilder(double[] C, Graph graph) {
        this.centralities = C;
        this.graph = graph;
        this.initiateNodes();
    }

    private TreeMap<Integer, Double> sort() {
        HashMap inter = new HashMap<Integer, Double>();
        ValueComparator bvc = new ValueComparator(inter);

        for (int i = 0; i < centralities.length; ++i) {
            inter.put(i, centralities[i]);
        }

        TreeMap<Integer, Double> sorted_map = new TreeMap<Integer, Double>(bvc);
        sorted_map.putAll(inter);

        return sorted_map;
    }

    public void initiateNodes() {
        int count = 0;
        for (Node u : graph.getNodes()) {
            //Association ( indice ----- noeud )
            Nodes.put(count, u);
            //Association ( noeud ----- indice )
            invNodes.put(u, count);
            count++;
        }

    }

    public List<Clique> statiqueCliques() {
        List<Clique> Cliques = new ArrayList();
        TreeMap<Integer, Double> Elements = this.sort();

        System.out.println("Elements :" + Elements.toString());

        while (!Elements.isEmpty()) {
            HashMap<Integer, Double> members = new HashMap<Integer, Double>();

            System.out.println("Value :" + Elements.firstKey());
            System.out.println("El :" + Elements.toString());

            members.put(Elements.firstKey(), Elements.get(Elements.firstKey()));

            Node n = graph.getNode(Elements.firstKey());
            Elements.remove(Elements.firstKey());

            for (Node neighbor : graph.getNeighbors(n)) {
                if (Elements.containsKey(invNodes.get(neighbor))) {
                    members.put(invNodes.get(neighbor), Elements.get(invNodes.get(neighbor)));
                    Elements.remove(invNodes.get(neighbor));
                }
            }

            Clique c = new Clique(members);
            Cliques.add(c);
        }

        return Cliques;
    }

    public Graph subGraph(Graph graph, Clique clique) {
        graph.writeLock();

        for (Integer id : clique.getNodesSet()) {
            Node n = Nodes.get(id);
            if(graph.contains(n)) {
                graph.removeNode(n);
            }
        }
        graph.writeUnlock();

        return graph;
    }

    public Clique getClique() {
        Clique clique = null;
        TreeMap<Integer, Double> Elements = this.sort();

        while (!Elements.isEmpty()) {
            HashMap<Integer, Double> members = new HashMap<Integer, Double>();

            System.out.println("Value :" + Elements.firstKey());
            System.out.println("El :" + Elements.toString());

            members.put(Elements.firstKey(), Elements.get(Elements.firstKey()));

            Node n = Nodes.get(Elements.firstKey());
            Elements.remove(Elements.firstKey());
            //if( n == null)
             //   break;
            for (Node neighbor : graph.getNeighbors(n)) {
                if (Elements.containsKey(invNodes.get(neighbor))) {
                    members.put(invNodes.get(neighbor), Elements.get(invNodes.get(neighbor)));
                    Elements.remove(invNodes.get(neighbor));
                }
            }

            Clique c = new Clique(members);
            clique = c;
            break;
        }

        return clique;
    }

    public static List<Clique> dynamiqueCliques(int type) {
        GraphController gc = Lookup.getDefault().lookup(GraphController.class);
        GraphModel model = gc.getModel();
        GraphView view = model.newView();
        Graph graph = model.getGraph(view);
        
        double centralities[] = null;
        
        List<Clique> Cliques = new ArrayList();
        CliqueBuilder cb = null;

        while (graph.getNodeCount() != 0) {
            switch (type) {
                case 0:
                    EigenvectorCentrality EV = new EigenvectorCentrality();
                    EV.execute(graph);
                    centralities = EV.getCentralities();
                    break;
                case 1:
                    DegreeCentrality iDC = new DegreeCentrality(true);
                    iDC.execute(graph);
                    centralities = iDC.getCentralities();
                    break;
                case 2:
                    DegreeCentrality oDC = new DegreeCentrality(false);
                    oDC.execute(graph);
                    centralities = oDC.getCentralities();
                    break;
            }
            cb = new CliqueBuilder(centralities, graph);
            Clique c = cb.getClique();
            if(c == null )
                break;
            Cliques.add(c);
            graph = cb.subGraph(graph, c);
            System.out.println(graph.getNodeCount());
        }

        return Cliques;
    }

}
