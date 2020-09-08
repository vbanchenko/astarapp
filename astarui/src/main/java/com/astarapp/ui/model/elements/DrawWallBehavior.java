package com.astarapp.ui.model.elements;

public class DrawWallBehavior implements CellBehavior {
    @Override
    public void onAction(FieldCell cell) {
        if (cell.isObject.get() || cell.isGoal.get()) {
            return;
        }
        cell.isWall.set(!cell.isWall.get());
    }
}
