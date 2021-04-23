package com.nsa.CodingAid.Model;

public class SlideModel {
    String text;
    int imageResource;

    public SlideModel(String text, int imageResource) {
        this.text = text;
        this.imageResource = imageResource;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}
