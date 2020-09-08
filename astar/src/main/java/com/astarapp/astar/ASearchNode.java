package com.astarapp.astar;

/**
 * Implements trivial functions for a search node.
 */
public abstract class ASearchNode implements ISearchNode {
    private Double g = 0.0;
    // total estimated cost of the node
    public double cost() {
        return this.g() + this.heuristicCost();
    }
    //"tentative" g, cost from the start node 
    public double g() {
        return this.g;
    }
    //set "tentative" g
    public void setG(double g) {
        this.g = g;
    } 
    
}

