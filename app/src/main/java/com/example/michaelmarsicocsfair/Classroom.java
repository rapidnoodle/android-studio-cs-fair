package com.example.michaelmarsicocsfair;

public class Classroom {
    private String name;
    private int floor;

    public Classroom(String name, int floor) {
        this.name = name;
        this.floor = floor;
    }

    public String getName() {
        return this.name;
    }

    public int getFloor() {
        return this.floor;
    }
}
