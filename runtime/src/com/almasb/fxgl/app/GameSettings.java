package com.almasb.fxgl.app;

public class GameSettings {
    
    private String title = "FXGL Game";
    private String version = "1.0";
    private int width = 800;
    private int height = 600;
    private boolean resizable = false;
    private boolean fullScreen = false;
    private boolean introEnabled = true;
    private boolean menuEnabled = true;
    private boolean userProfileEnabled = false;
    private boolean developerMenuEnabled = false;
    private boolean achievementsEnabled = false;
    private boolean soundMenuEnabled = true;
    private boolean profileSaveLoadEnabled = false;
    private String cssFile = "";
    private String fontUI = "";
    private String fontMono = "";
    private String fontText = "";
    private String fontGame = "";
    private String appIcon = "";
    private boolean singleStep = false;
    private double engineVersion = 17.0;
    private int targetFramerate = 60;
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public boolean isResizable() {
        return resizable;
    }
    
    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }
    
    public boolean isFullScreen() {
        return fullScreen;
    }
    
    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }
    
    public boolean isIntroEnabled() {
        return introEnabled;
    }
    
    public void setIntroEnabled(boolean introEnabled) {
        this.introEnabled = introEnabled;
    }
    
    public boolean isMenuEnabled() {
        return menuEnabled;
    }
    
    public void setMenuEnabled(boolean menuEnabled) {
        this.menuEnabled = menuEnabled;
    }
    
    public boolean isUserProfileEnabled() {
        return userProfileEnabled;
    }
    
    public void setUserProfileEnabled(boolean userProfileEnabled) {
        this.userProfileEnabled = userProfileEnabled;
    }
    
    public boolean isDeveloperMenuEnabled() {
        return developerMenuEnabled;
    }
    
    public void setDeveloperMenuEnabled(boolean developerMenuEnabled) {
        this.developerMenuEnabled = developerMenuEnabled;
    }
    
    public boolean isAchievementsEnabled() {
        return achievementsEnabled;
    }
    
    public void setAchievementsEnabled(boolean achievementsEnabled) {
        this.achievementsEnabled = achievementsEnabled;
    }
    
    public boolean isSoundMenuEnabled() {
        return soundMenuEnabled;
    }
    
    public void setSoundMenuEnabled(boolean soundMenuEnabled) {
        this.soundMenuEnabled = soundMenuEnabled;
    }
    
    public boolean isProfileSaveLoadEnabled() {
        return profileSaveLoadEnabled;
    }
    
    public void setProfileSaveLoadEnabled(boolean profileSaveLoadEnabled) {
        this.profileSaveLoadEnabled = profileSaveLoadEnabled;
    }
    
    public String getCssFile() {
        return cssFile;
    }
    
    public void setCssFile(String cssFile) {
        this.cssFile = cssFile;
    }
    
    public String getFontUI() {
        return fontUI;
    }
    
    public void setFontUI(String fontUI) {
        this.fontUI = fontUI;
    }
    
    public String getFontMono() {
        return fontMono;
    }
    
    public void setFontMono(String fontMono) {
        this.fontMono = fontMono;
    }
    
    public String getFontText() {
        return fontText;
    }
    
    public void setFontText(String fontText) {
        this.fontText = fontText;
    }
    
    public String getFontGame() {
        return fontGame;
    }
    
    public void setFontGame(String fontGame) {
        this.fontGame = fontGame;
    }
    
    public String getAppIcon() {
        return appIcon;
    }
    
    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }
    
    public boolean isSingleStep() {
        return singleStep;
    }
    
    public void setSingleStep(boolean singleStep) {
        this.singleStep = singleStep;
    }
    
    public double getEngineVersion() {
        return engineVersion;
    }
    
    public void setEngineVersion(double engineVersion) {
        this.engineVersion = engineVersion;
    }
    
    public int getTargetFramerate() {
        return targetFramerate;
    }
    
    public void setTargetFramerate(int targetFramerate) {
        this.targetFramerate = targetFramerate;
    }
}