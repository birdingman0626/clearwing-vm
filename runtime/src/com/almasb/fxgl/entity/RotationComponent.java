package com.almasb.fxgl.entity;

public class RotationComponent extends Component {
    
    private double value;
    
    public RotationComponent() {
        this(0);
    }
    
    public RotationComponent(double rotation) {
        this.value = rotation;
    }
    
    public double getValue() {
        return value;
    }
    
    public void setValue(double value) {
        this.value = value;
    }
    
    public void rotate(double angle) {
        this.value += angle;
    }
    
    public void rotateTowards(double targetAngle, double speed) {
        double diff = targetAngle - value;
        
        // Normalize the difference to [-180, 180]
        while (diff > 180) diff -= 360;
        while (diff < -180) diff += 360;
        
        if (Math.abs(diff) <= speed) {
            value = targetAngle;
        } else {
            value += Math.signum(diff) * speed;
        }
    }
    
    @Override
    public String toString() {
        return "RotationComponent{" + value + "°}";
    }
}