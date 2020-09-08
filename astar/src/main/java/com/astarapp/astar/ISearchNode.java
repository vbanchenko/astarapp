package com.astarapp.astar;
import java.util.*;

/**
 * Interface of a search node.
 */
public interface ISearchNode {
    // total estimated cost of the node
    public double cost();
    //"tentative" g, cost from the start node 
    public double g();
    //set "tentative" g
    public void setG(double g);
    //heuristic cost to the goal node
    public double heuristicCost();
    //costs to a successor
    public double costTo(ISearchNode successor);
    // a node possesses or computes his successors
    public ArrayList<ISearchNode> getSuccessors();
    // get parent of node in a path
    public ISearchNode getCameFrom();
    //set parent
    public void setCameFrom(ISearchNode parent);
    //unique hash for a node to be used in a hash map
    //makes algorithm significantly faster
    //return null if you do not want this feature
    public Integer keyCode();
    
    public boolean equals(Object other);

    public int hashCode();
}

