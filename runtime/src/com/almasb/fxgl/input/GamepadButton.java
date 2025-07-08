package com.almasb.fxgl.input;

/**
 * Enumeration of gamepad buttons
 */
public enum GamepadButton {
    A(0),
    B(1),
    X(2),
    Y(3),
    LEFT_SHOULDER(4),
    RIGHT_SHOULDER(5),
    BACK(6),
    START(7),
    LEFT_STICK(8),
    RIGHT_STICK(9),
    DPAD_UP(10),
    DPAD_DOWN(11),
    DPAD_LEFT(12),
    DPAD_RIGHT(13);
    
    private final int index;
    
    GamepadButton(int index) {
        this.index = index;
    }
    
    public int getIndex() {
        return index;
    }
    
    public static GamepadButton fromIndex(int index) {
        for (GamepadButton button : values()) {
            if (button.index == index) {
                return button;
            }
        }
        return null;
    }
}