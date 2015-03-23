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
public class EigenvectorCentrality {

    private final int Cicles = 100;
    private double[] centralities;
    

    public EigenvectorCentrality() {
    }

    public void execute(Graph hgraph){

        int N = hgraph.getNodeCount();
        hgraph.readLock();

        centralities = new double[N];

        HashMap<Integer, Node> indicies = new HashMap<Integer, Node>();
        HashMap<Node, Integer> invIndicies = new HashMap<Node, Integer>();
        
        //Initialisations des deux tables
        fillIndiciesMaps(hgraph, centralities, indicies, invIndicies);

        //Debut des cicles de calcul des centralitees
        Start(hgraph, centralities, indicies, invIndicies, Cicles);

        hgraph.readUnlock();
    }

    //Remplissage des tables indices
    public void fillIndiciesMaps(Graph hgraph, double[] eigenCentralities, HashMap<Integer, Node> indicies, 
            HashMap<Node, Integer> invIndicies) {
        
        if (indicies == null || invIndicies == null) {
            return;
        }

        int count = 0;
        for (Node u : hgraph.getNodes()) {
            //Association ( indice ----- noeud )
            indicies.put(count, u);
            //Association ( noeud ----- indice )
            invIndicies.put(u, count);
            //Initialisation des centralitees par 1 
            eigenCentralities[count] = 1;
            count++;
        }
    }

    private double processTempCentralitiesAndMaxCentralite(Graph hgraph, HashMap<Integer, Node> indicies, 
            HashMap<Node, Integer> invIndicies,double[] tempValues, double[] centralityValues) {
        
        double max = 0.;
        int N = hgraph.getNodeCount();

        for (int i = 0; i < N; i++) {
            //pour chaque noeud
            Node u = indicies.get(i);
            //extraire les arcs entrants
            EdgeIterable iter = ((DirectedGraph) hgraph).getInEdges(u);
         
            //Pour chaque arcs
            for (Edge e : iter) {              
                //on prend le noeud extreme
                Node v = hgraph.getOpposite(u, e);
                //on le localise dans la table des centralite (son rang)
                Integer id = invIndicies.get(v);
                //on ajoute sa centralitee aux centralitees temporaires du noeud courant
                tempValues[i] += centralityValues[id];
            }
            //la centralite maximale est retenue
            max = Math.max(max, tempValues[i]);
        }
        //retourner la valeure de la plus grande centralite
        return max;
    }

    //Mettre a jour le tableau des centralitees
    private void updateValues(Graph hgraph, double[] tempValues, double[] eigenCentralities, double max) {
        int N = hgraph.getNodeCount();

        for (int k = 0; k < N; k++) {
            if (max != 0)
                //mettre a jour la valeur de centralite
                eigenCentralities[k] = tempValues[k] / max;
           
        }
    }

    //Calcul des centralitees en faisant le calcul N fois (plus le N est grand plus les centralitees sont precises)
    public void Start(Graph hgraph, double[] eigenCentralities,
            HashMap<Integer, Node> indicies, HashMap<Node, Integer> invIndicies, int numIterations) {

        int N = hgraph.getNodeCount();
        double[] tmp = new double[N];

        for (int s = 0; s < numIterations; s++) {
            //caluler les nouvelles centralitees (temporaires)
            double max = processTempCentralitiesAndMaxCentralite(hgraph, indicies, invIndicies, tmp, eigenCentralities);
            //mettre a jours la centralitees
            updateValues(hgraph, tmp, eigenCentralities, max);
        }
    }

    public double[] getCentralities() {
        //finale table of Centralities values
        return centralities;
    }
    
    public int getEigenValuCentrality(){
        int centrality = 0;
        
        for(int i=0; i < centralities.length; ++i)
            if(centralities[centrality] < centralities[i])
                centrality = i;
            
        return centrality;
    }
}