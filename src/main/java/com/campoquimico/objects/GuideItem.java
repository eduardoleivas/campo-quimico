package com.campoquimico.objects;

public class GuideItem {
    private final int id;
    private final String text;

    public GuideItem(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
