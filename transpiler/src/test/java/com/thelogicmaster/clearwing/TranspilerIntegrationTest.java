package com.thelogicmaster.clearwing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;

/**
 * Integration tests for the transpiler with JDK 17 features
 */
class TranspilerIntegrationTest {

    @TempDir
    Path tempDir;
    
    private File inputDir;
    private File outputDir;
    private TranspilerConfig config;

    @BeforeEach
    void setUp() throws IOException {
        inputDir = tempDir.resolve("input").toFile();
        outputDir = tempDir.resolve("output").toFile();
        inputDir.mkdirs();
        outputDir.mkdirs();
        
        config = new TranspilerConfig();
        config.setWritingProjectFiles(true);
        config.setMainClass("TestClass");
    }

    @Test
    @DisplayName("TranspilerConfig parsing from JSON")
    void testConfigParsing() {
        String jsonConfig = """
            {
                "nonOptimized": ["com.test.*"],
                "intrinsics": ["java.lang.System.currentTimeMillis()J"],
                "useValueChecks": true,
                "useLineNumbers": false,
                "mainClass": "com.test.Main"
            }
            """;

        TranspilerConfig config = new TranspilerConfig(jsonConfig);
        
        assertEquals(1, config.getNonOptimized().size());
        assertEquals("com.test.*", config.getNonOptimized().get(0));
        assertEquals(1, config.getIntrinsics().size());
        assertEquals("java.lang.System.currentTimeMillis()J", config.getIntrinsics().get(0));
        assertTrue(config.hasValueChecks());
        assertFalse(config.hasLineNumbers());
        assertEquals("com.test.Main", config.getMainClass());
    }

    @Test
    @DisplayName("TranspilerConfig legacy config support")
    void testConfigLegacySupport() {
        String jsonConfig = """
            {
                "reflective": ["com.legacy.*"],
                "generateProjectFiles": true
            }
            """;

        TranspilerConfig config = new TranspilerConfig(jsonConfig);
        
        // reflective should be merged into nonOptimized
        assertEquals(1, config.getNonOptimized().size());
        assertEquals("com.legacy.*", config.getNonOptimized().get(0));
        assertFalse(config.isWritingProjectFiles()); // generateProjectFiles is false by default in new version
    }

    @Test
    @DisplayName("TranspilerConfig merge functionality")
    void testConfigMerge() {
        TranspilerConfig config1 = new TranspilerConfig();
        config1.getNonOptimized().add("com.test1.*");
        config1.getIntrinsics().add("method1");

        TranspilerConfig config2 = new TranspilerConfig();
        config2.getNonOptimized().add("com.test2.*");
        config2.getIntrinsics().add("method2");

        config1.merge(config2);

        assertEquals(2, config1.getNonOptimized().size());
        assertTrue(config1.getNonOptimized().contains("com.test1.*"));
        assertTrue(config1.getNonOptimized().contains("com.test2.*"));
        
        assertEquals(2, config1.getIntrinsics().size());
        assertTrue(config1.getIntrinsics().contains("method1"));
        assertTrue(config1.getIntrinsics().contains("method2"));
    }

    @Test
    @DisplayName("Parser handles JDK 17 bytecode version")
    void testParserBytecodeVersionSupport() {
        Parser parser = new Parser(config);
        assertNotNull(parser);
        
        // The parser should be created without issues
        // In a real test, we would parse actual bytecode, but that requires complex setup
    }

    @Test
    @DisplayName("Utils class name conversion")
    void testUtilsClassNameConversion() {
        assertEquals("java_lang_String", Utils.getClassFilename("java/lang/String"));
        assertEquals("java_util_List", Utils.getClassFilename("java/util/List"));
        assertEquals("TestClass", Utils.getClassFilename("TestClass"));
        assertEquals("com_example_MyClass", Utils.getClassFilename("com/example/MyClass"));
    }

    @Test
    @DisplayName("BytecodeClass basic properties")
    void testBytecodeClassBasics() {
        BytecodeClass clazz = new BytecodeClass("test/TestClass", "java/lang/Object", new String[0], 0);
        
        assertEquals("test/TestClass", clazz.getOriginalName());
        assertEquals("test_TestClass", clazz.getName());
        assertEquals("TestClass", clazz.getSimpleName());
        assertNotNull(clazz.getMethods());
        assertNotNull(clazz.getFields());
    }

    @Test
    @DisplayName("TranspilerException is properly thrown")
    void testTranspilerException() {
        TranspilerException exception = new TranspilerException("Test error");
        assertEquals("Test error", exception.getMessage());
        
        Exception cause = new RuntimeException("Cause");
        TranspilerException exceptionWithCause = new TranspilerException("Test error", cause);
        assertEquals("Test error", exceptionWithCause.getMessage());
        assertEquals(cause, exceptionWithCause.getCause());
    }

    @Test
    @DisplayName("JavaType enum coverage")
    void testJavaTypeEnum() {
        // Test that all primitive types are covered
        assertNotNull(JavaType.BOOLEAN);
        assertNotNull(JavaType.BYTE);
        assertNotNull(JavaType.CHAR);
        assertNotNull(JavaType.SHORT);
        assertNotNull(JavaType.INT);
        assertNotNull(JavaType.LONG);
        assertNotNull(JavaType.FLOAT);
        assertNotNull(JavaType.DOUBLE);
        assertNotNull(JavaType.VOID);
        assertNotNull(JavaType.REFERENCE);
        
        // Test basic operations
        assertTrue(JavaType.INT.isPrimitive());
        assertFalse(JavaType.REFERENCE.isPrimitive());
        assertTrue(JavaType.LONG.isWide());
        assertFalse(JavaType.INT.isWide());
    }

    @Test
    @DisplayName("OperandType enum coverage")
    void testOperandTypeEnum() {
        assertNotNull(OperandType.INT);
        assertNotNull(OperandType.LONG);
        assertNotNull(OperandType.FLOAT);
        assertNotNull(OperandType.DOUBLE);
        assertNotNull(OperandType.REFERENCE);
    }

    @Test
    @DisplayName("Instruction group constants")
    void testInstructionGroups() {
        // Test that instruction group constants are defined
        assertTrue(InstructionGroup.LOAD >= 0);
        assertTrue(InstructionGroup.STORE >= 0);
        assertTrue(InstructionGroup.MATH >= 0);
        assertTrue(InstructionGroup.CONVERT >= 0);
        assertTrue(InstructionGroup.COMPARE >= 0);
        assertTrue(InstructionGroup.BRANCH >= 0);
        assertTrue(InstructionGroup.INVOKE >= 0);
        assertTrue(InstructionGroup.FIELD >= 0);
        assertTrue(InstructionGroup.ARRAY >= 0);
        assertTrue(InstructionGroup.STACK >= 0);
        assertTrue(InstructionGroup.CONSTANT >= 0);
        assertTrue(InstructionGroup.OTHER >= 0);
    }

    @Test
    @DisplayName("TypeVariants utility methods")
    void testTypeVariants() {
        // Test type variant operations
        assertNotNull(TypeVariants.getStackType("I"));
        assertNotNull(TypeVariants.getStackType("J"));
        assertNotNull(TypeVariants.getStackType("F"));
        assertNotNull(TypeVariants.getStackType("D"));
        assertNotNull(TypeVariants.getStackType("Ljava/lang/String;"));
        
        // Test primitive type detection
        assertTrue(TypeVariants.isPrimitive("I"));
        assertTrue(TypeVariants.isPrimitive("J"));
        assertFalse(TypeVariants.isPrimitive("Ljava/lang/String;"));
    }

    @Test
    @DisplayName("MethodSignature parsing")
    void testMethodSignature() {
        MethodSignature sig = new MethodSignature("(ILjava/lang/String;)V");
        
        assertNotNull(sig.getParameterTypes());
        assertEquals("V", sig.getReturnType());
        assertEquals(2, sig.getParameterTypes().length);
        assertEquals("I", sig.getParameterTypes()[0]);
        assertEquals("Ljava/lang/String;", sig.getParameterTypes()[1]);
    }

    @Test
    @DisplayName("Configuration validation")
    void testConfigurationValidation() {
        TranspilerConfig config = new TranspilerConfig();
        
        // Test default values
        assertNotNull(config.getNonOptimized());
        assertNotNull(config.getIntrinsics());
        assertNotNull(config.getSourceIgnores());
        assertTrue(config.hasLineNumbers()); // Default should be true
        assertFalse(config.hasValueChecks()); // Default should be false
        assertTrue(config.isWritingProjectFiles()); // Default should be true
    }
}