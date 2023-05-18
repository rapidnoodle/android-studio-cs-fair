package com.example.michaelmarsicocsfair.pathfinding;

public class Edge {

    private final static int maxEdgeLength = 11111; //always have to be bigger than the length of the longest possible path
    private int fromNodeIndex;
    private int toNodeIndex;
    private int length;
    private int placeID;
    private String name;

    public Edge(int placeID, int fromNodeIndex, int toNodeIndex, int length, String name) {
        this.fromNodeIndex = fromNodeIndex;
        this.toNodeIndex = toNodeIndex;
        this.length = length;
        this.placeID = placeID;
        this.name = name;
    }

    public int getFromNodeIndex() {
        return fromNodeIndex;
    }

    public int getToNodeIndex() {
        return toNodeIndex;
    }

    public int getLength() {
        return length;
    }

    public int getEdgeId() {
        return placeID;
    }

    public String getEdgeName() {
        return name;
    }

    public void setLength() {
        length = maxEdgeLength;
    } //

    public int getNeighbourIndex(int nodeIndex) {
        if (this.fromNodeIndex == nodeIndex) {
            return this.toNodeIndex;
        } else {
            return this.fromNodeIndex;
        }
    }
}