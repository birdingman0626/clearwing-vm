package com.almasb.fxgl.entity;

import javafx.scene.Node;

public class ViewComponent extends Component {
    
    private Node view;
    
    public ViewComponent() {
        this(null);
    }
    
    public ViewComponent(Node view) {
        this.view = view;
    }
    
    public Node getView() {
        return view;
    }
    
    public void setView(Node view) {
        this.view = view;
    }
    
    public void setOpacity(double opacity) {
        if (view != null) {
            view.setOpacity(opacity);
        }
    }
    
    public double getOpacity() {
        return view != null ? view.getOpacity() : 1.0;
    }
    
    public void setVisible(boolean visible) {
        if (view != null) {
            view.setVisible(visible);
        }
    }
    
    public boolean isVisible() {
        return view != null ? view.isVisible() : true;
    }
    
    @Override
    protected void onAdded() {
        updateViewTransform();
    }
    
    @Override
    public void onUpdate(double tpf) {
        updateViewTransform();
    }
    
    private void updateViewTransform() {
        if (view == null || getEntity() == null) return;
        
        Entity entity = getEntity();
        
        // Update position
        if (entity.hasComponent(PositionComponent.class)) {
            PositionComponent pos = entity.getComponentUnsafe(PositionComponent.class);
            view.setLayoutX(pos.getX());
            view.setLayoutY(pos.getY());
        }
        
        // Update rotation
        if (entity.hasComponent(RotationComponent.class)) {
            RotationComponent rot = entity.getComponentUnsafe(RotationComponent.class);
            view.setRotate(rot.getValue());
        }
        
        // Update scale
        if (entity.hasComponent(ScaleComponent.class)) {
            ScaleComponent scale = entity.getComponentUnsafe(ScaleComponent.class);
            view.setScaleX(scale.getX());
            view.setScaleY(scale.getY());
        }
    }
    
    @Override
    public String toString() {
        return "ViewComponent{" + (view != null ? view.getClass().getSimpleName() : "null") + "}";
    }
}