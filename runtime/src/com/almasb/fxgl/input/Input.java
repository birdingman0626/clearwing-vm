package com.almasb.fxgl.input;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.geometry.Point2D;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Input {
    
    private final Set<KeyCode> pressedKeys;
    private final Set<MouseButton> pressedButtons;
    private final Map<UserAction, Runnable> actionCallbacks;
    private Point2D mousePosition;
    
    // Enhanced input features
    private final List<Gamepad> connectedGamepads;
    private final Map<Integer, GamepadState> gamepadStates;
    private boolean nativeInputEnabled = false;
    private Point2D mouseDelta = Point2D.ZERO;
    private double mouseWheel = 0.0;
    
    // Input event listeners
    private final List<KeyListener> keyListeners;
    private final List<MouseListener> mouseListeners;
    private final List<GamepadListener> gamepadListeners;
    
    static {
        try {
            initializeNativeInput();
        } catch (Exception e) {
            System.err.println("Failed to initialize native input: " + e.getMessage());
        }
    }
    
    public Input() {
        this.pressedKeys = new HashSet<>();
        this.pressedButtons = new HashSet<>();
        this.actionCallbacks = new HashMap<>();
        this.mousePosition = Point2D.ZERO;
        
        // Initialize enhanced input features
        this.connectedGamepads = new CopyOnWriteArrayList<>();
        this.gamepadStates = new HashMap<>();
        this.keyListeners = new CopyOnWriteArrayList<>();
        this.mouseListeners = new CopyOnWriteArrayList<>();
        this.gamepadListeners = new CopyOnWriteArrayList<>();
        
        // Enable native input if available
        enableNativeInput();
    }
    
    // Key input
    public boolean isPressed(KeyCode key) {
        return pressedKeys.contains(key);
    }
    
    public boolean isPressed(UserAction action) {
        // Simplified for clearwing-vm - in real FXGL this would check action bindings
        return false;
    }
    
    public void mockKeyPress(KeyCode key) {
        pressedKeys.add(key);
    }
    
    public void mockKeyRelease(KeyCode key) {
        pressedKeys.remove(key);
    }
    
    // Mouse input
    public boolean isPressed(MouseButton button) {
        return pressedButtons.contains(button);
    }
    
    public void mockMousePress(MouseButton button) {
        pressedButtons.add(button);
    }
    
    public void mockMouseRelease(MouseButton button) {
        pressedButtons.remove(button);
    }
    
    public Point2D getMousePositionWorld() {
        return mousePosition;
    }
    
    public Point2D getMousePositionUI() {
        return mousePosition;
    }
    
    public double getMouseX() {
        return mousePosition.getX();
    }
    
    public double getMouseY() {
        return mousePosition.getY();
    }
    
    public void mockMousePosition(double x, double y) {
        this.mousePosition = new Point2D(x, y);
    }
    
    // Action binding
    public void onAction(UserAction action, Runnable callback) {
        actionCallbacks.put(action, callback);
    }
    
    public void onActionBegin(UserAction action, Runnable callback) {
        actionCallbacks.put(action, callback);
    }
    
    public void onActionEnd(UserAction action, Runnable callback) {
        actionCallbacks.put(action, callback);
    }
    
    public void triggerAction(UserAction action) {
        Runnable callback = actionCallbacks.get(action);
        if (callback != null) {
            callback.run();
        }
    }
    
    // Clear all input state
    public void clearAll() {
        pressedKeys.clear();
        pressedButtons.clear();
        mousePosition = Point2D.ZERO;
    }
    
    // Mock methods for testing
    public void mockInput(KeyCode key, boolean pressed) {
        if (pressed) {
            mockKeyPress(key);
        } else {
            mockKeyRelease(key);
        }
    }
    
    public void mockInput(MouseButton button, boolean pressed) {
        if (pressed) {
            mockMousePress(button);
        } else {
            mockMouseRelease(button);
        }
    }
    
    // Enhanced input methods
    
    /**
     * Enable native input handling for better performance
     */
    public boolean enableNativeInput() {
        if (!nativeInputEnabled) {
            nativeInputEnabled = enableNativeInputNative();
            if (nativeInputEnabled) {
                refreshGamepads();
                System.out.println("Native input enabled successfully");
            }
        }
        return nativeInputEnabled;
    }
    
    /**
     * Disable native input handling
     */
    public void disableNativeInput() {
        if (nativeInputEnabled) {
            disableNativeInputNative();
            nativeInputEnabled = false;
            connectedGamepads.clear();
            gamepadStates.clear();
        }
    }
    
    /**
     * Get mouse movement delta since last frame
     */
    public Point2D getMouseDelta() {
        return mouseDelta;
    }
    
    /**
     * Get mouse wheel scroll value
     */
    public double getMouseWheel() {
        return mouseWheel;
    }
    
    /**
     * Reset mouse delta and wheel (call at end of frame)
     */
    public void resetMouseDelta() {
        mouseDelta = Point2D.ZERO;
        mouseWheel = 0.0;
    }
    
    // Gamepad support
    
    /**
     * Get list of connected gamepads
     */
    public List<Gamepad> getGamepads() {
        return new ArrayList<>(connectedGamepads);
    }
    
    /**
     * Get gamepad by index
     */
    public Gamepad getGamepad(int index) {
        if (index >= 0 && index < connectedGamepads.size()) {
            return connectedGamepads.get(index);
        }
        return null;
    }
    
    /**
     * Check if gamepad button is pressed
     */
    public boolean isGamepadButtonPressed(int gamepadIndex, GamepadButton button) {
        GamepadState state = gamepadStates.get(gamepadIndex);
        return state != null && state.isButtonPressed(button);
    }
    
    /**
     * Get gamepad axis value (-1.0 to 1.0)
     */
    public double getGamepadAxisValue(int gamepadIndex, GamepadAxis axis) {
        GamepadState state = gamepadStates.get(gamepadIndex);
        return state != null ? state.getAxisValue(axis) : 0.0;
    }
    
    /**
     * Update gamepad states (called from native input polling)
     */
    public void updateGamepads() {
        if (nativeInputEnabled) {
            updateGamepadsNative();
        }
    }
    
    /**
     * Refresh connected gamepads list
     */
    public void refreshGamepads() {
        if (nativeInputEnabled) {
            connectedGamepads.clear();
            gamepadStates.clear();
            
            int gamepadCount = getGamepadCountNative();
            for (int i = 0; i < gamepadCount; i++) {
                String name = getGamepadNameNative(i);
                if (name != null) {
                    Gamepad gamepad = new Gamepad(i, name);
                    connectedGamepads.add(gamepad);
                    gamepadStates.put(i, new GamepadState());
                }
            }
        }
    }
    
    // Event listeners
    
    /**
     * Add key event listener
     */
    public void addKeyListener(KeyListener listener) {
        keyListeners.add(listener);
    }
    
    /**
     * Remove key event listener
     */
    public void removeKeyListener(KeyListener listener) {
        keyListeners.remove(listener);
    }
    
    /**
     * Add mouse event listener
     */
    public void addMouseListener(MouseListener listener) {
        mouseListeners.add(listener);
    }
    
    /**
     * Remove mouse event listener
     */
    public void removeMouseListener(MouseListener listener) {
        mouseListeners.remove(listener);
    }
    
    /**
     * Add gamepad event listener
     */
    public void addGamepadListener(GamepadListener listener) {
        gamepadListeners.add(listener);
    }
    
    /**
     * Remove gamepad event listener
     */
    public void removeGamepadListener(GamepadListener listener) {
        gamepadListeners.remove(listener);
    }
    
    // Internal methods for native callbacks
    
    void onKeyPressed(int keyCode) {
        // Convert native key code to JavaFX KeyCode
        KeyCode key = convertNativeKeyCode(keyCode);
        if (key != null) {
            pressedKeys.add(key);
            for (KeyListener listener : keyListeners) {
                listener.onKeyPressed(key);
            }
        }
    }
    
    void onKeyReleased(int keyCode) {
        KeyCode key = convertNativeKeyCode(keyCode);
        if (key != null) {
            pressedKeys.remove(key);
            for (KeyListener listener : keyListeners) {
                listener.onKeyReleased(key);
            }
        }
    }
    
    void onMousePressed(int button) {
        MouseButton mouseButton = convertNativeMouseButton(button);
        if (mouseButton != null) {
            pressedButtons.add(mouseButton);
            for (MouseListener listener : mouseListeners) {
                listener.onMousePressed(mouseButton);
            }
        }
    }
    
    void onMouseReleased(int button) {
        MouseButton mouseButton = convertNativeMouseButton(button);
        if (mouseButton != null) {
            pressedButtons.remove(mouseButton);
            for (MouseListener listener : mouseListeners) {
                listener.onMouseReleased(mouseButton);
            }
        }
    }
    
    void onMouseMoved(double x, double y) {
        Point2D newPosition = new Point2D(x, y);
        mouseDelta = newPosition.subtract(mousePosition);
        mousePosition = newPosition;
        
        for (MouseListener listener : mouseListeners) {
            listener.onMouseMoved(mousePosition);
        }
    }
    
    void onMouseWheel(double delta) {
        mouseWheel += delta;
        
        for (MouseListener listener : mouseListeners) {
            listener.onMouseWheel(delta);
        }
    }
    
    void onGamepadConnected(int index) {
        refreshGamepads();
        
        for (GamepadListener listener : gamepadListeners) {
            listener.onGamepadConnected(index);
        }
    }
    
    void onGamepadDisconnected(int index) {
        connectedGamepads.removeIf(gamepad -> gamepad.getIndex() == index);
        gamepadStates.remove(index);
        
        for (GamepadListener listener : gamepadListeners) {
            listener.onGamepadDisconnected(index);
        }
    }
    
    void onGamepadButtonPressed(int gamepadIndex, int button) {
        GamepadButton gamepadButton = GamepadButton.fromIndex(button);
        if (gamepadButton != null) {
            GamepadState state = gamepadStates.get(gamepadIndex);
            if (state != null) {
                state.setButtonPressed(gamepadButton, true);
                
                for (GamepadListener listener : gamepadListeners) {
                    listener.onGamepadButtonPressed(gamepadIndex, gamepadButton);
                }
            }
        }
    }
    
    void onGamepadButtonReleased(int gamepadIndex, int button) {
        GamepadButton gamepadButton = GamepadButton.fromIndex(button);
        if (gamepadButton != null) {
            GamepadState state = gamepadStates.get(gamepadIndex);
            if (state != null) {
                state.setButtonPressed(gamepadButton, false);
                
                for (GamepadListener listener : gamepadListeners) {
                    listener.onGamepadButtonReleased(gamepadIndex, gamepadButton);
                }
            }
        }
    }
    
    void onGamepadAxisChanged(int gamepadIndex, int axis, double value) {
        GamepadAxis gamepadAxis = GamepadAxis.fromIndex(axis);
        if (gamepadAxis != null) {
            GamepadState state = gamepadStates.get(gamepadIndex);
            if (state != null) {
                state.setAxisValue(gamepadAxis, value);
                
                for (GamepadListener listener : gamepadListeners) {
                    listener.onGamepadAxisChanged(gamepadIndex, gamepadAxis, value);
                }
            }
        }
    }
    
    // Helper conversion methods
    
    private KeyCode convertNativeKeyCode(int nativeCode) {
        // Simplified conversion - in real implementation would have complete mapping
        switch (nativeCode) {
            case 32: return KeyCode.SPACE;
            case 13: return KeyCode.ENTER;
            case 27: return KeyCode.ESCAPE;
            case 37: return KeyCode.LEFT;
            case 38: return KeyCode.UP;
            case 39: return KeyCode.RIGHT;
            case 40: return KeyCode.DOWN;
            default:
                if (nativeCode >= 65 && nativeCode <= 90) {
                    // A-Z keys
                    return KeyCode.valueOf(String.valueOf((char)nativeCode));
                }
                if (nativeCode >= 48 && nativeCode <= 57) {
                    // 0-9 keys
                    return KeyCode.valueOf("DIGIT" + (char)nativeCode);
                }
                return null;
        }
    }
    
    private MouseButton convertNativeMouseButton(int nativeButton) {
        switch (nativeButton) {
            case 0: return MouseButton.PRIMARY;
            case 1: return MouseButton.SECONDARY;
            case 2: return MouseButton.MIDDLE;
            default: return null;
        }
    }
    
    /**
     * Check if native input is enabled
     */
    public boolean isNativeInputEnabled() {
        return nativeInputEnabled;
    }
    
    // Native method declarations
    private static native void initializeNativeInput();
    private static native boolean enableNativeInputNative();
    private static native void disableNativeInputNative();
    private static native void updateGamepadsNative();
    private static native int getGamepadCountNative();
    private static native String getGamepadNameNative(int index);
    
    @Override
    public String toString() {
        return "Input{" +
                "pressedKeys=" + pressedKeys.size() +
                ", pressedButtons=" + pressedButtons.size() +
                ", mousePosition=" + mousePosition +
                ", gamepads=" + connectedGamepads.size() +
                ", nativeInput=" + nativeInputEnabled +
                '}';
    }
    
    /*JNI
    #ifdef _WIN32
    #include <windows.h>
    #include <xinput.h>
    #pragma comment(lib, "xinput.lib")
    #elif defined(__APPLE__)
    #include <CoreFoundation/CoreFoundation.h>
    #include <IOKit/hid/IOHIDLib.h>
    #elif defined(__linux__)
    #include <linux/input.h>
    #include <fcntl.h>
    #include <unistd.h>
    #include <dirent.h>
    #endif
    
    #include <cstdlib>
    #include <cstring>
    #include <iostream>
    #include <vector>
    #include <map>
    
    // Input state
    static bool g_nativeInputEnabled = false;
    static std::vector<std::string> g_gamepadNames;
    
    #ifdef _WIN32
    static XINPUT_STATE g_gamepadStates[4];
    #endif
    
    // Helper functions for Windows XInput
    #ifdef _WIN32
    static void updateXInputGamepads() {
        for (DWORD i = 0; i < 4; ++i) {
            XINPUT_STATE state;
            DWORD result = XInputGetState(i, &state);
            
            if (result == ERROR_SUCCESS) {
                // Update gamepad state
                g_gamepadStates[i] = state;
                
                // TODO: Call Java callbacks for button/axis changes
            }
        }
    }
    #endif
    
    // Native method implementations
    JNIEXPORT void JNICALL Java_com_almasb_fxgl_input_Input_initializeNativeInput(JNIEnv* env, jclass clazz) {
        // Initialize platform-specific input systems
        #ifdef _WIN32
        // XInput is automatically available
        #elif defined(__APPLE__)
        // Initialize HID Manager
        #elif defined(__linux__)
        // Initialize evdev input
        #endif
    }
    
    JNIEXPORT jboolean JNICALL Java_com_almasb_fxgl_input_Input_enableNativeInputNative(JNIEnv* env, jclass clazz) {
        if (g_nativeInputEnabled) {
            return JNI_TRUE;
        }
        
        #ifdef _WIN32
        // XInput initialization (always available)
        g_nativeInputEnabled = true;
        
        // Populate gamepad names
        g_gamepadNames.clear();
        for (DWORD i = 0; i < 4; ++i) {
            XINPUT_STATE state;
            if (XInputGetState(i, &state) == ERROR_SUCCESS) {
                g_gamepadNames.push_back("XInput Controller " + std::to_string(i));
            } else {
                g_gamepadNames.push_back("");
            }
        }
        #elif defined(__APPLE__)
        // macOS HID implementation
        g_nativeInputEnabled = true;
        #elif defined(__linux__)
        // Linux evdev implementation
        g_nativeInputEnabled = true;
        #endif
        
        return g_nativeInputEnabled ? JNI_TRUE : JNI_FALSE;
    }
    
    JNIEXPORT void JNICALL Java_com_almasb_fxgl_input_Input_disableNativeInputNative(JNIEnv* env, jclass clazz) {
        g_nativeInputEnabled = false;
        g_gamepadNames.clear();
    }
    
    JNIEXPORT void JNICALL Java_com_almasb_fxgl_input_Input_updateGamepadsNative(JNIEnv* env, jclass clazz) {
        if (!g_nativeInputEnabled) return;
        
        #ifdef _WIN32
        updateXInputGamepads();
        #elif defined(__APPLE__)
        // Update HID gamepad states
        #elif defined(__linux__)
        // Update evdev gamepad states
        #endif
    }
    
    JNIEXPORT jint JNICALL Java_com_almasb_fxgl_input_Input_getGamepadCountNative(JNIEnv* env, jclass clazz) {
        if (!g_nativeInputEnabled) return 0;
        
        #ifdef _WIN32
        int count = 0;
        for (DWORD i = 0; i < 4; ++i) {
            XINPUT_STATE state;
            if (XInputGetState(i, &state) == ERROR_SUCCESS) {
                count++;
            }
        }
        return count;
        #else
        return 0;
        #endif
    }
    
    JNIEXPORT jstring JNICALL Java_com_almasb_fxgl_input_Input_getGamepadNameNative(JNIEnv* env, jclass clazz, jint index) {
        if (!g_nativeInputEnabled || index < 0 || index >= (int)g_gamepadNames.size()) {
            return nullptr;
        }
        
        const std::string& name = g_gamepadNames[index];
        if (name.empty()) {
            return nullptr;
        }
        
        return env->NewStringUTF(name.c_str());
    }
    */
}