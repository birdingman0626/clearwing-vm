package com.almasb.fxgl.entity;

import javafx.geometry.Point2D;

public class ScaleComponent extends Component {
    
    private Point2D value;
    
    public ScaleComponent() {
        this(1, 1);
    }
    
    public ScaleComponent(double scale) {
        this(scale, scale);
    }
    
    public ScaleComponent(double scaleX, double scaleY) {
        this(new Point2D(scaleX, scaleY));
    }
    
    public ScaleComponent(Point2D scale) {
        this.value = scale;
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
    
    public void setX(double scaleX) {
        value = new Point2D(scaleX, value.getY());
    }
    
    public double getY() {
        return value.getY();
    }
    
    public void setY(double scaleY) {
        value = new Point2D(value.getX(), scaleY);
    }
    
    public void scale(double factor) {
        value = value.multiply(factor);
    }
    
    public void scale(double factorX, double factorY) {
        value = new Point2D(value.getX() * factorX, value.getY() * factorY);
    }
    
    @Override
    public String toString() {
        return "ScaleComponent{" + value + "}";
    }
}