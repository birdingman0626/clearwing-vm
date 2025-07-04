# Clearwing VM (Fork)

This is a fork of the [Clearwing VM](https://github.com/SwitchGDX/clearwing-vm) project by TheLogicMaster. For the original documentation, core features, and detailed usage instructions, please refer to the upstream repository.

This fork focuses on updating the project to support modern Java versions and streamlining the build process.

## Key Changes in This Fork

*   **Java 17 Runtime Support**: The primary change in this fork is the upgrade of the runtime to support Java 17. This includes compatibility with many modern language features.

*   **Supported Java 17 Features**:
    *   **Module System**: Basic support for `java.lang.module`.
    *   **Collection Factories**: `List.of()`, `Set.of()`, `Map.of()`.
    *   **String Enhancements**: `isBlank()`, `strip()`, `stripLeading()`, `stripTrailing()`, `repeat()`, and `lines()`.
    *   **Sealed Classes**: Basic support via `isSealed()` and `getPermittedSubclasses()`.
    *   **Records**: Basic support via `isRecord()` and `getRecordComponents()`.

*   **Unified JDK 17 Build**: The entire project has been standardized to build with JDK 17, providing backward compatibility for features from JDK 8 through 17. This simplifies the development and build environment.

*   **Updated Dependencies**: The ASM library has been upgraded to version 9.7 to handle JDK 17 bytecode correctly.

For all other information, including the core architecture, limitations, and setup, please consult the [original README.md](https://github.com/SwitchGDX/clearwing-vm/blob/master/README.md).

## Contributors

- Claude
- Gemini