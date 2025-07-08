package com.almasb.fxgl.app;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.scene.GameScene;
import com.almasb.fxgl.graphics.GraphicsBackend;
import com.almasb.fxgl.audio.AudioSystem;
import com.almasb.fxgl.assets.AssetLoader;

public abstract class GameApplication extends Application {
    
    private static GameApplication instance;
    private GameWorld gameWorld;
    private Input input;
    private GameScene gameScene;
    private GameSettings settings;
    private GraphicsBackend graphicsBackend;
    private AudioSystem audioSystem;
    private AssetLoader assetLoader;
    
    public GameApplication() {
        instance = this;
        this.gameWorld = new GameWorld();
        this.input = new Input();
        this.gameScene = new GameScene();
        this.settings = new GameSettings();
        this.graphicsBackend = GraphicsBackend.getInstance();
        this.audioSystem = AudioSystem.getInstance();
        this.assetLoader = AssetLoader.getInstance();
    }
    
    public static GameApplication getInstance() {
        return instance;
    }
    
    @Override
    public final void start(Stage primaryStage) throws Exception {
        initSettings(settings);
        
        // Initialize enhanced graphics backend
        if (!graphicsBackend.initialize(settings.getWidth(), settings.getHeight())) {
            System.err.println("Failed to initialize graphics backend, falling back to JavaFX");
        } else {
            System.out.println("Enhanced graphics backend initialized successfully");
        }
        
        // Initialize audio system
        if (!audioSystem.initialize()) {
            System.err.println("Failed to initialize audio system, audio will be disabled");
        } else {
            System.out.println("Audio system initialized successfully");
        }
        
        // Initialize asset loader
        if (!assetLoader.initialize()) {
            System.err.println("Failed to initialize asset loader, asset loading will be limited");
        } else {
            System.out.println("Asset loader initialized successfully");
        }
        
        primaryStage.setTitle(settings.getTitle());
        primaryStage.setWidth(settings.getWidth());
        primaryStage.setHeight(settings.getHeight());
        primaryStage.setResizable(settings.isResizable());
        
        Scene scene = new Scene(gameScene.getRoot(), settings.getWidth(), settings.getHeight());
        primaryStage.setScene(scene);
        primaryStage.show();
        
        initInput();
        initGameVars(gameWorld.getProperties());
        initGame();
        initPhysics();
        initUI();
        
        startGameLoop();
    }
    
    protected abstract void initSettings(GameSettings settings);
    
    protected void initInput() {
        // Override to setup input bindings
    }
    
    protected void initGameVars(java.util.Map<String, Object> vars) {
        // Override to initialize game variables
    }
    
    protected void initGame() {
        // Override to initialize game objects
    }
    
    protected void initPhysics() {
        // Override to initialize physics
    }
    
    protected void initUI() {
        // Override to initialize UI
    }
    
    protected void onUpdate(double tpf) {
        // Override to handle game updates
    }
    
    /**
     * Enhanced render method that can use hardware acceleration
     */
    protected void onRender() {
        // Override to handle custom rendering with GraphicsBackend
        if (graphicsBackend.isInitialized()) {
            graphicsBackend.beginFrame();
            // Custom rendering code goes here
            graphicsBackend.endFrame();
        }
    }
    
    private void startGameLoop() {
        // Enhanced game loop with rendering support
        new Thread(() -> {
            long lastTime = System.nanoTime();
            
            while (true) {
                long currentTime = System.nanoTime();
                double tpf = (currentTime - lastTime) / 1_000_000_000.0;
                lastTime = currentTime;
                
                javafx.application.Platform.runLater(() -> {
                    onUpdate(tpf);
                    gameWorld.onUpdate(tpf);
                    onRender();
                });
                
                try {
                    Thread.sleep(16); // ~60 FPS
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }
    
    public GameWorld getGameWorld() {
        return gameWorld;
    }
    
    public Input getInput() {
        return input;
    }
    
    public GameScene getGameScene() {
        return gameScene;
    }
    
    public GameSettings getSettings() {
        return settings;
    }
    
    public GraphicsBackend getGraphicsBackend() {
        return graphicsBackend;
    }
    
    public AudioSystem getAudioSystem() {
        return audioSystem;
    }
    
    public AssetLoader getAssetLoader() {
        return assetLoader;
    }
    
    // Static utility methods for global access
    public static GameWorld getWorldProperties() {
        return getInstance().getGameWorld();
    }
    
    public static Input getInputService() {
        return getInstance().getInput();
    }
    
    public static GameScene getSceneService() {
        return getInstance().getGameScene();
    }
}