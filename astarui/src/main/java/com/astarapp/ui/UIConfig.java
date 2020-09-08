package com.astarapp.ui;

public interface UIConfig {
    String WINDOW_TITLE = "A*";

    int FIELD_SIZE_IN_PIXELS = 850;
    int FIELD_EDGE_LENGTH_IN_CELLS = 50;
    int CELL_SIZE = FIELD_SIZE_IN_PIXELS / FIELD_EDGE_LENGTH_IN_CELLS;
}
