package com.example.educash;

public class Preset {
    private int presetId;
    private String presetName;

    private Double price;

    public Preset(int presetId, String presetName, Double price) {
        this.presetId = presetId;
        this.presetName = presetName;
        this.price = price;
    }

    public int getPresetId() {
        return presetId;
    }

    public String getPresetName() {
        return presetName;
    }

    public Double getPrice() { return price; }
}

