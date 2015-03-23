/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.plugin.cliquecenter.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.gephi.graph.api.Node;

/**
 *
 * @author lakhyos
 */
public class Clique {
    
    public HashMap<Integer,Double> members;
    
    public Clique(HashMap<Integer,Double> members){
        this.members = members;
    }
    
    Set<Integer> getNodesSet(){
        return members.keySet();
    }
    
    public int central(){
        double max = 0.;
        int nMax = 0;
        for(Integer n: members.keySet()) {
            double v = members.get(n);
            if(max < v) {
                nMax = n;
                max = v;
            }
        }
        return nMax;
    }
    
    @Override
    public String toString(){
        
        StringBuffer result = new StringBuffer();
        for(Integer k: this.members.keySet()) {
            result.append("Node: " + k + " -> " + this.members.get(k) + "\n");
        }
        return result.toString();
    }
    
    public  Set<Integer> getMembers(){
        return members.keySet();
    }
}
