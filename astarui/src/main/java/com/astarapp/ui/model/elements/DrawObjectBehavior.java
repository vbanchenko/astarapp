package com.astarapp.ui.model.elements;

import com.astarapp.astar.ISearchNode;
import com.astarapp.ui.controllers.MainViewController;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class DrawObjectBehavior implements CellBehavior {

    private final MainViewController controller;

    @Getter
    private FieldCell object;
    private List<FieldCell> area;

    public DrawObjectBehavior(MainViewController controller) {
        this.controller = controller;
        area = new ArrayList<>();
    }

    @Override
    public void onAction(FieldCell cell) {
        if (cell.isGoal.get()) {
            return;
        }
        ArrayList<ISearchNode> area = controller.getArea(cell.getX(), cell.getY());
        int objectSize = controller.getObjectSize();
        if (area.size() == objectSize * objectSize) {
            for (FieldCell obj : this.area) {
                obj.isObject.set(false);
            }
            this.area.clear();
            for (ISearchNode obj : area) {
                FieldCell fieldCell = (FieldCell) obj;
                fieldCell.isObject.set(true);
                this.area.add(fieldCell);
            }
            object = cell;
        }
    }
}
