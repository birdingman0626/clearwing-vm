package com.almasb.fxgl.input;

import javafx.scene.input.MouseButton;
import javafx.geometry.Point2D;

/**
 * Interface for handling mouse events
 */
public interface MouseListener {
    
    /**
     * Called when a mouse button is pressed
     */
    void onMousePressed(MouseButton button);
    
    /**
     * Called when a mouse button is released
     */
    void onMouseReleased(MouseButton button);
    
    /**
     * Called when the mouse is moved
     */
    void onMouseMoved(Point2D position);
    
    /**
     * Called when the mouse wheel is scrolled
     */
    void onMouseWheel(double delta);
}