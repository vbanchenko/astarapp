package com.astarapp.astar.test;
import com.astarapp.astar.AStar;
import com.astarapp.astar.ISearchNode;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.*; 

public class AStarTest {
    @Test
    public void SearchNodeTest2D() {
        GoalNode2D goalNode = new GoalNode2D(3, 3);
        SearchNode2D initialNode = new SearchNode2D(1, 1, null, goalNode);
        ArrayList<ISearchNode> path = new AStar().shortestPath(initialNode, goalNode);
        assertEquals(path.size(), 5);
    }
    @Test
    public void SearchNodeCityTest() {
        ISearchNode initialNode = new SearchNodeCity("Saarbrücken");
        ArrayList<ISearchNode> path = new AStar().shortestPath(initialNode, new GoalNodeCity("Würzburg"));
        double e = 0.00001;
        assertEquals(path.get(0).cost(), 222.0, e);
        assertEquals(path.get(1).cost(), 228, e);
        assertEquals(path.get(2).cost(), 269, e);
        assertEquals(path.get(3).cost(), 289, e);
        assertEquals(path.toString(), "[Saarbrücken,cost:222.0, Kaiserslautern,cost:228.0, Frankfurt,cost:269.0, Würzburg,cost:289.0]");
    }
}

