package com.almasb.fxgl.input;

import java.util.Map;
import java.util.HashMap;

/**
 * Tracks the current state of a gamepad
 */
public class GamepadState {
    
    private final Map<GamepadButton, Boolean> buttonStates;
    private final Map<GamepadAxis, Double> axisValues;
    
    public GamepadState() {
        this.buttonStates = new HashMap<>();
        this.axisValues = new HashMap<>();
        
        // Initialize all buttons as released
        for (GamepadButton button : GamepadButton.values()) {
            buttonStates.put(button, false);
        }
        
        // Initialize all axes as center/zero
        for (GamepadAxis axis : GamepadAxis.values()) {
            axisValues.put(axis, 0.0);
        }
    }
    
    public boolean isButtonPressed(GamepadButton button) {
        return buttonStates.getOrDefault(button, false);
    }
    
    public void setButtonPressed(GamepadButton button, boolean pressed) {
        buttonStates.put(button, pressed);
    }
    
    public double getAxisValue(GamepadAxis axis) {
        return axisValues.getOrDefault(axis, 0.0);
    }
    
    public void setAxisValue(GamepadAxis axis, double value) {
        // Clamp value to valid range
        value = Math.max(-1.0, Math.min(1.0, value));
        axisValues.put(axis, value);
    }
    
    /**
     * Reset all button and axis states
     */
    public void reset() {
        for (GamepadButton button : GamepadButton.values()) {
            buttonStates.put(button, false);
        }
        for (GamepadAxis axis : GamepadAxis.values()) {
            axisValues.put(axis, 0.0);
        }
    }
    
    @Override
    public String toString() {
        int pressedButtons = (int) buttonStates.values().stream().mapToInt(pressed -> pressed ? 1 : 0).sum();
        int activeAxes = (int) axisValues.values().stream().mapToInt(value -> Math.abs(value) > 0.1 ? 1 : 0).sum();
        
        return "GamepadState{" +
                "pressedButtons=" + pressedButtons +
                ", activeAxes=" + activeAxes +
                '}';
    }
}