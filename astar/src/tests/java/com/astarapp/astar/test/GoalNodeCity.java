package com.astarapp.astar.test;
import com.astarapp.astar.IGoalNode;
import com.astarapp.astar.ISearchNode;

/**
 * Test case from wikipedia
 * http://de.wikipedia.org/wiki/A*-Algorithmus
 */

public class GoalNodeCity implements IGoalNode {
    private String name;
    public GoalNodeCity(String name) {
        this.name = name;
    }
    public boolean inGoal(ISearchNode other) {
         if(other instanceof SearchNodeCity) {
            SearchNodeCity otherNode = (SearchNodeCity) other;
            return (this.name == otherNode.getName());
        }
        return false;
    }
}
