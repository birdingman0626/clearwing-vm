package com.almasb.fxgl.entity;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

public class Entity {
    
    private static int nextId = 0;
    
    private final int id;
    private final Map<Class<? extends Component>, Component> components;
    private final Map<String, Object> properties;
    private GameWorld world;
    private boolean active = true;
    private String type = "";
    
    public Entity() {
        this.id = nextId++;
        this.components = new HashMap<>();
        this.properties = new HashMap<>();
    }
    
    public int getId() {
        return id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public GameWorld getWorld() {
        return world;
    }
    
    void setWorld(GameWorld world) {
        this.world = world;
    }
    
    // Component management
    public <T extends Component> Entity addComponent(T component) {
        components.put((Class<? extends Component>) component.getClass(), component);
        component.setEntity(this);
        return this;
    }
    
    public <T extends Component> Entity with(T component) {
        return addComponent(component);
    }
    
    public <T extends Component> Optional<T> getComponent(Class<T> componentType) {
        return Optional.ofNullable((T) components.get(componentType));
    }
    
    public <T extends Component> T getComponentUnsafe(Class<T> componentType) {
        return (T) components.get(componentType);
    }
    
    public <T extends Component> boolean hasComponent(Class<T> componentType) {
        return components.containsKey(componentType);
    }
    
    public <T extends Component> boolean removeComponent(Class<T> componentType) {
        Component removed = components.remove(componentType);
        if (removed != null) {
            removed.setEntity(null);
            return true;
        }
        return false;
    }
    
    public List<Component> getComponents() {
        return new ArrayList<>(components.values());
    }
    
    // Property management
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key) {
        return (T) properties.get(key);
    }
    
    public <T> T getProperty(String key, T defaultValue) {
        T value = getProperty(key);
        return value != null ? value : defaultValue;
    }
    
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }
    
    public void removeProperty(String key) {
        properties.remove(key);
    }
    
    public Map<String, Object> getProperties() {
        return new HashMap<>(properties);
    }
    
    // Convenience methods for common components
    public double getX() {
        return getComponent(PositionComponent.class)
            .map(PositionComponent::getX)
            .orElse(0.0);
    }
    
    public void setX(double x) {
        getComponent(PositionComponent.class)
            .ifPresent(pos -> pos.setX(x));
    }
    
    public double getY() {
        return getComponent(PositionComponent.class)
            .map(PositionComponent::getY)
            .orElse(0.0);
    }
    
    public void setY(double y) {
        getComponent(PositionComponent.class)
            .ifPresent(pos -> pos.setY(y));
    }
    
    public Point2D getPosition() {
        return getComponent(PositionComponent.class)
            .map(PositionComponent::getValue)
            .orElse(Point2D.ZERO);
    }
    
    public void setPosition(Point2D position) {
        getComponent(PositionComponent.class)
            .ifPresent(pos -> pos.setValue(position));
    }
    
    public void setPosition(double x, double y) {
        setPosition(new Point2D(x, y));
    }
    
    public double getRotation() {
        return getComponent(RotationComponent.class)
            .map(RotationComponent::getValue)
            .orElse(0.0);
    }
    
    public void setRotation(double rotation) {
        getComponent(RotationComponent.class)
            .ifPresent(rot -> rot.setValue(rotation));
    }
    
    public Point2D getScale() {
        return getComponent(ScaleComponent.class)
            .map(ScaleComponent::getValue)
            .orElse(new Point2D(1, 1));
    }
    
    public void setScale(Point2D scale) {
        getComponent(ScaleComponent.class)
            .ifPresent(sc -> sc.setValue(scale));
    }
    
    public void setScale(double scaleX, double scaleY) {
        setScale(new Point2D(scaleX, scaleY));
    }
    
    public Node getView() {
        return getComponent(ViewComponent.class)
            .map(ViewComponent::getView)
            .orElse(null);
    }
    
    public void setView(Node view) {
        getComponent(ViewComponent.class)
            .ifPresent(v -> v.setView(view));
    }
    
    // Entity lifecycle
    public void removeFromWorld() {
        if (world != null) {
            world.removeEntity(this);
        }
    }
    
    // Update method called by the game world
    void update(double tpf) {
        if (!active) return;
        
        for (Component component : components.values()) {
            component.onUpdate(tpf);
        }
    }
    
    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", active=" + active +
                ", components=" + components.size() +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Entity entity = (Entity) obj;
        return id == entity.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}