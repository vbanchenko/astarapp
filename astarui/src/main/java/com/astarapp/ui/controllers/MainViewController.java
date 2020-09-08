package com.astarapp.ui.controllers;

import com.astarapp.astar.ASearchNode;
import com.astarapp.astar.AStar;
import com.astarapp.astar.IGoalNode;
import com.astarapp.astar.ISearchNode;
import com.astarapp.ui.UIConfig;
import com.astarapp.ui.model.elements.CellBehavior;
import com.astarapp.ui.model.elements.DrawGoalBehavior;
import com.astarapp.ui.model.elements.DrawObjectBehavior;
import com.astarapp.ui.model.elements.DrawWallBehavior;
import com.astarapp.ui.model.elements.FieldCell;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import lombok.Getter;

public class MainViewController implements Initializable {
    private final DrawObjectBehavior drawObjectBehavior = new DrawObjectBehavior(this);
    private final DrawWallBehavior drawWallBehavior = new DrawWallBehavior();
    private final DrawGoalBehavior drawGoalBehavior = new DrawGoalBehavior();

    @FXML
    private AnchorPane root;
    @FXML
    private GridPane mainGrid;
    @FXML
    private GridPane fieldGrid;
    @FXML
    private Spinner<Integer> objectSizeSpinner;
    @FXML
    private ToggleButton drawObjectToggle;
    @FXML
    private ToggleButton drawWallsToggle;
    @FXML
    private ToggleButton drawGoalToggle;
    @FXML
    private Button runButton;
    @FXML
    private Button resetButton;

    @Getter
    private CellBehavior currentBehavior;
    @Getter
    private int objectSize;

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fieldGrid.setPrefSize(UIConfig.FIELD_SIZE_IN_PIXELS, UIConfig.FIELD_SIZE_IN_PIXELS);
        fieldGrid.setMinSize(UIConfig.FIELD_SIZE_IN_PIXELS, UIConfig.FIELD_SIZE_IN_PIXELS);
        fieldGrid.setMaxSize(UIConfig.FIELD_SIZE_IN_PIXELS, UIConfig.FIELD_SIZE_IN_PIXELS);

        for (int x = 0; x < UIConfig.FIELD_EDGE_LENGTH_IN_CELLS; x++) {
            fieldGrid.getColumnConstraints().add(new ColumnConstraints(UIConfig.CELL_SIZE));
            fieldGrid.getRowConstraints().add(new RowConstraints(UIConfig.CELL_SIZE));
            for (int y = 0; y < UIConfig.FIELD_EDGE_LENGTH_IN_CELLS; y++) {
                fieldGrid.add(new FieldCell(x, y, this::getNeighbours, this::getGoalCell, this::getCurrentBehavior), x, y);
            }
        }
        objectSize = 1;
        objectSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, objectSize));
        objectSizeSpinner.valueProperty().addListener((observable, oldValue, newValue) -> objectSize = newValue);

        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(drawObjectToggle, drawWallsToggle, drawGoalToggle);
        drawObjectToggle.setUserData(drawObjectBehavior);
        drawWallsToggle.setUserData(drawWallBehavior);
        drawGoalToggle.setUserData(drawGoalBehavior);
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null && oldValue != null) {
                toggleGroup.selectToggle(oldValue);
            } else if (newValue != null) {
                currentBehavior = (CellBehavior) newValue.getUserData();
                resetPath();
            }
        });
        toggleGroup.selectToggle(drawWallsToggle);

        runButton.setOnAction(event -> {
            FieldCell object = drawObjectBehavior.getObject();
            if (object == null || getGoalCell() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Both start and goal must be specified.", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            resetPath();
            AStar aStar = new AStar();
            ArrayList<ISearchNode> path = aStar.shortestPath(object, this::isGoal);
            if (path == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "No way.", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            for (ISearchNode node : path) {
                FieldCell cell = (FieldCell) node;
                for (ISearchNode areaCell : getArea(cell.getX(), cell.getY())) {
                    cell = (FieldCell) areaCell;
                    cell.showPath();
                }
            }
        });
        resetButton.setOnAction(event -> resetMap());
    }

    private boolean isGoal(ISearchNode other) {
        FieldCell cell = (FieldCell) other;
        ArrayList<ISearchNode> area = getArea(cell.getX(), cell.getY());
        return area.size() == objectSize * objectSize && area.contains(getGoalCell());
    }

    private void resetMap() {
        fieldGrid.getChildren().forEach(child -> ((FieldCell) child).reset(true));
    }

    private void resetPath() {
        fieldGrid.getChildren().forEach(child -> ((FieldCell) child).reset(false));
    }

    private FieldCell getCellByCoords(Integer x, Integer y) {
        if (isValidCoord(x) && isValidCoord(y)) {
            return (FieldCell) fieldGrid.getChildren().get(UIConfig.FIELD_EDGE_LENGTH_IN_CELLS * x + y);
        }
        return null;
    }

    private ArrayList<ISearchNode> getNeighbours(Integer x, Integer y) {
        int[] neighboursX = {1, -1, 0, 0};
        int[] neighboursY = {0, 0, 1, -1};
        return (ArrayList<ISearchNode>) getCellsByDimensions(x, y, neighboursX, neighboursY).stream()
                .filter(fieldCell -> {
                    FieldCell cell = (FieldCell) fieldCell;
                    return getArea(cell.getX(), cell.getY()).size() == objectSize * objectSize;
                })
                .collect(Collectors.toList());
    }

    public ArrayList<ISearchNode> getArea(Integer x, Integer y) {
        int startX = x - objectSize / 2 + 1 - objectSize % 2;
        int startY = y - objectSize / 2 + 1 - objectSize % 2;
        ArrayList<ISearchNode> neighbours = new ArrayList<>();
        for (int i = 0; i < objectSize; i++) {
            for (int j = 0; j < objectSize; j++) {
                FieldCell neighbour = getCellByCoords(startX + i, startY + j);
                if (neighbour != null && !neighbour.isWall()) {
                    neighbours.add(neighbour);
                }
            }
        }
        return neighbours;
    }

    private ArrayList<ISearchNode> getCellsByDimensions(Integer x, Integer y, int[] neighboursX, int[] neighboursY) {
        ArrayList<ISearchNode> neighbours = new ArrayList<>();
        for (int i = 0; i < neighboursX.length; i++) {
            FieldCell neighbour = getCellByCoords(x + neighboursX[i], y + neighboursY[i]);
            if (neighbour != null && !neighbour.isWall()) {
                neighbours.add(neighbour);
            }
        }
        return neighbours;
    }

    private boolean isValidCoord(double coord) {
        return coord >= 0 && coord < UIConfig.FIELD_EDGE_LENGTH_IN_CELLS;
    }

    private FieldCell getGoalCell() {
        return drawGoalBehavior.getGoal();
    }
}
