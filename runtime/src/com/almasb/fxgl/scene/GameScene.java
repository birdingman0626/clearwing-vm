package com.almasb.fxgl.scene;

import javafx.scene.Parent;
import javafx.scene.Group;
import javafx.scene.Node;
import java.util.List;
import java.util.ArrayList;

public class GameScene {
    
    private final Group root;
    private final Group gameRoot;
    private final Group uiRoot;
    private final List<Node> gameNodes;
    private final List<Node> uiNodes;
    
    public GameScene() {
        this.root = new Group();
        this.gameRoot = new Group();
        this.uiRoot = new Group();
        this.gameNodes = new ArrayList<>();
        this.uiNodes = new ArrayList<>();
        
        root.getChildren().addAll(gameRoot, uiRoot);
    }
    
    public Parent getRoot() {
        return root;
    }
    
    public Group getGameRoot() {
        return gameRoot;
    }
    
    public Group getUIRoot() {
        return uiRoot;
    }
    
    // Game layer management
    public void addGameView(Node node) {
        gameNodes.add(node);
        gameRoot.getChildren().add(node);
    }
    
    public void removeGameView(Node node) {
        gameNodes.remove(node);
        gameRoot.getChildren().remove(node);
    }
    
    public void clearGameViews() {
        gameNodes.clear();
        gameRoot.getChildren().clear();
    }
    
    public List<Node> getGameViews() {
        return new ArrayList<>(gameNodes);
    }
    
    // UI layer management
    public void addUINode(Node node) {
        uiNodes.add(node);
        uiRoot.getChildren().add(node);
    }
    
    public void removeUINode(Node node) {
        uiNodes.remove(node);
        uiRoot.getChildren().remove(node);
    }
    
    public void clearUINodes() {
        uiNodes.clear();
        uiRoot.getChildren().clear();
    }
    
    public List<Node> getUINodes() {
        return new ArrayList<>(uiNodes);
    }
    
    // Convenience methods
    public void addChild(Node node) {
        addGameView(node);
    }
    
    public void removeChild(Node node) {
        removeGameView(node);
        removeUINode(node);
    }
    
    public void clear() {
        clearGameViews();
        clearUINodes();
    }
    
    // Camera simulation (simplified for clearwing-vm)
    public void setViewport(double x, double y) {
        gameRoot.setLayoutX(-x);
        gameRoot.setLayoutY(-y);
    }
    
    public double getViewportX() {
        return -gameRoot.getLayoutX();
    }
    
    public double getViewportY() {
        return -gameRoot.getLayoutY();
    }
    
    public void centerViewport(double centerX, double centerY, double screenWidth, double screenHeight) {
        setViewport(centerX - screenWidth / 2, centerY - screenHeight / 2);
    }
    
    @Override
    public String toString() {
        return "GameScene{" +
                "gameNodes=" + gameNodes.size() +
                ", uiNodes=" + uiNodes.size() +
                ", viewport=(" + getViewportX() + ", " + getViewportY() + ")" +
                '}';
    }
}