# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository Overview

Clearwing VM is a Java-to-C++ transpiler that converts Java bytecode into native C++ executables. Originally based on the CodenameOne Parpar VM, it's designed for use with SwitchGDX (Nintendo Switch Homebrew and Xbox UWP backend for LibGDX). The project produces native executables without requiring a JVM at runtime.

## Architecture

The project consists of three main modules:

### 1. Transpiler (`transpiler/`)
- Java 17 transpiler that converts Java bytecode to C++
- Uses ASM library for bytecode analysis and manipulation
- Main class: `com.thelogicmaster.clearwing.Transpiler`
- Supports incremental compilation and class-level optimization
- Generates C++ code with C ABI compatibility

### 2. Runtime (`runtime/`)
- Java 17 runtime library implementation with JDK 8-17 compatibility
- Stripped-down standard library with LibGDX compatibility
- C++ VM implementation in `runtime/res/clearwing/src/`
- Core header: `Clearwing.h` defines VM types and functions

### 3. Annotations (`annotations/`)
- Compile-time annotations for transpiler directives
- Java 17 library for marking weak references and native methods

## Build Commands

### Development Build
```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :transpiler:build
./gradlew :runtime:build
./gradlew :annotations:build

# Create transpiler fat JAR
./gradlew :transpiler:shadowJar
```

### Example Project Build
```bash
# Build and transpile example (produces C++ code)
./gradlew :example:transpile

# Build and run example (compiles C++ and executes)
./gradlew :example:run
```

### Transpiler Command Line Usage
```bash
# Run transpiler directly
java -jar transpiler/build/libs/transpiler-{version}.jar \
  --input build/classes \
  --output build/dist \
  --source src/main/java \
  --config config.json \
  --main com.example.Main \
  --project true
```

## Key Components

### Transpiler Core Classes
- `Transpiler.java`: Main entry point and orchestration
- `Parser.java`: ASM-based bytecode analysis
- `BytecodeClass.java`: Represents Java classes for transpilation
- `TranspilerConfig.java`: Configuration management

### Runtime VM Implementation
- `Clearwing.h`: Core VM types, macros, and functions
- `Object.cpp`: Base object implementation (only non-generated runtime class)
- `Array.hpp`: Java array representation
- Generated C++ files for each Java class

### Configuration Format
JSON configuration files support:
- `nonOptimized`: Class patterns to always include
- `intrinsics`: Methods to treat as native
- `sourceIgnores`: Source file patterns to ignore
- `useValueChecks`: Runtime type/NPE checking
- `useLineNumbers`: Stack trace line numbers
- `mainClass`: Entry point class specification

## Memory Management

### Garbage Collection
- Allocation-triggered GC based on object count and memory thresholds
- Mark-and-sweep algorithm with thread safe-points
- Configurable thresholds:
  - `GC_OBJECT_THRESHOLD`: Objects between collections (default: 1M)
  - `GC_MEM_THRESHOLD`: Memory between collections (default: 100MB)
  - `GC_HEAP_THRESHOLD`: Total memory before frequent collection (default: 2.5GB)

### Exception Handling
- Uses `setjmp`/`longjmp` instead of C++ exceptions
- Variables crossing exception boundaries must be `volatile`
- No RAII guarantees - manual cleanup required

## Native Code Integration

### Inline Native Code (JNIGen Style)
Add native C++ code in Java methods using block comments:
```java
public void nativeMethod() {
    /*
    // C++ code here
    printf("Hello from native code\n");
    */
}
```

### JNI Code Blocks
Use `/*JNI` comments for code outside functions:
```java
/*JNI
#include <stdio.h>
// Global native code
*/
```

### Intrinsic Methods
Mark methods as native in config to replace with custom C++ implementation:
```json
{
  "intrinsics": ["java.lang.Integer.toString()Ljava/lang/String;"]
}
```

## Testing

### JDK 8 Feature Testing
```bash
# Run comprehensive JDK 8 feature validation
./gradlew :jdk8-test:validateJDK8Features
```

### JDK 17 Feature Testing  
```bash
# Run comprehensive JDK 17 feature validation
./gradlew :jdk17-test:validateJDK17Features
```

### Example Project Testing
The example project serves as integration testing for the transpiler and basic functionality verification.

## Dependencies

### Runtime Dependencies
- C++ 20 compiler
- ZLib
- ZZip  
- LibFFI
- CMake (for generated projects)

### Build Dependencies
- JDK 17 (for all development)
- Gradle

## Development Patterns

### Module Structure
- `transpiler/`: Java 17, uses ASM 9.7, JavaParser, and ClassGraph
- `runtime/`: Java 17 with JDK 8-17 feature support, minimal dependencies (RegExodus for regex)
- `annotations/`: Java 17, no dependencies

### Generated Code Structure
- Each Java class generates `.h` and `.cpp` files
- C struct representation with function pointer vtables
- VM initialization through global constructors
- String pooling with `_j` suffix for literals

## JDK Support

The project supports JDK 8-17 features with comprehensive backward compatibility:

## JDK 8 Support

### Core JDK 8 Features (Fully Implemented)
- **Lambda expressions**: Full support for lambda syntax and method references 
- **Stream API**: Complete implementation including filter, map, collect, reduce, etc.
- **Functional interfaces**: All `java.util.function.*` interfaces (Function, Consumer, Predicate, etc.)
- **Optional**: Full Optional API with all methods (of, empty, map, filter, orElse, etc.)
- **Default methods**: Interface default methods with proper vtable support
- **CompletableFuture**: Complete async programming support
- **Collectors**: All standard collectors (toList, toSet, joining, groupingBy, partitioningBy, etc.)
- **Stream operations**: All terminal (anyMatch, count, findFirst) and intermediate (distinct, sorted, limit, skip) operations

### Example Usage
See `jdk8-test/src/test/java/com/thelogicmaster/clearwing/jdk8test/JDK8ValidationTest.java` for comprehensive JDK 8 feature demonstrations.

## JDK 17 Support

The project now supports JDK 17 with backward compatibility for JDK 8-17 features:

### Supported JDK 9+ Features
- **Module system**: Basic `java.lang.module` support with `Module` and `ModuleDescriptor`
- **Collection factory methods**: `List.of()`, `Set.of()`, `Map.of()` implementations
- **Class enhancements**: `getPackageName()`, `getModule()`, `getTypeName()`

### Supported JDK 11+ Features
- **String enhancements**: `isBlank()`, `strip()`, `stripLeading()`, `stripTrailing()`, `repeat()`, `lines()`
- **Collection improvements**: Enhanced `copyOf()` methods

### Supported JDK 14-17+ Features
- **Sealed classes**: Basic `isSealed()` and `getPermittedSubclasses()` support (returns empty)
- **Records**: Basic `isRecord()` and `getRecordComponents()` support with `RecordComponent` class
- **Text blocks**: Compiler support (if using JDK 15+)
- **Pattern matching**: Enhanced `instanceof` (if using JDK 16+)
- **String formatting**: `formatted()` method (JDK 15+)

### ASM Upgrade
- Updated from ASM 7 to ASM 9.7 for JDK 17 bytecode support
- All `Opcodes.ASM*` references updated to `ASM9`

### Example Usage
See `example/src/main/java/com/thelogicmaster/example/JDK17Example.java` for demonstrations of JDK 17 features.

## Limitations

- Limited runtime library (not full Java standard library)
- No conformance with Java specification for regex, string formatting, Unicode
- Debug information as large C++ codebase
- Long compilation times for generated C++ code
- Must use `volatile` for variables crossing exception boundaries
- Some JDK 17 features have simplified implementations (sealed classes, records)