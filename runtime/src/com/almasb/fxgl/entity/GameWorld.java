package com.almasb.fxgl.entity;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameWorld {
    
    private final List<Entity> entities;
    private final Map<String, Object> properties;
    private final List<Entity> entitiesToAdd;
    private final List<Entity> entitiesToRemove;
    
    public GameWorld() {
        this.entities = new CopyOnWriteArrayList<>();
        this.properties = new HashMap<>();
        this.entitiesToAdd = new ArrayList<>();
        this.entitiesToRemove = new ArrayList<>();
    }
    
    // Entity management
    public Entity addEntity(Entity entity) {
        entitiesToAdd.add(entity);
        return entity;
    }
    
    public void removeEntity(Entity entity) {
        entitiesToRemove.add(entity);
    }
    
    public List<Entity> getEntities() {
        return new ArrayList<>(entities);
    }
    
    public List<Entity> getEntitiesByType(String type) {
        return entities.stream()
                .filter(e -> type.equals(e.getType()))
                .collect(Collectors.toList());
    }
    
    public <T extends Component> List<Entity> getEntitiesWithComponent(Class<T> componentType) {
        return entities.stream()
                .filter(e -> e.hasComponent(componentType))
                .collect(Collectors.toList());
    }
    
    public Entity getEntityById(int id) {
        return entities.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    public List<Entity> getEntitiesInRange(double x, double y, double range) {
        return entities.stream()
                .filter(e -> {
                    double dx = e.getX() - x;
                    double dy = e.getY() - y;
                    return Math.sqrt(dx * dx + dy * dy) <= range;
                })
                .collect(Collectors.toList());
    }
    
    public Entity getClosestEntity(double x, double y, String type) {
        return entities.stream()
                .filter(e -> type.equals(e.getType()))
                .min((e1, e2) -> {
                    double dist1 = Math.sqrt(Math.pow(e1.getX() - x, 2) + Math.pow(e1.getY() - y, 2));
                    double dist2 = Math.sqrt(Math.pow(e2.getX() - x, 2) + Math.pow(e2.getY() - y, 2));
                    return Double.compare(dist1, dist2);
                })
                .orElse(null);
    }
    
    public void clear() {
        entities.clear();
        entitiesToAdd.clear();
        entitiesToRemove.clear();
        properties.clear();
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
    
    // Convenience property methods
    public void setInt(String key, int value) {
        setProperty(key, value);
    }
    
    public int getInt(String key) {
        return getProperty(key, 0);
    }
    
    public void setDouble(String key, double value) {
        setProperty(key, value);
    }
    
    public double getDouble(String key) {
        return getProperty(key, 0.0);
    }
    
    public void setBoolean(String key, boolean value) {
        setProperty(key, value);
    }
    
    public boolean getBoolean(String key) {
        return getProperty(key, false);
    }
    
    public void setString(String key, String value) {
        setProperty(key, value);
    }
    
    public String getString(String key) {
        return getProperty(key, "");
    }
    
    // Update method called by the game application
    public void onUpdate(double tpf) {
        // Add pending entities
        for (Entity entity : entitiesToAdd) {
            entities.add(entity);
            entity.setWorld(this);
        }
        entitiesToAdd.clear();
        
        // Remove pending entities
        for (Entity entity : entitiesToRemove) {
            entities.remove(entity);
            entity.setWorld(null);
        }
        entitiesToRemove.clear();
        
        // Update all entities
        for (Entity entity : entities) {
            entity.update(tpf);
        }
    }
    
    public int getEntityCount() {
        return entities.size();
    }
    
    public boolean isEmpty() {
        return entities.isEmpty();
    }
    
    @Override
    public String toString() {
        return "GameWorld{" +
                "entities=" + entities.size() +
                ", properties=" + properties.size() +
                '}';
    }
}