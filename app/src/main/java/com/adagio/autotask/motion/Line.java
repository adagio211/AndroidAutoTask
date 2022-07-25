package com.adagio.autotask.motion;

public class Line {

    public final Pointer startPointer;
    public final Pointer endPointer;

    public Line(Pointer startPointer, Pointer endPointer) {
        this.startPointer = startPointer;
        this.endPointer = endPointer;
    }
}
