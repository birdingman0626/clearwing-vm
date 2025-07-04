# Supported APIs

This document outlines the Java APIs supported by the Clearwing VM.

## Conformance

The runtime library is not fully conformant with the Java specification. The following sections detail the supported packages and classes.

## java.lang

*   `Object`: Fully supported.
*   `String`: Most methods supported. `formatted()` is supported.
*   `Integer`, `Double`, etc.: Basic support for primitive wrappers.
*   `Thread`: Basic threading support.
*   `System`: `System.out` and `System.err` are supported.

## java.io

*   Basic File I/O is supported.

## java.nio

*   `ByteBuffer`: Supported, including direct buffers.

## java.util

*   `List`, `Set`, `Map`: Supported, including factory methods (`List.of()`, etc.).
*   `Optional`: Fully supported.
*   `stream`: Most stream operations are supported.
*   `regex`: Supported via RegExodus.

## java.util.function

*   All functional interfaces are supported.
