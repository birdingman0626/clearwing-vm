package com.almasb.fxgl.input;

/**
 * Represents a connected gamepad/controller
 */
public class Gamepad {
    
    private final int index;
    private final String name;
    
    public Gamepad(int index, String name) {
        this.index = index;
        this.name = name;
    }
    
    public int getIndex() {
        return index;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return "Gamepad{" +
                "index=" + index +
                ", name='" + name + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Gamepad gamepad = (Gamepad) o;
        return index == gamepad.index;
    }
    
    @Override
    public int hashCode() {
        return index;
    }
}