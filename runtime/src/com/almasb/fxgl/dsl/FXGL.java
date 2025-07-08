package com.almasb.fxgl.dsl;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.PositionComponent;
import com.almasb.fxgl.entity.RotationComponent;
import com.almasb.fxgl.entity.ScaleComponent;
import com.almasb.fxgl.entity.ViewComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.scene.GameScene;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

import java.util.List;

public final class FXGL {
    
    private FXGL() {
        // Utility class
    }
    
    // Entity creation and management
    public static Entity entityBuilder() {
        return new Entity();
    }
    
    public static Entity entityBuilder(String type) {
        Entity entity = new Entity();
        entity.setType(type);
        return entity;
    }
    
    public static Entity spawn(String entityType) {
        return spawn(entityType, 0, 0);
    }
    
    public static Entity spawn(String entityType, double x, double y) {
        Entity entity = entityBuilder(entityType)
                .with(new PositionComponent(x, y))
                .with(new RotationComponent())
                .with(new ScaleComponent())
                .with(new ViewComponent());
        
        getGameWorld().addEntity(entity);
        return entity;
    }
    
    public static Entity spawn(String entityType, Point2D position) {
        return spawn(entityType, position.getX(), position.getY());
    }
    
    // Entity factory methods with common views
    public static Entity spawnRectangle(double x, double y, double width, double height, Color color) {
        Rectangle rect = new Rectangle(width, height, color);
        Entity entity = spawn("Rectangle", x, y);
        entity.getComponentUnsafe(ViewComponent.class).setView(rect);
        return entity;
    }
    
    public static Entity spawnCircle(double x, double y, double radius, Color color) {
        Circle circle = new Circle(radius, color);
        Entity entity = spawn("Circle", x, y);
        entity.getComponentUnsafe(ViewComponent.class).setView(circle);
        return entity;
    }
    
    public static Entity spawnText(double x, double y, String text, Color color) {
        Text textNode = new Text(text);
        textNode.setFill(color);
        Entity entity = spawn("Text", x, y);
        entity.getComponentUnsafe(ViewComponent.class).setView(textNode);
        return entity;
    }
    
    // World access
    public static GameWorld getGameWorld() {
        return GameApplication.getInstance().getGameWorld();
    }
    
    public static List<Entity> getEntitiesByType(String type) {
        return getGameWorld().getEntitiesByType(type);
    }
    
    public static Entity getEntityById(int id) {
        return getGameWorld().getEntityById(id);
    }
    
    public static List<Entity> getEntitiesInRange(double x, double y, double range) {
        return getGameWorld().getEntitiesInRange(x, y, range);
    }
    
    public static Entity getClosestEntity(double x, double y, String type) {
        return getGameWorld().getClosestEntity(x, y, type);
    }
    
    // World properties
    public static void set(String key, Object value) {
        getGameWorld().setProperty(key, value);
    }
    
    public static <T> T get(String key) {
        return getGameWorld().getProperty(key);
    }
    
    public static int geti(String key) {
        return getGameWorld().getInt(key);
    }
    
    public static double getd(String key) {
        return getGameWorld().getDouble(key);
    }
    
    public static boolean getb(String key) {
        return getGameWorld().getBoolean(key);
    }
    
    public static String gets(String key) {
        return getGameWorld().getString(key);
    }
    
    public static void inc(String key, int value) {
        set(key, geti(key) + value);
    }
    
    public static void inc(String key, double value) {
        set(key, getd(key) + value);
    }
    
    // Input access
    public static Input getInput() {
        return GameApplication.getInstance().getInput();
    }
    
    public static boolean isPressed(KeyCode key) {
        return getInput().isPressed(key);
    }
    
    public static boolean isPressed(MouseButton button) {
        return getInput().isPressed(button);
    }
    
    public static boolean isPressed(UserAction action) {
        return getInput().isPressed(action);
    }
    
    public static Point2D getMousePosition() {
        return getInput().getMousePositionWorld();
    }
    
    public static double getMouseX() {
        return getInput().getMouseX();
    }
    
    public static double getMouseY() {
        return getInput().getMouseY();
    }
    
    // Scene access
    public static GameScene getGameScene() {
        return GameApplication.getInstance().getGameScene();
    }
    
    public static void addUINode(Node node) {
        getGameScene().addUINode(node);
    }
    
    public static void removeUINode(Node node) {
        getGameScene().removeUINode(node);
    }
    
    // Utility methods
    public static double random(double min, double max) {
        return Math.random() * (max - min) + min;
    }
    
    public static int random(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }
    
    public static double distance(Entity e1, Entity e2) {
        return e1.getPosition().distance(e2.getPosition());
    }
    
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
    
    public static Point2D vec2(double x, double y) {
        return new Point2D(x, y);
    }
    
    // Logging (simplified for clearwing-vm)
    public static void log(String message) {
        System.out.println("[FXGL] " + message);
    }
    
    public static void log(String format, Object... args) {
        System.out.println("[FXGL] " + String.format(format, args));
    }
}