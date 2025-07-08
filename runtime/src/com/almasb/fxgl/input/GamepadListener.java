package com.almasb.fxgl.input;

/**
 * Interface for handling gamepad events
 */
public interface GamepadListener {
    
    /**
     * Called when a gamepad is connected
     */
    void onGamepadConnected(int gamepadIndex);
    
    /**
     * Called when a gamepad is disconnected
     */
    void onGamepadDisconnected(int gamepadIndex);
    
    /**
     * Called when a gamepad button is pressed
     */
    void onGamepadButtonPressed(int gamepadIndex, GamepadButton button);
    
    /**
     * Called when a gamepad button is released
     */
    void onGamepadButtonReleased(int gamepadIndex, GamepadButton button);
    
    /**
     * Called when a gamepad axis value changes
     */
    void onGamepadAxisChanged(int gamepadIndex, GamepadAxis axis, double value);
}