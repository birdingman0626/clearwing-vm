package com.almasb.fxgl.input;

import javafx.scene.input.KeyCode;

/**
 * Interface for handling keyboard events
 */
public interface KeyListener {
    
    /**
     * Called when a key is pressed
     */
    void onKeyPressed(KeyCode key);
    
    /**
     * Called when a key is released
     */
    void onKeyReleased(KeyCode key);
}