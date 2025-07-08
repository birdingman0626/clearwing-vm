package com.almasb.fxgl.input;

/**
 * Enumeration of gamepad axes
 */
public enum GamepadAxis {
    LEFT_STICK_X(0),
    LEFT_STICK_Y(1),
    RIGHT_STICK_X(2),
    RIGHT_STICK_Y(3),
    LEFT_TRIGGER(4),
    RIGHT_TRIGGER(5);
    
    private final int index;
    
    GamepadAxis(int index) {
        this.index = index;
    }
    
    public int getIndex() {
        return index;
    }
    
    public static GamepadAxis fromIndex(int index) {
        for (GamepadAxis axis : values()) {
            if (axis.index == index) {
                return axis;
            }
        }
        return null;
    }
}