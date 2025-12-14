package com.gold.witt.api;

import java.util.ArrayList;
import java.util.List;

public final class WittTooltip {

    public static final class Line {
        public final String text;
        public final int color;

        public Line(String text, int color) {
            this.text = text;
            this.color = color;
        }
    }

    private final List<Line> lines = new ArrayList<Line>();

    public void add(String text, int color) {
        if (text == null) return;
        if (text.length() == 0) return;
        lines.add(new Line(text, color));
    }

    public List<Line> lines() {
        return lines;
    }
}
