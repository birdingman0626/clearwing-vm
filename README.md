# Clearwing VM - Java to C++ Transpiler

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/SwitchGDX/clearwing-vm)
[![JDK](https://img.shields.io/badge/JDK-17-blue)](https://openjdk.org/projects/jdk/17/)
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)

Clearwing VM is a Java-to-C++ transpiler that converts Java bytecode into native C++ executables without requiring a JVM at runtime. Originally designed for the SwitchGDX project (Nintendo Switch Homebrew and Xbox UWP backend for LibGDX), it enables cross-platform deployment of Java applications as native executables.

This fork enhances the original project with modern Java support and comprehensive JDK 8-17 compatibility.

## Features

### Core Capabilities
- **Java Bytecode Transpilation**: Converts Java/Kotlin bytecode to optimized C++ code
- **Native Executable Generation**: Produces standalone executables without JVM dependency
- **Cross-Platform Support**: Targets multiple platforms including Nintendo Switch, Xbox UWP, and desktop
- **LibGDX Compatibility**: Specifically designed for LibGDX game development
- **Reflection Support**: Automatic metadata generation for reflection operations

### Java Language Support

#### JDK 8 Features (Fully Implemented)
- **Lambda Expressions**: Complete lambda syntax and method references
- **Stream API**: Full implementation with filter, map, collect, reduce, and all standard operations
- **Functional Interfaces**: All `java.util.function.*` interfaces (Function, Consumer, Predicate, etc.)
- **Optional**: Complete Optional API with all methods
- **Default Methods**: Interface default methods with proper vtable support
- **CompletableFuture**: Full async programming support
- **Collectors**: All standard collectors (toList, toSet, joining, groupingBy, etc.)

#### JDK 9-17 Features (Selective Implementation)
- **Collection Factory Methods**: `List.of()`, `Set.of()`, `Map.of()` implementations
- **String Enhancements**: `isBlank()`, `strip()`, `stripLeading()`, `stripTrailing()`, `repeat()`, `lines()`
- **Module System**: Basic `java.lang.module` support
- **Sealed Classes**: Basic `isSealed()` and `getPermittedSubclasses()` support
- **Records**: Basic `isRecord()` and `getRecordComponents()` support
- **Text Blocks**: Compiler support (JDK 15+)
- **Pattern Matching**: Enhanced `instanceof` (JDK 16+)

### Memory Management
- **Garbage Collection**: Allocation-triggered mark-and-sweep GC with configurable thresholds
- **Exception Handling**: Uses `setjmp`/`longjmp` for performance-optimized exception handling
- **Thread Safety**: Thread-safe garbage collection with safe-point synchronization

### Native Code Integration
- **Inline Native Code**: JNIGen-style native C++ code embedding
- **JNI Code Blocks**: Global native code support
- **Intrinsic Methods**: Custom C++ implementations for performance-critical methods

## Quick Start

### Prerequisites
- JDK 17 or later
- C++20 compatible compiler
- CMake 3.16+
- Dependencies: ZLib, ZZip, LibFFI

### Building
```bash
# Build all modules
./gradlew build

# Create transpiler JAR
./gradlew :transpiler:shadowJar

# Run comprehensive tests
./gradlew testAll
```

### Basic Usage
```bash
# Transpile Java project
java -jar transpiler/build/libs/transpiler-3.1.3.jar \
  --input build/classes \
  --output build/dist \
  --source src/main/java \
  --main com.example.Main \
  --project true
```

### Example Project
```bash
# Build and run example
./gradlew :example:run

# Just transpile example
./gradlew :example:transpile
```

## Architecture

The project consists of three main modules:

### 1. Transpiler (`transpiler/`)
- Java 17 transpiler using ASM 9.7 for bytecode analysis
- Supports incremental compilation and class-level optimization
- Generates C++ code with C ABI compatibility
- Main class: `com.thelogicmaster.clearwing.Transpiler`

### 2. Runtime (`runtime/`)
- Java 17 runtime library with JDK 8-17 compatibility
- Stripped-down standard library optimized for LibGDX
- C++ VM implementation in `runtime/res/clearwing/src/`
- Core header: `Clearwing.h` defines VM types and functions

### 3. Annotations (`annotations/`)
- Compile-time annotations for transpiler directives
- Support for weak references and native method marking

## Configuration

### Transpiler Configuration (JSON)
```json
{
  "nonOptimized": ["com.example.DebugClass"],
  "intrinsics": ["java.lang.Integer.toString()Ljava/lang/String;"],
  "sourceIgnores": ["**/*Test.java"],
  "useValueChecks": true,
  "useLineNumbers": true,
  "mainClass": "com.example.Main"
}
```

### Memory Configuration
```cpp
// Configurable GC thresholds
GC_OBJECT_THRESHOLD=1000000    // Objects between collections
GC_MEM_THRESHOLD=100000000     // Memory between collections (100MB)
GC_HEAP_THRESHOLD=2500000000   // Total memory before frequent collection (2.5GB)
```

## Testing

### Comprehensive Test Suite
```bash
# Run all tests
./gradlew testAll

# JDK 8 feature validation
./gradlew :jdk8-test:test

# JDK 17 feature validation
./gradlew :jdk17-test:test

# Integration testing
./gradlew :example:run
```

## Limitations

- **Limited Standard Library**: Not a full Java standard library implementation
- **Specification Compliance**: Limited conformance for regex, string formatting, Unicode
- **Compilation Time**: Long C++ compilation times for large projects
- **Debug Information**: Debug info generates large C++ codebase
- **Exception Variables**: Must use `volatile` for variables crossing exception boundaries

## Advanced Features

### Native Code Integration
```java
public void nativeMethod() {
    /*
    // Inline C++ code
    printf("Hello from native code\n");
    */
}

/*JNI
#include <stdio.h>
// Global native code
*/
```

### Reflection Support
Automatic generation of reflection metadata for:
- Class information and methods
- Field access and modification
- Constructor invocation
- Type checking and casting

## Performance Characteristics

- **Startup Time**: Near-instantaneous (no JVM startup overhead)
- **Memory Usage**: Configurable GC with low memory footprint
- **Execution Speed**: Native C++ performance with optimized bytecode translation
- **File Size**: Compact executables with dead code elimination

## Contributing

1. Ensure JDK 17 is installed
2. Run `./gradlew build` to verify setup
3. Add tests for new features in appropriate test modules
4. Follow existing code patterns and documentation style

## License

MIT License - see [LICENSE](LICENSE) file for details.

## Acknowledgments

- Original Clearwing VM by TheLogicMaster
- SwitchGDX team for Nintendo Switch support
- LibGDX community for cross-platform gaming framework

## Related Projects

- [SwitchGDX](https://github.com/SwitchGDX/switchgdx) - LibGDX backend for Nintendo Switch
- [Original Clearwing VM](https://github.com/SwitchGDX/clearwing-vm) - Upstream repository
- [LibGDX](https://github.com/libgdx/libgdx) - Cross-platform game development framework