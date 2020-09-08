package com.astarapp.ui.model.elements;

import lombok.Getter;

public class DrawGoalBehavior implements CellBehavior {
    @Getter
    private FieldCell goal;

    @Override
    public void onAction(FieldCell cell) {
        if (cell.isObject.get()) {
            return;
        }
        if (goal != null) {
            goal.isGoal.set(false);
        }
        cell.isGoal.set(true);
        goal = cell;
    }
}
