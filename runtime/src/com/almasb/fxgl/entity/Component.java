package com.almasb.fxgl.entity;

public abstract class Component {
    
    private Entity entity;
    
    public Entity getEntity() {
        return entity;
    }
    
    void setEntity(Entity entity) {
        this.entity = entity;
        if (entity != null) {
            onAdded();
        } else {
            onRemoved();
        }
    }
    
    protected void onAdded() {
        // Override in subclasses to handle when component is added to entity
    }
    
    protected void onRemoved() {
        // Override in subclasses to handle when component is removed from entity
    }
    
    public void onUpdate(double tpf) {
        // Override in subclasses to handle updates
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{entity=" + (entity != null ? entity.getId() : "null") + "}";
    }
}