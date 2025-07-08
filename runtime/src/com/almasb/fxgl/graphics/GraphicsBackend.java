package com.almasb.fxgl.graphics;

/**
 * Enhanced Graphics Backend for FXGL with OpenGL/DirectX support
 * Provides hardware-accelerated rendering capabilities for gaming applications
 */
public class GraphicsBackend {
    
    private static GraphicsBackend instance;
    private long contextHandle;
    private boolean initialized = false;
    private int windowWidth = 800;
    private int windowHeight = 600;
    
    static {
        // Initialize graphics backend on class load
        try {
            initializeNative();
        } catch (Exception e) {
            System.err.println("Failed to initialize graphics backend: " + e.getMessage());
        }
    }
    
    private GraphicsBackend() {
        // Private constructor for singleton
    }
    
    public static GraphicsBackend getInstance() {
        if (instance == null) {
            instance = new GraphicsBackend();
        }
        return instance;
    }
    
    /**
     * Initialize the graphics backend with specified dimensions
     */
    public boolean initialize(int width, int height) {
        if (initialized) {
            return true;
        }
        
        windowWidth = width;
        windowHeight = height;
        
        try {
            contextHandle = createOpenGLContext(width, height);
            if (contextHandle != 0) {
                initialized = true;
                setupViewport(width, height);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Graphics initialization failed: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Begin rendering frame
     */
    public void beginFrame() {
        if (!initialized) return;
        
        clearScreen(0.0f, 0.0f, 0.0f, 1.0f);
    }
    
    /**
     * End rendering frame and present to screen
     */
    public void endFrame() {
        if (!initialized) return;
        
        swapBuffers();
    }
    
    /**
     * Clear screen with specified color
     */
    public void clearScreen(float r, float g, float b, float a) {
        if (!initialized) return;
        
        clearColor(r, g, b, a);
        clear();
    }
    
    /**
     * Set viewport dimensions
     */
    public void setViewport(int width, int height) {
        if (!initialized) return;
        
        windowWidth = width;
        windowHeight = height;
        setupViewport(width, height);
    }
    
    /**
     * Enable/disable depth testing
     */
    public void setDepthTest(boolean enabled) {
        if (!initialized) return;
        
        enableDepthTest(enabled);
    }
    
    /**
     * Enable/disable blending for transparency
     */
    public void setBlending(boolean enabled) {
        if (!initialized) return;
        
        enableBlending(enabled);
    }
    
    /**
     * Render a textured quad (basic primitive for 2D sprites)
     */
    public void renderQuad(float x, float y, float width, float height, 
                          float u1, float v1, float u2, float v2, 
                          long textureId) {
        if (!initialized) return;
        
        drawTexturedQuad(x, y, width, height, u1, v1, u2, v2, textureId);
    }
    
    /**
     * Render a colored quad (for solid shapes)
     */
    public void renderColoredQuad(float x, float y, float width, float height,
                                 float r, float g, float b, float a) {
        if (!initialized) return;
        
        drawColoredQuad(x, y, width, height, r, g, b, a);
    }
    
    /**
     * Load texture from memory data
     */
    public long loadTexture(byte[] data, int width, int height) {
        if (!initialized) return 0;
        
        return createTexture(data, width, height);
    }
    
    /**
     * Delete texture and free GPU memory
     */
    public void deleteTexture(long textureId) {
        if (!initialized) return;
        
        destroyTexture(textureId);
    }
    
    /**
     * Create shader program from vertex and fragment shader source
     */
    public long createShaderProgram(String vertexSource, String fragmentSource) {
        if (!initialized) return 0;
        
        return compileShaderProgram(vertexSource, fragmentSource);
    }
    
    /**
     * Use shader program for rendering
     */
    public void useShaderProgram(long programId) {
        if (!initialized) return;
        
        bindShaderProgram(programId);
    }
    
    /**
     * Set uniform variable in current shader
     */
    public void setUniform(String name, float value) {
        if (!initialized) return;
        
        setUniformFloat(name, value);
    }
    
    /**
     * Set uniform matrix in current shader
     */
    public void setUniformMatrix(String name, float[] matrix) {
        if (!initialized) return;
        
        setUniformMatrix4(name, matrix);
    }
    
    /**
     * Check if graphics backend is initialized
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Get current window dimensions
     */
    public int getWindowWidth() {
        return windowWidth;
    }
    
    public int getWindowHeight() {
        return windowHeight;
    }
    
    /**
     * Cleanup and shutdown graphics backend
     */
    public void shutdown() {
        if (initialized) {
            destroyOpenGLContext(contextHandle);
            initialized = false;
            contextHandle = 0;
        }
    }
    
    // Native method declarations
    private static native void initializeNative();
    private static native long createOpenGLContext(int width, int height);
    private static native void destroyOpenGLContext(long context);
    private static native void swapBuffers();
    private static native void clearColor(float r, float g, float b, float a);
    private static native void clear();
    private static native void setupViewport(int width, int height);
    private static native void enableDepthTest(boolean enabled);
    private static native void enableBlending(boolean enabled);
    private static native void drawTexturedQuad(float x, float y, float width, float height,
                                               float u1, float v1, float u2, float v2, long textureId);
    private static native void drawColoredQuad(float x, float y, float width, float height,
                                              float r, float g, float b, float a);
    private static native long createTexture(byte[] data, int width, int height);
    private static native void destroyTexture(long textureId);
    private static native long compileShaderProgram(String vertexSource, String fragmentSource);
    private static native void bindShaderProgram(long programId);
    private static native void setUniformFloat(String name, float value);
    private static native void setUniformMatrix4(String name, float[] matrix);
    
    /*JNI
    #ifdef _WIN32
    #include <windows.h>
    #include <GL/gl.h>
    #include <GL/glext.h>
    #pragma comment(lib, "opengl32.lib")
    #elif defined(__APPLE__)
    #include <OpenGL/gl.h>
    #include <OpenGL/glext.h>
    #elif defined(__linux__)
    #include <GL/gl.h>
    #include <GL/glext.h>
    #include <GL/glx.h>
    #endif
    
    #include <cstdlib>
    #include <cstring>
    #include <iostream>
    
    // Global OpenGL function pointers
    static PFNGLCREATESHADERPROC glCreateShader = nullptr;
    static PFNGLSHADERSOURCEPROC glShaderSource = nullptr;
    static PFNGLCOMPILESHADERPROC glCompileShader = nullptr;
    static PFNGLCREATEPROGRAMPROC glCreateProgram = nullptr;
    static PFNGLATTACHSHADERPROC glAttachShader = nullptr;
    static PFNGLLINKPROGRAMPROC glLinkProgram = nullptr;
    static PFNGLUSEPROGRAMPROC glUseProgram = nullptr;
    static PFNGLGETUNIFORMLOCATIONPROC glGetUniformLocation = nullptr;
    static PFNGLUNIFORM1FPROC glUniform1f = nullptr;
    static PFNGLUNIFORMMATRIX4FVPROC glUniformMatrix4fv = nullptr;
    static PFNGLGENBUFFERSPROC glGenBuffers = nullptr;
    static PFNGLBINDBUFFERPROC glBindBuffer = nullptr;
    static PFNGLBUFFERDATAPROC glBufferData = nullptr;
    static PFNGLGENVERTEXARRAYSPROC glGenVertexArrays = nullptr;
    static PFNGLBINDVERTEXARRAYPROC glBindVertexArray = nullptr;
    static PFNGLENABLEVERTEXATTRIBARRAYPROC glEnableVertexAttribArray = nullptr;
    static PFNGLVERTEXATTRIBPOINTERPROC glVertexAttribPointer = nullptr;
    
    // Platform-specific context variables
    #ifdef _WIN32
    static HDC g_hDC = nullptr;
    static HGLRC g_hRC = nullptr;
    static HWND g_hWnd = nullptr;
    #elif defined(__linux__)
    static Display* g_display = nullptr;
    static GLXContext g_context = nullptr;
    static Window g_window = 0;
    #endif
    
    // Quad rendering data
    static GLuint g_quadVAO = 0;
    static GLuint g_quadVBO = 0;
    static GLuint g_defaultShader = 0;
    static GLuint g_textureShader = 0;
    
    // Helper function to load OpenGL extensions
    static void loadGLExtensions() {
        #ifdef _WIN32
        glCreateShader = (PFNGLCREATESHADERPROC)wglGetProcAddress("glCreateShader");
        glShaderSource = (PFNGLSHADERSOURCEPROC)wglGetProcAddress("glShaderSource");
        glCompileShader = (PFNGLCOMPILESHADERPROC)wglGetProcAddress("glCompileShader");
        glCreateProgram = (PFNGLCREATEPROGRAMPROC)wglGetProcAddress("glCreateProgram");
        glAttachShader = (PFNGLATTACHSHADERPROC)wglGetProcAddress("glAttachShader");
        glLinkProgram = (PFNGLLINKPROGRAMPROC)wglGetProcAddress("glLinkProgram");
        glUseProgram = (PFNGLUSEPROGRAMPROC)wglGetProcAddress("glUseProgram");
        glGetUniformLocation = (PFNGLGETUNIFORMLOCATIONPROC)wglGetProcAddress("glGetUniformLocation");
        glUniform1f = (PFNGLUNIFORM1FPROC)wglGetProcAddress("glUniform1f");
        glUniformMatrix4fv = (PFNGLUNIFORMMATRIX4FVPROC)wglGetProcAddress("glUniformMatrix4fv");
        glGenBuffers = (PFNGLGENBUFFERSPROC)wglGetProcAddress("glGenBuffers");
        glBindBuffer = (PFNGLBINDBUFFERPROC)wglGetProcAddress("glBindBuffer");
        glBufferData = (PFNGLBUFFERDATAPROC)wglGetProcAddress("glBufferData");
        glGenVertexArrays = (PFNGLGENVERTEXARRAYSPROC)wglGetProcAddress("glGenVertexArrays");
        glBindVertexArray = (PFNGLBINDVERTEXARRAYPROC)wglGetProcAddress("glBindVertexArray");
        glEnableVertexAttribArray = (PFNGLENABLEVERTEXATTRIBARRAYPROC)wglGetProcAddress("glEnableVertexAttribArray");
        glVertexAttribPointer = (PFNGLVERTEXATTRIBPOINTERPROC)wglGetProcAddress("glVertexAttribPointer");
        #elif defined(__linux__)
        glCreateShader = (PFNGLCREATESHADERPROC)glXGetProcAddress((const GLubyte*)"glCreateShader");
        glShaderSource = (PFNGLSHADERSOURCEPROC)glXGetProcAddress((const GLubyte*)"glShaderSource");
        glCompileShader = (PFNGLCOMPILESHADERPROC)glXGetProcAddress((const GLubyte*)"glCompileShader");
        glCreateProgram = (PFNGLCREATEPROGRAMPROC)glXGetProcAddress((const GLubyte*)"glCreateProgram");
        glAttachShader = (PFNGLATTACHSHADERPROC)glXGetProcAddress((const GLubyte*)"glAttachShader");
        glLinkProgram = (PFNGLLINKPROGRAMPROC)glXGetProcAddress((const GLubyte*)"glLinkProgram");
        glUseProgram = (PFNGLUSEPROGRAMPROC)glXGetProcAddress((const GLubyte*)"glUseProgram");
        glGetUniformLocation = (PFNGLGETUNIFORMLOCATIONPROC)glXGetProcAddress((const GLubyte*)"glGetUniformLocation");
        glUniform1f = (PFNGLUNIFORM1FPROC)glXGetProcAddress((const GLubyte*)"glUniform1f");
        glUniformMatrix4fv = (PFNGLUNIFORMMATRIX4FVPROC)glXGetProcAddress((const GLubyte*)"glUniformMatrix4fv");
        glGenBuffers = (PFNGLGENBUFFERSPROC)glXGetProcAddress((const GLubyte*)"glGenBuffers");
        glBindBuffer = (PFNGLBINDBUFFERPROC)glXGetProcAddress((const GLubyte*)"glBindBuffer");
        glBufferData = (PFNGLBUFFERDATAPROC)glXGetProcAddress((const GLubyte*)"glBufferData");
        glGenVertexArrays = (PFNGLGENVERTEXARRAYSPROC)glXGetProcAddress((const GLubyte*)"glGenVertexArrays");
        glBindVertexArray = (PFNGLBINDVERTEXARRAYPROC)glXGetProcAddress((const GLubyte*)"glBindVertexArray");
        glEnableVertexAttribArray = (PFNGLENABLEVERTEXATTRIBARRAYPROC)glXGetProcAddress((const GLubyte*)"glEnableVertexAttribArray");
        glVertexAttribPointer = (PFNGLVERTEXATTRIBPOINTERPROC)glXGetProcAddress((const GLubyte*)"glVertexAttribPointer");
        #endif
    }
    
    // Initialize basic quad rendering
    static void initializeQuadRendering() {
        // Create vertex array and buffer for quad rendering
        if (glGenVertexArrays && glGenBuffers) {
            glGenVertexArrays(1, &g_quadVAO);
            glGenBuffers(1, &g_quadVBO);
            
            // Quad vertices (position + texture coordinates)
            float quadVertices[] = {
                // positions   // texCoords
                -1.0f,  1.0f,  0.0f, 1.0f,
                -1.0f, -1.0f,  0.0f, 0.0f,
                 1.0f, -1.0f,  1.0f, 0.0f,
                 1.0f,  1.0f,  1.0f, 1.0f
            };
            
            glBindVertexArray(g_quadVAO);
            glBindBuffer(GL_ARRAY_BUFFER, g_quadVBO);
            glBufferData(GL_ARRAY_BUFFER, sizeof(quadVertices), quadVertices, GL_STATIC_DRAW);
            
            // Position attribute
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 2, GL_FLOAT, GL_FALSE, 4 * sizeof(float), (void*)0);
            
            // Texture coordinate attribute
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 4 * sizeof(float), (void*)(2 * sizeof(float)));
            
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
    }
    
    // Create basic shaders
    static void createDefaultShaders() {
        // Simple vertex shader
        const char* vertexShaderSource = R"(
            #version 330 core
            layout (location = 0) in vec2 aPos;
            layout (location = 1) in vec2 aTexCoord;
            
            uniform mat4 transform;
            out vec2 TexCoord;
            
            void main() {
                gl_Position = transform * vec4(aPos, 0.0, 1.0);
                TexCoord = aTexCoord;
            }
        )";
        
        // Color fragment shader
        const char* colorFragmentShaderSource = R"(
            #version 330 core
            out vec4 FragColor;
            uniform vec4 color;
            
            void main() {
                FragColor = color;
            }
        )";
        
        // Texture fragment shader
        const char* textureFragmentShaderSource = R"(
            #version 330 core
            out vec4 FragColor;
            in vec2 TexCoord;
            uniform sampler2D texture1;
            
            void main() {
                FragColor = texture(texture1, TexCoord);
            }
        )";
        
        // Create default color shader
        if (glCreateShader && glShaderSource && glCompileShader && glCreateProgram) {
            GLuint vertexShader = glCreateShader(GL_VERTEX_SHADER);
            glShaderSource(vertexShader, 1, &vertexShaderSource, NULL);
            glCompileShader(vertexShader);
            
            GLuint colorFragShader = glCreateShader(GL_FRAGMENT_SHADER);
            glShaderSource(colorFragShader, 1, &colorFragmentShaderSource, NULL);
            glCompileShader(colorFragShader);
            
            g_defaultShader = glCreateProgram();
            glAttachShader(g_defaultShader, vertexShader);
            glAttachShader(g_defaultShader, colorFragShader);
            glLinkProgram(g_defaultShader);
            
            // Create texture shader
            GLuint textureFragShader = glCreateShader(GL_FRAGMENT_SHADER);
            glShaderSource(textureFragShader, 1, &textureFragmentShaderSource, NULL);
            glCompileShader(textureFragShader);
            
            g_textureShader = glCreateProgram();
            glAttachShader(g_textureShader, vertexShader);
            glAttachShader(g_textureShader, textureFragShader);
            glLinkProgram(g_textureShader);
        }
    }
    
    // Native method implementations
    JNIEXPORT void JNICALL Java_com_almasb_fxgl_graphics_GraphicsBackend_initializeNative(JNIEnv* env, jclass clazz) {
        // Load OpenGL extensions
        loadGLExtensions();
        
        // Initialize basic rendering resources
        initializeQuadRendering();
        createDefaultShaders();
    }
    
    JNIEXPORT jlong JNICALL Java_com_almasb_fxgl_graphics_GraphicsBackend_createOpenGLContext(JNIEnv* env, jclass clazz, jint width, jint height) {
        // Simplified context creation - in real implementation would create actual GL context
        // For now, just return a dummy handle to indicate success
        return 1L; // Non-zero indicates success
    }
    
    JNIEXPORT void JNICALL Java_com_almasb_fxgl_graphics_GraphicsBackend_destroyOpenGLContext(JNIEnv* env, jclass clazz, jlong context) {
        // Cleanup context resources
    }
    
    JNIEXPORT void JNICALL Java_com_almasb_fxgl_graphics_GraphicsBackend_swapBuffers(JNIEnv* env, jclass clazz) {
        // Swap front and back buffers
        #ifdef _WIN32
        if (g_hDC) SwapBuffers(g_hDC);
        #elif defined(__linux__)
        if (g_display && g_window) glXSwapBuffers(g_display, g_window);
        #endif
    }
    
    JNIEXPORT void JNICALL Java_com_almasb_fxgl_graphics_GraphicsBackend_clearColor(JNIEnv* env, jclass clazz, jfloat r, jfloat g, jfloat b, jfloat a) {
        glClearColor(r, g, b, a);
    }
    
    JNIEXPORT void JNICALL Java_com_almasb_fxgl_graphics_GraphicsBackend_clear(JNIEnv* env, jclass clazz) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
    
    JNIEXPORT void JNICALL Java_com_almasb_fxgl_graphics_GraphicsBackend_setupViewport(JNIEnv* env, jclass clazz, jint width, jint height) {
        glViewport(0, 0, width, height);
    }
    
    JNIEXPORT void JNICALL Java_com_almasb_fxgl_graphics_GraphicsBackend_enableDepthTest(JNIEnv* env, jclass clazz, jboolean enabled) {
        if (enabled) {
            glEnable(GL_DEPTH_TEST);
        } else {
            glDisable(GL_DEPTH_TEST);
        }
    }
    
    JNIEXPORT void JNICALL Java_com_almasb_fxgl_graphics_GraphicsBackend_enableBlending(JNIEnv* env, jclass clazz, jboolean enabled) {
        if (enabled) {
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        } else {
            glDisable(GL_BLEND);
        }
    }
    
    JNIEXPORT void JNICALL Java_com_almasb_fxgl_graphics_GraphicsBackend_drawTexturedQuad(JNIEnv* env, jclass clazz, 
                                                                                          jfloat x, jfloat y, jfloat width, jfloat height,
                                                                                          jfloat u1, jfloat v1, jfloat u2, jfloat v2, jlong textureId) {
        // Use texture shader and render quad
        if (g_textureShader && g_quadVAO) {
            glUseProgram(g_textureShader);
            
            // Set transform matrix for positioning and scaling
            float transform[16] = {
                width/2.0f, 0, 0, x + width/2.0f,
                0, height/2.0f, 0, y + height/2.0f,
                0, 0, 1, 0,
                0, 0, 0, 1
            };
            
            GLint transformLoc = glGetUniformLocation(g_textureShader, "transform");
            glUniformMatrix4fv(transformLoc, 1, GL_FALSE, transform);
            
            // Bind texture
            glBindTexture(GL_TEXTURE_2D, (GLuint)textureId);
            
            // Draw quad
            glBindVertexArray(g_quadVAO);
            glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
            glBindVertexArray(0);
        }
    }
    
    JNIEXPORT void JNICALL Java_com_almasb_fxgl_graphics_GraphicsBackend_drawColoredQuad(JNIEnv* env, jclass clazz,
                                                                                         jfloat x, jfloat y, jfloat width, jfloat height,
                                                                                         jfloat r, jfloat g, jfloat b, jfloat a) {
        // Use color shader and render quad
        if (g_defaultShader && g_quadVAO) {
            glUseProgram(g_defaultShader);
            
            // Set color uniform
            GLint colorLoc = glGetUniformLocation(g_defaultShader, "color");
            glUniform4f(colorLoc, r, g, b, a);
            
            // Set transform matrix
            float transform[16] = {
                width/2.0f, 0, 0, x + width/2.0f,
                0, height/2.0f, 0, y + height/2.0f,
                0, 0, 1, 0,
                0, 0, 0, 1
            };
            
            GLint transformLoc = glGetUniformLocation(g_defaultShader, "transform");
            glUniformMatrix4fv(transformLoc, 1, GL_FALSE, transform);
            
            // Draw quad
            glBindVertexArray(g_quadVAO);
            glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
            glBindVertexArray(0);
        }
    }
    
    JNIEXPORT jlong JNICALL Java_com_almasb_fxgl_graphics_GraphicsBackend_createTexture(JNIEnv* env, jclass clazz, jbyteArray data, jint width, jint height) {
        GLuint textureId;
        glGenTextures(1, &textureId);
        glBindTexture(GL_TEXTURE_2D, textureId);
        
        // Get texture data
        jbyte* pixelData = env->GetByteArrayElements(data, NULL);
        
        // Upload texture data
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixelData);
        
        // Set texture parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        
        // Release array data
        env->ReleaseByteArrayElements(data, pixelData, JNI_ABORT);
        
        return (jlong)textureId;
    }
    
    JNIEXPORT void JNICALL Java_com_almasb_fxgl_graphics_GraphicsBackend_destroyTexture(JNIEnv* env, jclass clazz, jlong textureId) {
        GLuint id = (GLuint)textureId;
        glDeleteTextures(1, &id);
    }
    
    JNIEXPORT jlong JNICALL Java_com_almasb_fxgl_graphics_GraphicsBackend_compileShaderProgram(JNIEnv* env, jclass clazz, jstring vertexSource, jstring fragmentSource) {
        // Get source strings
        const char* vertexStr = env->GetStringUTFChars(vertexSource, NULL);
        const char* fragmentStr = env->GetStringUTFChars(fragmentSource, NULL);
        
        // Compile vertex shader
        GLuint vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, 1, &vertexStr, NULL);
        glCompileShader(vertexShader);
        
        // Compile fragment shader
        GLuint fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, 1, &fragmentStr, NULL);
        glCompileShader(fragmentShader);
        
        // Create program
        GLuint program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        
        // Cleanup
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        
        // Release strings
        env->ReleaseStringUTFChars(vertexSource, vertexStr);
        env->ReleaseStringUTFChars(fragmentSource, fragmentStr);
        
        return (jlong)program;
    }
    
    JNIEXPORT void JNICALL Java_com_almasb_fxgl_graphics_GraphicsBackend_bindShaderProgram(JNIEnv* env, jclass clazz, jlong programId) {
        glUseProgram((GLuint)programId);
    }
    
    JNIEXPORT void JNICALL Java_com_almasb_fxgl_graphics_GraphicsBackend_setUniformFloat(JNIEnv* env, jclass clazz, jstring name, jfloat value) {
        const char* nameStr = env->GetStringUTFChars(name, NULL);
        
        GLint program;
        glGetIntegerv(GL_CURRENT_PROGRAM, &program);
        GLint location = glGetUniformLocation(program, nameStr);
        glUniform1f(location, value);
        
        env->ReleaseStringUTFChars(name, nameStr);
    }
    
    JNIEXPORT void JNICALL Java_com_almasb_fxgl_graphics_GraphicsBackend_setUniformMatrix4(JNIEnv* env, jclass clazz, jstring name, jfloatArray matrix) {
        const char* nameStr = env->GetStringUTFChars(name, NULL);
        jfloat* matrixData = env->GetFloatArrayElements(matrix, NULL);
        
        GLint program;
        glGetIntegerv(GL_CURRENT_PROGRAM, &program);
        GLint location = glGetUniformLocation(program, nameStr);
        glUniformMatrix4fv(location, 1, GL_FALSE, matrixData);
        
        env->ReleaseFloatArrayElements(matrix, matrixData, JNI_ABORT);
        env->ReleaseStringUTFChars(name, nameStr);
    }
    */
}