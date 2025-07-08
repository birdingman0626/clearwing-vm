package com.almasb.fxgl.input;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

public class UserAction {
    
    private final String name;
    private final ActionType type;
    
    public UserAction(String name) {
        this(name, ActionType.ON_ACTION);
    }
    
    public UserAction(String name, ActionType type) {
        this.name = name;
        this.type = type;
    }
    
    public String getName() {
        return name;
    }
    
    public ActionType getType() {
        return type;
    }
    
    public static UserAction action(String name) {
        return new UserAction(name);
    }
    
    public static UserAction actionBegin(String name) {
        return new UserAction(name, ActionType.ON_ACTION_BEGIN);
    }
    
    public static UserAction actionEnd(String name) {
        return new UserAction(name, ActionType.ON_ACTION_END);
    }
    
    @Override
    public String toString() {
        return "UserAction{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserAction that = (UserAction) obj;
        return name.equals(that.name) && type == that.type;
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, type);
    }
    
    public enum ActionType {
        ON_ACTION,
        ON_ACTION_BEGIN,
        ON_ACTION_END
    }
}