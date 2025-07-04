package com.thelogicmaster.clearwing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Enhanced debug information generator for Clearwing VM
 * Generates debug symbols, source maps, and debugging scripts to improve C++ debugging experience
 */
public class DebugInfoGenerator {
    
    private final TranspilerConfig config;
    private final Map<String, SourceMapEntry> sourceMap = new HashMap<>();
    private final Map<String, List<String>> classComments = new HashMap<>();
    private final Map<String, String> javaToNativeNameMap = new HashMap<>();
    private final Path outputPath;
    
    public static class SourceMapEntry {
        public final String javaClass;
        public final String javaMethod;
        public final int javaLine;
        public final String cppFile;
        public final int cppLine;
        public final String cppFunction;
        
        public SourceMapEntry(String javaClass, String javaMethod, int javaLine, 
                             String cppFile, int cppLine, String cppFunction) {
            this.javaClass = javaClass;
            this.javaMethod = javaMethod;
            this.javaLine = javaLine;
            this.cppFile = cppFile;
            this.cppLine = cppLine;
            this.cppFunction = cppFunction;
        }
    }
    
    public DebugInfoGenerator(TranspilerConfig config, Path outputPath) {
        this.config = config;
        this.outputPath = outputPath;
    }
    
    /**
     * Add a source mapping entry linking Java source to C++ output
     */
    public void addSourceMapping(String javaClass, String javaMethod, int javaLine,
                                String cppFile, int cppLine, String cppFunction) {
        if (config.hasSourceMapping()) {
            String key = javaClass + ":" + javaMethod + ":" + javaLine;
            sourceMap.put(key, new SourceMapEntry(javaClass, javaMethod, javaLine, 
                                                 cppFile, cppLine, cppFunction));
        }
    }
    
    /**
     * Add comments for a class to be included in generated C++ code
     */
    public void addClassComments(String className, List<String> comments) {
        if (config.hasInlineComments()) {
            classComments.put(className, new ArrayList<>(comments));
        }
    }
    
    /**
     * Map Java name to native C++ name
     */
    public void mapJavaToNativeName(String javaName, String nativeName) {
        if (config.shouldPreserveJavaNames()) {
            javaToNativeNameMap.put(javaName, nativeName);
        }
    }
    
    /**
     * Generate comprehensive debug information
     */
    public void generateDebugInfo() throws IOException {
        if (config.hasDebugSymbols()) {
            generateDebugSymbols();
        }
        
        if (config.hasSourceMapping()) {
            generateSourceMap();
        }
        
        if (config.shouldGenerateDebugScript()) {
            generateGDBScript();
            generateLLDBScript();
        }
        
        generateDebugHeader();
    }
    
    /**
     * Generate debug symbols configuration
     */
    private void generateDebugSymbols() throws IOException {
        Path debugFile = outputPath.resolve("debug_config.cmake");
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(debugFile.toFile()))) {
            writer.println("# Enhanced Debug Configuration for Clearwing VM");
            writer.println("# Generated automatically - do not edit manually");
            writer.println();
            
            // Configure debug flags based on format
            switch (config.getDebugOutputFormat().toLowerCase()) {
                case "dwarf":
                    writer.println("set(CMAKE_CXX_FLAGS_DEBUG \"-g -gdwarf-4 -O0 -DDEBUG\")");
                    writer.println("set(CMAKE_C_FLAGS_DEBUG \"-g -gdwarf-4 -O0 -DDEBUG\")");
                    break;
                case "codeview":
                    writer.println("set(CMAKE_CXX_FLAGS_DEBUG \"-g -gcodeview -O0 -DDEBUG\")");
                    writer.println("set(CMAKE_C_FLAGS_DEBUG \"-g -gcodeview -O0 -DDEBUG\")");
                    break;
                case "stabs":
                    writer.println("set(CMAKE_CXX_FLAGS_DEBUG \"-g -gstabs+ -O0 -DDEBUG\")");
                    writer.println("set(CMAKE_C_FLAGS_DEBUG \"-g -gstabs+ -O0 -DDEBUG\")");
                    break;
                default:
                    writer.println("set(CMAKE_CXX_FLAGS_DEBUG \"-g -O0 -DDEBUG\")");
                    writer.println("set(CMAKE_C_FLAGS_DEBUG \"-g -O0 -DDEBUG\")");
            }
            
            writer.println();
            writer.println("# Preserve frame pointers for better stack traces");
            writer.println("set(CMAKE_CXX_FLAGS_DEBUG \"${CMAKE_CXX_FLAGS_DEBUG} -fno-omit-frame-pointer\")");
            writer.println("set(CMAKE_C_FLAGS_DEBUG \"${CMAKE_C_FLAGS_DEBUG} -fno-omit-frame-pointer\")");
            
            writer.println();
            writer.println("# Enable additional debug information");
            writer.println("set(CMAKE_CXX_FLAGS_DEBUG \"${CMAKE_CXX_FLAGS_DEBUG} -fno-eliminate-unused-debug-types\")");
            writer.println("set(CMAKE_C_FLAGS_DEBUG \"${CMAKE_C_FLAGS_DEBUG} -fno-eliminate-unused-debug-types\")");
            
            writer.println();
            writer.println("# Link with debug runtime");
            writer.println("set(CMAKE_EXE_LINKER_FLAGS_DEBUG \"-g\")");
            
            if (config.hasLineNumbers()) {
                writer.println();
                writer.println("# Enable line number information");
                writer.println("add_definitions(-DCLEARWING_LINE_NUMBERS=1)");
            }
            
            writer.println();
            writer.println("# Set debug build type");
            writer.println("if(NOT CMAKE_BUILD_TYPE)");
            writer.println("    set(CMAKE_BUILD_TYPE Debug)");
            writer.println("endif()");
        }
    }
    
    /**
     * Generate source map file for debugging
     */
    private void generateSourceMap() throws IOException {
        Path sourceMapFile = outputPath.resolve("clearwing_source_map.json");
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(sourceMapFile.toFile()))) {
            writer.println("{");
            writer.println("  \"version\": 1,");
            writer.println("  \"generator\": \"Clearwing VM Transpiler\",");
            writer.println("  \"mappings\": [");
            
            boolean first = true;
            for (SourceMapEntry entry : sourceMap.values()) {
                if (!first) {
                    writer.println(",");
                }
                writer.printf("    {%n");
                writer.printf("      \"javaClass\": \"%s\",%n", escapeJson(entry.javaClass));
                writer.printf("      \"javaMethod\": \"%s\",%n", escapeJson(entry.javaMethod));
                writer.printf("      \"javaLine\": %d,%n", entry.javaLine);
                writer.printf("      \"cppFile\": \"%s\",%n", escapeJson(entry.cppFile));
                writer.printf("      \"cppLine\": %d,%n", entry.cppLine);
                writer.printf("      \"cppFunction\": \"%s\"%n", escapeJson(entry.cppFunction));
                writer.print("    }");
                first = false;
            }
            
            writer.println();
            writer.println("  ],");
            writer.println("  \"nameMapping\": {");
            
            first = true;
            for (Map.Entry<String, String> mapping : javaToNativeNameMap.entrySet()) {
                if (!first) {
                    writer.println(",");
                }
                writer.printf("    \"%s\": \"%s\"", 
                             escapeJson(mapping.getKey()), 
                             escapeJson(mapping.getValue()));
                first = false;
            }
            
            writer.println();
            writer.println("  }");
            writer.println("}");
        }
    }
    
    /**
     * Generate GDB debugging script
     */
    private void generateGDBScript() throws IOException {
        Path gdbFile = outputPath.resolve("clearwing_debug.gdb");
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(gdbFile.toFile()))) {
            writer.println("# GDB debugging script for Clearwing VM");
            writer.println("# Usage: gdb -x clearwing_debug.gdb <executable>");
            writer.println();
            
            writer.println("# Set up better C++ debugging");
            writer.println("set print pretty on");
            writer.println("set print object on");
            writer.println("set print static-members on");
            writer.println("set print vtbl on");
            writer.println("set print demangle on");
            writer.println("set demangle-style gnu-v3");
            writer.println();
            
            writer.println("# Enable source-level debugging");
            writer.println("set substitute-path java/ src/main/java/");
            writer.println();
            
            writer.println("# Java exception handling helpers");
            writer.println("define java-bt");
            writer.println("  set $exc = (java_lang_Throwable*) $arg0");
            writer.println("  if $exc");
            writer.println("    printf \"Java Exception: %s\\n\", $exc->__class->name");
            writer.println("    if $exc->stackTrace");
            writer.println("      printf \"Stack trace available\\n\"");
            writer.println("    end");
            writer.println("  end");
            writer.println("end");
            writer.println();
            
            writer.println("# Helper to print Java strings");
            writer.println("define java-string");
            writer.println("  set $str = (java_lang_String*) $arg0");
            writer.println("  if $str && $str->data");
            writer.println("    printf \"Java String: \\\"\"");
            writer.println("    set $i = 0");
            writer.println("    while $i < $str->length");
            writer.println("      printf \"%c\", (char) $str->data->data[$i]");
            writer.println("      set $i = $i + 1");
            writer.println("    end");
            writer.println("    printf \"\\\"\\n\"");
            writer.println("  else");
            writer.println("    printf \"null\\n\"");
            writer.println("  end");
            writer.println("end");
            writer.println();
            
            writer.println("# Helper to inspect Java objects");
            writer.println("define java-object");
            writer.println("  set $obj = (java_lang_Object*) $arg0");
            writer.println("  if $obj");
            writer.println("    printf \"Java Object: %s@%p\\n\", $obj->__class->name, $obj");
            writer.println("    printf \"Class: %s\\n\", $obj->__class->name");
            writer.println("    printf \"Hash: %d\\n\", $obj->__id");
            writer.println("  else");
            writer.println("    printf \"null\\n\"");
            writer.println("  end");
            writer.println("end");
            writer.println();
            
            // Add breakpoints for common Java runtime functions
            writer.println("# Common breakpoints");
            writer.println("# break java_lang_Object___init___");
            writer.println("# break java_lang_Throwable___init___");
            writer.println("# break java_lang_System_gc__");
            writer.println();
            
            writer.println("echo \\nClearwing VM GDB script loaded.\\n");
            writer.println("echo Use 'java-bt <exception>' to print Java exception info\\n");
            writer.println("echo Use 'java-string <string>' to print Java string content\\n");
            writer.println("echo Use 'java-object <object>' to inspect Java object\\n");
        }
    }
    
    /**
     * Generate LLDB debugging script
     */
    private void generateLLDBScript() throws IOException {
        Path lldbFile = outputPath.resolve("clearwing_debug.lldb");
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(lldbFile.toFile()))) {
            writer.println("# LLDB debugging script for Clearwing VM");
            writer.println("# Usage: lldb -s clearwing_debug.lldb <executable>");
            writer.println();
            
            writer.println("# Set up better C++ debugging");
            writer.println("settings set target.prefer-dynamic-value run-target");
            writer.println("settings set target.enable-synthetic-value true");
            writer.println("settings set target.show-summary-children true");
            writer.println();
            
            writer.println("# Java exception helpers");
            writer.println("command script add -f clearwing_debug.java_bt java_bt");
            writer.println("command script add -f clearwing_debug.java_string java_string");
            writer.println("command script add -f clearwing_debug.java_object java_object");
            writer.println();
            
            writer.println("# Python helper functions");
            writer.println("script");
            writer.println("def java_bt(debugger, command, result, internal_dict):");
            writer.println("    target = debugger.GetSelectedTarget()");
            writer.println("    process = target.GetProcess()");
            writer.println("    thread = process.GetSelectedThread()");
            writer.println("    frame = thread.GetSelectedFrame()");
            writer.println("    ");
            writer.println("    # Get exception object from argument");
            writer.println("    exc = frame.FindVariable(command.strip())");
            writer.println("    if exc.IsValid():");
            writer.println("        class_name = exc.GetChildMemberWithName('__class').GetChildMemberWithName('name')");
            writer.println("        if class_name.IsValid():");
            writer.println("            print('Java Exception:', class_name.GetSummary())");
            writer.println("        stack_trace = exc.GetChildMemberWithName('stackTrace')");
            writer.println("        if stack_trace.IsValid():");
            writer.println("            print('Stack trace available')");
            writer.println("    else:");
            writer.println("        print('Invalid exception object')");
            writer.println();
            
            writer.println("def java_string(debugger, command, result, internal_dict):");
            writer.println("    target = debugger.GetSelectedTarget()");
            writer.println("    process = target.GetProcess()");
            writer.println("    thread = process.GetSelectedThread()");
            writer.println("    frame = thread.GetSelectedFrame()");
            writer.println("    ");
            writer.println("    # Get string object from argument");
            writer.println("    str_obj = frame.FindVariable(command.strip())");
            writer.println("    if str_obj.IsValid():");
            writer.println("        data = str_obj.GetChildMemberWithName('data')");
            writer.println("        length = str_obj.GetChildMemberWithName('length')");
            writer.println("        if data.IsValid() and length.IsValid():");
            writer.println("            # Read string content (simplified)");
            writer.println("            print('Java String: (length=' + str(length.GetValueAsUnsigned()) + ')')");
            writer.println("    else:");
            writer.println("        print('null')");
            writer.println();
            
            writer.println("def java_object(debugger, command, result, internal_dict):");
            writer.println("    target = debugger.GetSelectedTarget()");
            writer.println("    process = target.GetProcess()");
            writer.println("    thread = process.GetSelectedThread()");
            writer.println("    frame = thread.GetSelectedFrame()");
            writer.println("    ");
            writer.println("    # Get object from argument");
            writer.println("    obj = frame.FindVariable(command.strip())");
            writer.println("    if obj.IsValid():");
            writer.println("        class_info = obj.GetChildMemberWithName('__class')");
            writer.println("        if class_info.IsValid():");
            writer.println("            class_name = class_info.GetChildMemberWithName('name')");
            writer.println("            if class_name.IsValid():");
            writer.println("                print('Java Object:', class_name.GetSummary(), '@', hex(obj.GetValueAsUnsigned()))");
            writer.println("        obj_id = obj.GetChildMemberWithName('__id')");
            writer.println("        if obj_id.IsValid():");
            writer.println("            print('Hash:', obj_id.GetValueAsUnsigned())");
            writer.println("    else:");
            writer.println("        print('null')");
            writer.println("script");
            writer.println();
            
            writer.println("echo \"\\nClearwing VM LLDB script loaded.\\n\"");
            writer.println("echo \"Use 'java_bt <exception>' to print Java exception info\\n\"");
            writer.println("echo \"Use 'java_string <string>' to print Java string content\\n\"");
            writer.println("echo \"Use 'java_object <object>' to inspect Java object\\n\"");
        }
    }
    
    /**
     * Generate debug header with useful macros and type definitions
     */
    private void generateDebugHeader() throws IOException {
        Path debugHeader = outputPath.resolve("clearwing_debug.h");
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(debugHeader.toFile()))) {
            writer.println("// Enhanced debug support for Clearwing VM");
            writer.println("// Generated automatically - do not edit manually");
            writer.println();
            writer.println("#ifndef CLEARWING_DEBUG_H");
            writer.println("#define CLEARWING_DEBUG_H");
            writer.println();
            
            writer.println("#ifdef __cplusplus");
            writer.println("extern \"C\" {");
            writer.println("#endif");
            writer.println();
            
            if (config.hasDebugSymbols()) {
                writer.println("// Debug symbols enabled");
                writer.println("#define CLEARWING_DEBUG_SYMBOLS 1");
                writer.println();
                
                writer.println("// Debug macros for better C++ debugging");
                writer.println("#ifdef DEBUG");
                writer.println("#include <stdio.h>");
                writer.println("#include <string.h>");
                writer.println();
                
                writer.println("// Java object inspection macros");
                writer.println("#define JAVA_OBJECT_INFO(obj) \\");
                writer.println("    do { \\");
                writer.println("        if (obj) { \\");
                writer.println("            printf(\"Java Object: %s@%p\\n\", (obj)->__class->name, (obj)); \\");
                writer.println("        } else { \\");
                writer.println("            printf(\"Java Object: null\\n\"); \\");
                writer.println("        } \\");
                writer.println("    } while(0)");
                writer.println();
                
                writer.println("// Java string debugging");
                writer.println("#define JAVA_STRING_CONTENT(str) \\");
                writer.println("    do { \\");
                writer.println("        if (str && (str)->data) { \\");
                writer.println("            printf(\"Java String: \\\"\"); \\");
                writer.println("            for (int i = 0; i < (str)->length; i++) { \\");
                writer.println("                printf(\"%c\", (char)(str)->data->data[i]); \\");
                writer.println("            } \\");
                writer.println("            printf(\"\\\"\\n\"); \\");
                writer.println("        } else { \\");
                writer.println("            printf(\"Java String: null\\n\"); \\");
                writer.println("        } \\");
                writer.println("    } while(0)");
                writer.println();
                
                writer.println("// Method entry/exit tracing");
                writer.println("#define JAVA_METHOD_ENTER(class_name, method_name) \\");
                writer.println("    printf(\"[TRACE] Entering %s.%s\\n\", class_name, method_name)");
                writer.println();
                writer.println("#define JAVA_METHOD_EXIT(class_name, method_name) \\");
                writer.println("    printf(\"[TRACE] Exiting %s.%s\\n\", class_name, method_name)");
                writer.println();
                
                writer.println("#else");
                writer.println("// Release mode - no debug output");
                writer.println("#define JAVA_OBJECT_INFO(obj) do {} while(0)");
                writer.println("#define JAVA_STRING_CONTENT(str) do {} while(0)");
                writer.println("#define JAVA_METHOD_ENTER(class_name, method_name) do {} while(0)");
                writer.println("#define JAVA_METHOD_EXIT(class_name, method_name) do {} while(0)");
                writer.println("#endif // DEBUG");
                writer.println();
            }
            
            if (config.hasLineNumbers()) {
                writer.println("// Line number tracking");
                writer.println("#define CLEARWING_LINE_NUMBERS 1");
                writer.println();
                writer.println("// Current source location tracking");
                writer.println("typedef struct {");
                writer.println("    const char* java_class;");
                writer.println("    const char* java_method;");
                writer.println("    int java_line;");
                writer.println("    const char* cpp_file;");
                writer.println("    int cpp_line;");
                writer.println("} SourceLocation;");
                writer.println();
                
                writer.println("// Set current source location for debugging");
                writer.println("extern void clearwing_set_source_location(const SourceLocation* location);");
                writer.println("extern const SourceLocation* clearwing_get_source_location(void);");
                writer.println();
                
                writer.println("// Macro to set source location");
                writer.println("#define SET_SOURCE_LOCATION(java_cls, java_mth, java_ln) \\");
                writer.println("    do { \\");
                writer.println("        static const SourceLocation loc = { \\");
                writer.println("            .java_class = java_cls, \\");
                writer.println("            .java_method = java_mth, \\");
                writer.println("            .java_line = java_ln, \\");
                writer.println("            .cpp_file = __FILE__, \\");
                writer.println("            .cpp_line = __LINE__ \\");
                writer.println("        }; \\");
                writer.println("        clearwing_set_source_location(&loc); \\");
                writer.println("    } while(0)");
                writer.println();
            }
            
            // Add function signature annotations if enabled
            if (config.hasFunctionAnnotations()) {
                writer.println("// Function annotations for better debugging");
                writer.println("#define JAVA_METHOD_SIGNATURE(signature) \\");
                writer.println("    __attribute__((annotate(\"java_method:\" signature)))");
                writer.println();
                writer.println("#define JAVA_CLASS_SIGNATURE(signature) \\");
                writer.println("    __attribute__((annotate(\"java_class:\" signature)))");
                writer.println();
            } else {
                writer.println("#define JAVA_METHOD_SIGNATURE(signature)");
                writer.println("#define JAVA_CLASS_SIGNATURE(signature)");
                writer.println();
            }
            
            writer.println("// Exception handling helpers");
            writer.println("extern void clearwing_print_java_stack_trace(void* exception);");
            writer.println("extern void clearwing_debug_exception(void* exception);");
            writer.println();
            
            writer.println("#ifdef __cplusplus");
            writer.println("}");
            writer.println("#endif");
            writer.println();
            writer.println("#endif // CLEARWING_DEBUG_H");
        }
    }
    
    /**
     * Generate inline comments for C++ code from Java source
     */
    public String generateInlineComment(String javaClass, String javaMethod, int javaLine) {
        if (!config.hasInlineComments()) {
            return "";
        }
        
        StringBuilder comment = new StringBuilder();
        comment.append("// Java: ").append(javaClass);
        if (javaMethod != null && !javaMethod.isEmpty()) {
            comment.append(".").append(javaMethod);
        }
        if (javaLine > 0) {
            comment.append(":").append(javaLine);
        }
        
        return comment.toString();
    }
    
    /**
     * Generate function annotation for method signatures
     */
    public String generateFunctionAnnotation(String javaSignature) {
        if (!config.hasFunctionAnnotations()) {
            return "";
        }
        
        return "JAVA_METHOD_SIGNATURE(\"" + escapeString(javaSignature) + "\") ";
    }
    
    /**
     * Generate class annotation for class definitions
     */
    public String generateClassAnnotation(String javaClassName) {
        if (!config.hasFunctionAnnotations()) {
            return "";
        }
        
        return "JAVA_CLASS_SIGNATURE(\"" + escapeString(javaClassName) + "\") ";
    }
    
    /**
     * Generate source location tracking code
     */
    public String generateSourceLocationCode(String javaClass, String javaMethod, int javaLine) {
        if (!config.hasLineNumbers()) {
            return "";
        }
        
        return "SET_SOURCE_LOCATION(\"" + escapeString(javaClass) + "\", \"" + 
               escapeString(javaMethod) + "\", " + javaLine + ");";
    }
    
    private String escapeString(String str) {
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }
    
    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}