package com.astarapp.ui.model.elements;

import com.astarapp.astar.ISearchNode;
import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;

public class FieldCell extends Pane implements ISearchNode {

    @Getter
    private final int x;
    @Getter
    private final int y;
    private final BiFunction<Integer, Integer, ArrayList<ISearchNode>> neighbourProvider;
    private final Supplier<FieldCell> goalProvider;
    private final Supplier<CellBehavior> cellBehaviorProvider;
    @Setter
    private double g;
    @Getter
    @Setter
    private ISearchNode cameFrom;
    private boolean isPath;

    SimpleBooleanProperty isObject;
    SimpleBooleanProperty isWall;
    SimpleBooleanProperty isGoal;

    public FieldCell(int x, int y,
                     BiFunction<Integer, Integer, ArrayList<ISearchNode>> neighbourProvider,
                     Supplier<FieldCell> goalProvider,
                     Supplier<CellBehavior> cellBehaviorProvider) {
        this.x = x;
        this.y = y;
        this.neighbourProvider = neighbourProvider;
        this.goalProvider = goalProvider;
        this.cellBehaviorProvider = cellBehaviorProvider;
        getStyleClass().addAll("cell", "empty");

        isObject = new SimpleBooleanProperty();
        isObject.addListener((observable, oldValue, newValue) -> onIsObjectPropertyChanged(newValue));
        isWall = new SimpleBooleanProperty();
        isWall.addListener((observable, oldValue, newValue) -> onIsWallPropertyChanged(newValue));
        isGoal = new SimpleBooleanProperty();
        isGoal.addListener((observable, oldValue, newValue) -> onIsGoalPropertyChanged(newValue));

        setOnMousePressed(this::onDragDetected);
        setOnMousePressed(event -> event.setDragDetect(true));
        setOnMouseDragged(event -> event.setDragDetect(false));
        setOnDragDetected(event -> startFullDrag());
        setOnMouseDragEntered(this::onDragDetected);
    }

    public boolean isWall() {
        return isWall.get();
    }

    public void showPath() {
        if (!isObject.get() && !isGoal.get()) {
            isPath = true;
            switchState("path", "empty");
        }
    }

    public void reset(boolean full) {
        setG(0);
        setCameFrom(null);
        setStyle("");
        if (isPath) {
            switchState("empty", "path");
        }
        if (full) {
            isObject.set(false);
            isWall.set(false);
            isGoal.set(false);
        }
    }

    private void onDragDetected(MouseEvent mouseEvent) {
        cellBehaviorProvider.get().onAction(this);
    }

    private void onIsObjectPropertyChanged(Boolean newValue) {
        if (newValue != null) {
            if (newValue) {
                isWall.set(false);
                switchState("object", "empty", "wall");
            } else {
                switchState("empty", "object");
            }
        }
    }

    private void onIsWallPropertyChanged(Boolean newValue) {
        if (newValue != null) {
            if (newValue) {
                switchState("wall", "empty", "path");
            } else {
                switchState("empty", "wall");
            }
        }
    }

    private void onIsGoalPropertyChanged(Boolean newValue) {
        if (newValue != null) {
            if (newValue) {
                isWall.set(false);
                switchState("goal", "empty", "wall");
            } else {
                switchState("empty", "goal");
            }
        }
    }

    private void switchState(String to, String... from) {
        getStyleClass().removeAll(from);
        getStyleClass().add(to);
    }

    @Override
    public double cost() {
        return this.g() + this.heuristicCost();
    }

    @Override
    public double g() {
        return g;
    }

    @Override
    public double heuristicCost() {
        return costTo(goalProvider.get());
    }

    @Override
    public double costTo(ISearchNode successor) {
        FieldCell cell = (FieldCell) successor;
        return (Math.abs(cell.x - this.x) + Math.abs(cell.y - this.y)) / 2d;
    }

    @Override
    public ArrayList<ISearchNode> getSuccessors() {
        return neighbourProvider.apply(x, y);
    }

    @Override
    public Integer keyCode() {
        return hashCode();
    }
}