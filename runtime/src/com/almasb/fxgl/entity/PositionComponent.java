package com.almasb.fxgl.entity;

import javafx.geometry.Point2D;

public class PositionComponent extends Component {
    
    private Point2D value;
    
    public PositionComponent() {
        this(0, 0);
    }
    
    public PositionComponent(double x, double y) {
        this(new Point2D(x, y));
    }
    
    public PositionComponent(Point2D position) {
        this.value = position;
    }
    
    public Point2D getValue() {
        return value;
    }
    
    public void setValue(Point2D value) {
        this.value = value;
    }
    
    public double getX() {
        return value.getX();
    }
    
    public void setX(double x) {
        value = new Point2D(x, value.getY());
    }
    
    public double getY() {
        return value.getY();
    }
    
    public void setY(double y) {
        value = new Point2D(value.getX(), y);
    }
    
    public void translate(double dx, double dy) {
        value = value.add(dx, dy);
    }
    
    public void translate(Point2D delta) {
        value = value.add(delta);
    }
    
    public double distance(PositionComponent other) {
        return value.distance(other.getValue());
    }
    
    public double distance(Point2D point) {
        return value.distance(point);
    }
    
    public double distance(double x, double y) {
        return value.distance(x, y);
    }
    
    @Override
    public String toString() {
        return "PositionComponent{" + value + "}";
    }
}