package com.almasb.fxgl.assets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enhanced Asset Loading Pipeline for FXGL
 * Provides comprehensive asset management for textures, sounds, fonts, and other game resources
 */
public class AssetLoader {
    
    private static AssetLoader instance;
    private final Map<String, Long> textureCache = new ConcurrentHashMap<>();
    private final Map<String, Long> fontCache = new ConcurrentHashMap<>();
    private final Map<String, byte[]> dataCache = new ConcurrentHashMap<>();
    private boolean initialized = false;
    
    static {
        try {
            initializeNative();
        } catch (Exception e) {
            System.err.println("Failed to initialize asset loader: " + e.getMessage());
        }
    }
    
    private AssetLoader() {
        // Private constructor for singleton
    }
    
    public static AssetLoader getInstance() {
        if (instance == null) {
            instance = new AssetLoader();
        }
        return instance;
    }
    
    /**
     * Initialize the asset loader
     */
    public boolean initialize() {
        if (initialized) {
            return true;
        }
        
        try {
            if (createAssetContext()) {
                initialized = true;
                System.out.println("Asset loader initialized successfully");
                return true;
            }
        } catch (Exception e) {
            System.err.println("Asset loader initialization failed: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Load texture from file path
     */
    public long loadTexture(String path) {
        return loadTexture(path, false);
    }
    
    /**
     * Load texture from file path with optional caching
     */
    public long loadTexture(String path, boolean cache) {
        if (!initialized) return 0;
        
        // Check cache first
        if (cache && textureCache.containsKey(path)) {
            return textureCache.get(path);
        }
        
        try {
            byte[] imageData = loadFileData(path);
            if (imageData != null) {
                long textureId = loadTextureFromMemory(imageData, path);
                
                if (cache && textureId != 0) {
                    textureCache.put(path, textureId);
                }
                
                return textureId;
            }
        } catch (Exception e) {
            System.err.println("Failed to load texture: " + path + " - " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Load texture from InputStream
     */
    public long loadTexture(InputStream inputStream, String name) {
        if (!initialized) return 0;
        
        try {
            byte[] imageData = readStreamData(inputStream);
            if (imageData != null) {
                return loadTextureFromMemory(imageData, name);
            }
        } catch (Exception e) {
            System.err.println("Failed to load texture from stream: " + name + " - " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Load font from file path
     */
    public long loadFont(String path, int size) {
        return loadFont(path, size, false);
    }
    
    /**
     * Load font from file path with optional caching
     */
    public long loadFont(String path, int size, boolean cache) {
        if (!initialized) return 0;
        
        String cacheKey = path + ":" + size;
        
        // Check cache first
        if (cache && fontCache.containsKey(cacheKey)) {
            return fontCache.get(cacheKey);
        }
        
        try {
            byte[] fontData = loadFileData(path);
            if (fontData != null) {
                long fontId = loadFontFromMemory(fontData, size);
                
                if (cache && fontId != 0) {
                    fontCache.put(cacheKey, fontId);
                }
                
                return fontId;
            }
        } catch (Exception e) {
            System.err.println("Failed to load font: " + path + " - " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Load raw file data
     */
    public byte[] loadData(String path) {
        return loadData(path, false);
    }
    
    /**
     * Load raw file data with optional caching
     */
    public byte[] loadData(String path, boolean cache) {
        // Check cache first
        if (cache && dataCache.containsKey(path)) {
            return dataCache.get(path);
        }
        
        try {
            byte[] data = loadFileData(path);
            
            if (cache && data != null) {
                dataCache.put(path, data);
            }
            
            return data;
        } catch (Exception e) {
            System.err.println("Failed to load data: " + path + " - " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Load text file content
     */
    public String loadText(String path) {
        byte[] data = loadData(path);
        if (data != null) {
            return new String(data);
        }
        return null;
    }
    
    /**
     * Check if texture is cached
     */
    public boolean isTextureCached(String path) {
        return textureCache.containsKey(path);
    }
    
    /**
     * Check if font is cached
     */
    public boolean isFontCached(String path, int size) {
        return fontCache.containsKey(path + ":" + size);
    }
    
    /**
     * Check if data is cached
     */
    public boolean isDataCached(String path) {
        return dataCache.containsKey(path);
    }
    
    /**
     * Get texture info (width, height, channels)
     */
    public int[] getTextureInfo(long textureId) {
        if (!initialized) return null;
        return getTextureInfoNative(textureId);
    }
    
    /**
     * Unload texture from memory
     */
    public void unloadTexture(String path) {
        Long textureId = textureCache.remove(path);
        if (textureId != null && initialized) {
            unloadTextureNative(textureId);
        }
    }
    
    /**
     * Unload font from memory
     */
    public void unloadFont(String path, int size) {
        String cacheKey = path + ":" + size;
        Long fontId = fontCache.remove(cacheKey);
        if (fontId != null && initialized) {
            unloadFontNative(fontId);
        }
    }
    
    /**
     * Clear data cache
     */
    public void clearDataCache() {
        dataCache.clear();
    }
    
    /**
     * Clear all caches
     */
    public void clearAllCaches() {
        // Unload all textures
        for (Long textureId : textureCache.values()) {
            if (initialized) {
                unloadTextureNative(textureId);
            }
        }
        textureCache.clear();
        
        // Unload all fonts
        for (Long fontId : fontCache.values()) {
            if (initialized) {
                unloadFontNative(fontId);
            }
        }
        fontCache.clear();
        
        // Clear data cache
        dataCache.clear();
    }
    
    /**
     * Get memory usage statistics
     */
    public AssetStats getStats() {
        return new AssetStats(
            textureCache.size(),
            fontCache.size(),
            dataCache.size(),
            calculateDataCacheSize()
        );
    }
    
    private long calculateDataCacheSize() {
        return dataCache.values().stream()
            .mapToLong(data -> data.length)
            .sum();
    }
    
    /**
     * Check if asset loader is initialized
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Cleanup and shutdown asset loader
     */
    public void shutdown() {
        if (initialized) {
            clearAllCaches();
            destroyAssetContext();
            initialized = false;
        }
    }
    
    // Helper method to read file data
    private byte[] loadFileData(String path) {
        try {
            // Try to load from classpath first
            InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
            if (stream != null) {
                return readStreamData(stream);
            }
            
            // If not found in classpath, use native file loading
            return loadFileDataNative(path);
        } catch (Exception e) {
            return null;
        }
    }
    
    // Helper method to read stream data
    private byte[] readStreamData(InputStream stream) throws IOException {
        try {
            byte[] buffer = new byte[8192];
            java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
            int bytesRead;
            
            while ((bytesRead = stream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            
            return output.toByteArray();
        } finally {
            stream.close();
        }
    }
    
    // Native method declarations
    private static native void initializeNative();
    private static native boolean createAssetContext();
    private static native void destroyAssetContext();
    private static native byte[] loadFileDataNative(String path);
    private static native long loadTextureFromMemory(byte[] data, String name);
    private static native long loadFontFromMemory(byte[] data, int size);
    private static native int[] getTextureInfoNative(long textureId);
    private static native void unloadTextureNative(long textureId);
    private static native void unloadFontNative(long fontId);
    
    /**
     * Asset statistics holder
     */
    public static class AssetStats {
        public final int textureCount;
        public final int fontCount;
        public final int dataCount;
        public final long dataCacheSize;
        
        public AssetStats(int textureCount, int fontCount, int dataCount, long dataCacheSize) {
            this.textureCount = textureCount;
            this.fontCount = fontCount;
            this.dataCount = dataCount;
            this.dataCacheSize = dataCacheSize;
        }
        
        @Override
        public String toString() {
            return String.format(
                "AssetStats{textures=%d, fonts=%d, data=%d, dataSize=%d bytes}",
                textureCount, fontCount, dataCount, dataCacheSize
            );
        }
    }
    
    /*JNI
    #include <cstdlib>
    #include <cstring>
    #include <iostream>
    #include <fstream>
    #include <vector>
    #include <map>
    
    #ifdef _WIN32
    #include <windows.h>
    #elif defined(__APPLE__)
    #include <CoreFoundation/CoreFoundation.h>
    #elif defined(__linux__)
    #include <unistd.h>
    #endif
    
    // Asset management structures
    struct TextureData {
        int width;
        int height;
        int channels;
        unsigned char* pixels;
    };
    
    struct FontData {
        void* fontHandle;
        int size;
    };
    
    static std::map<long, TextureData> g_textures;
    static std::map<long, FontData> g_fonts;
    static long g_nextTextureId = 1;
    static long g_nextFontId = 1;
    
    // Helper function to load file
    static std::vector<unsigned char> loadFile(const char* filename) {
        std::ifstream file(filename, std::ios::binary | std::ios::ate);
        if (!file.is_open()) {
            return {};
        }
        
        std::streamsize size = file.tellg();
        file.seekg(0, std::ios::beg);
        
        std::vector<unsigned char> buffer(size);
        file.read(reinterpret_cast<char*>(buffer.data()), size);
        
        return buffer;
    }
    
    // Simple image format detection and loading (simplified implementation)
    static TextureData loadImageFromMemory(const unsigned char* data, size_t size) {
        TextureData texture = {0};
        
        // Simplified image loading - in real implementation would use stb_image or similar
        // For now, create a dummy texture
        texture.width = 64;
        texture.height = 64;
        texture.channels = 4;
        texture.pixels = new unsigned char[texture.width * texture.height * texture.channels];
        
        // Fill with a pattern
        for (int y = 0; y < texture.height; ++y) {
            for (int x = 0; x < texture.width; ++x) {
                int index = (y * texture.width + x) * texture.channels;
                texture.pixels[index + 0] = (x + y) % 256; // R
                texture.pixels[index + 1] = (x * 2) % 256; // G
                texture.pixels[index + 2] = (y * 2) % 256; // B
                texture.pixels[index + 3] = 255;           // A
            }
        }
        
        return texture;
    }
    
    // Native method implementations
    JNIEXPORT void JNICALL Java_com_almasb_fxgl_assets_AssetLoader_initializeNative(JNIEnv* env, jclass clazz) {
        // Initialize asset loading libraries (stb_image, FreeType, etc.)
    }
    
    JNIEXPORT jboolean JNICALL Java_com_almasb_fxgl_assets_AssetLoader_createAssetContext(JNIEnv* env, jclass clazz) {
        // Initialize asset loading context
        return JNI_TRUE;
    }
    
    JNIEXPORT void JNICALL Java_com_almasb_fxgl_assets_AssetLoader_destroyAssetContext(JNIEnv* env, jclass clazz) {
        // Clean up all loaded assets
        for (auto& pair : g_textures) {
            delete[] pair.second.pixels;
        }
        g_textures.clear();
        
        for (auto& pair : g_fonts) {
            // Clean up font resources
        }
        g_fonts.clear();
    }
    
    JNIEXPORT jbyteArray JNICALL Java_com_almasb_fxgl_assets_AssetLoader_loadFileDataNative(JNIEnv* env, jclass clazz, jstring path) {
        const char* pathStr = env->GetStringUTFChars(path, nullptr);
        
        std::vector<unsigned char> data = loadFile(pathStr);
        
        env->ReleaseStringUTFChars(path, pathStr);
        
        if (data.empty()) {
            return nullptr;
        }
        
        jbyteArray result = env->NewByteArray(data.size());
        env->SetByteArrayRegion(result, 0, data.size(), reinterpret_cast<const jbyte*>(data.data()));
        
        return result;
    }
    
    JNIEXPORT jlong JNICALL Java_com_almasb_fxgl_assets_AssetLoader_loadTextureFromMemory(JNIEnv* env, jclass clazz, jbyteArray data, jstring name) {
        jsize dataSize = env->GetArrayLength(data);
        jbyte* dataPtr = env->GetByteArrayElements(data, nullptr);
        
        TextureData texture = loadImageFromMemory(reinterpret_cast<const unsigned char*>(dataPtr), dataSize);
        
        env->ReleaseByteArrayElements(data, dataPtr, JNI_ABORT);
        
        if (texture.pixels) {
            long textureId = g_nextTextureId++;
            g_textures[textureId] = texture;
            return textureId;
        }
        
        return 0;
    }
    
    JNIEXPORT jlong JNICALL Java_com_almasb_fxgl_assets_AssetLoader_loadFontFromMemory(JNIEnv* env, jclass clazz, jbyteArray data, jint size) {
        jsize dataSize = env->GetArrayLength(data);
        jbyte* dataPtr = env->GetByteArrayElements(data, nullptr);
        
        // Simplified font loading - in real implementation would use FreeType
        FontData font = {0};
        font.size = size;
        
        env->ReleaseByteArrayElements(data, dataPtr, JNI_ABORT);
        
        long fontId = g_nextFontId++;
        g_fonts[fontId] = font;
        return fontId;
    }
    
    JNIEXPORT jintArray JNICALL Java_com_almasb_fxgl_assets_AssetLoader_getTextureInfoNative(JNIEnv* env, jclass clazz, jlong textureId) {
        auto it = g_textures.find(textureId);
        if (it != g_textures.end()) {
            jintArray result = env->NewIntArray(3);
            jint info[3] = {it->second.width, it->second.height, it->second.channels};
            env->SetIntArrayRegion(result, 0, 3, info);
            return result;
        }
        return nullptr;
    }
    
    JNIEXPORT void JNICALL Java_com_almasb_fxgl_assets_AssetLoader_unloadTextureNative(JNIEnv* env, jclass clazz, jlong textureId) {
        auto it = g_textures.find(textureId);
        if (it != g_textures.end()) {
            delete[] it->second.pixels;
            g_textures.erase(it);
        }
    }
    
    JNIEXPORT void JNICALL Java_com_almasb_fxgl_assets_AssetLoader_unloadFontNative(JNIEnv* env, jclass clazz, jlong fontId) {
        auto it = g_fonts.find(fontId);
        if (it != g_fonts.end()) {
            // Clean up font resources
            g_fonts.erase(it);
        }
    }
    */
}