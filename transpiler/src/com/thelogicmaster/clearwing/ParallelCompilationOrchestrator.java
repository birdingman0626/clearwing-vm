package com.thelogicmaster.clearwing;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Advanced parallel compilation orchestrator that maximizes throughput
 * and minimizes compilation times for large Clearwing VM projects.
 */
public class ParallelCompilationOrchestrator {
    
    private final TranspilerConfig config;
    private final Path outputPath;
    private final int maxParallelism;
    private final ExecutorService transpilerPool;
    private final ExecutorService compilerPool;
    private final ExecutorService linkerPool;
    private final CompletionService<CompilationTask> completionService;
    
    // Performance monitoring
    private final AtomicLong totalTranspileTime = new AtomicLong(0);
    private final AtomicLong totalCompileTime = new AtomicLong(0);
    private final AtomicLong totalLinkTime = new AtomicLong(0);
    private final AtomicInteger completedTasks = new AtomicInteger(0);
    private final AtomicInteger failedTasks = new AtomicInteger(0);
    
    // Compilation pipeline stages
    public enum CompilationStage {
        TRANSPILE,     // Java bytecode to C++
        COMPILE,       // C++ to object files
        LINK,          // Object files to executables
        OPTIMIZE       // Post-compilation optimizations
    }
    
    public static class CompilationTask {
        public final String className;
        public final CompilationStage stage;
        public final Path inputFile;
        public final Path outputFile;
        public final Set<String> dependencies;
        public final Map<String, Object> metadata;
        public final long submissionTime;
        
        public CompilationTask(String className, CompilationStage stage, 
                              Path inputFile, Path outputFile,
                              Set<String> dependencies, Map<String, Object> metadata) {
            this.className = className;
            this.stage = stage;
            this.inputFile = inputFile;
            this.outputFile = outputFile;
            this.dependencies = new HashSet<>(dependencies);
            this.metadata = new HashMap<>(metadata);
            this.submissionTime = System.currentTimeMillis();
        }
    }
    
    public static class CompilationStats {
        public final long totalDuration;
        public final long transpileTime;
        public final long compileTime;
        public final long linkTime;
        public final int completedTasks;
        public final int failedTasks;
        public final double throughput; // tasks per second
        public final Map<CompilationStage, Long> stageTimings;
        
        public CompilationStats(long totalDuration, long transpileTime, long compileTime,
                               long linkTime, int completedTasks, int failedTasks,
                               Map<CompilationStage, Long> stageTimings) {
            this.totalDuration = totalDuration;
            this.transpileTime = transpileTime;
            this.compileTime = compileTime;
            this.linkTime = linkTime;
            this.completedTasks = completedTasks;
            this.failedTasks = failedTasks;
            this.throughput = completedTasks / (totalDuration / 1000.0);
            this.stageTimings = new HashMap<>(stageTimings);
        }
    }
    
    public static class CompilationResult {
        public final boolean success;
        public final String errorMessage;
        public final CompilationStats stats;
        public final List<Path> generatedFiles;
        public final Map<String, Exception> failures;
        
        public CompilationResult(boolean success, String errorMessage, 
                               CompilationStats stats, List<Path> generatedFiles,
                               Map<String, Exception> failures) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.stats = stats;
            this.generatedFiles = new ArrayList<>(generatedFiles);
            this.failures = new HashMap<>(failures);
        }
    }
    
    public ParallelCompilationOrchestrator(TranspilerConfig config, Path outputPath, int maxParallelism) {
        this.config = config;
        this.outputPath = outputPath;
        this.maxParallelism = maxParallelism;
        
        // Create specialized thread pools for different compilation stages
        this.transpilerPool = Executors.newFixedThreadPool(
            Math.min(maxParallelism, Runtime.getRuntime().availableProcessors()),
            r -> {
                Thread t = new Thread(r, "Transpiler-Thread");
                t.setDaemon(true);
                return t;
            }
        );
        
        this.compilerPool = Executors.newFixedThreadPool(
            maxParallelism,
            r -> {
                Thread t = new Thread(r, "Compiler-Thread");
                t.setDaemon(true);
                return t;
            }
        );
        
        this.linkerPool = Executors.newFixedThreadPool(
            Math.min(4, maxParallelism), // Linking is typically I/O bound
            r -> {
                Thread t = new Thread(r, "Linker-Thread");
                t.setDaemon(true);
                return t;
            }
        );
        
        this.completionService = new ExecutorCompletionService<>(transpilerPool);
    }
    
    /**
     * Compile multiple classes in parallel with intelligent scheduling
     */
    public CompletableFuture<CompilationResult> compileAsync(Collection<BytecodeClass> classes) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            List<Path> generatedFiles = new ArrayList<>();
            Map<String, Exception> failures = new HashMap<>();
            Map<CompilationStage, Long> stageTimings = new HashMap<>();
            
            try {
                // Phase 1: Parallel transpilation (Java bytecode to C++)
                CompletableFuture<Map<String, Path>> transpilePhase = 
                    executeTranspilePhase(classes, stageTimings);
                
                // Phase 2: Parallel compilation (C++ to object files)
                CompletableFuture<Map<String, Path>> compilePhase = 
                    transpilePhase.thenCompose(cppFiles -> 
                        executeCompilePhase(cppFiles, stageTimings));
                
                // Phase 3: Linking (object files to executable)
                CompletableFuture<Path> linkPhase = 
                    compilePhase.thenCompose(objectFiles -> 
                        executeLinkPhase(objectFiles, stageTimings));
                
                // Phase 4: Post-compilation optimizations
                CompletableFuture<List<Path>> optimizePhase = 
                    linkPhase.thenCompose(executable -> 
                        executeOptimizePhase(executable, stageTimings));
                
                // Wait for completion
                generatedFiles.addAll(optimizePhase.join());
                
                long totalDuration = System.currentTimeMillis() - startTime;
                
                CompilationStats stats = new CompilationStats(
                    totalDuration,
                    totalTranspileTime.get(),
                    totalCompileTime.get(), 
                    totalLinkTime.get(),
                    completedTasks.get(),
                    failedTasks.get(),
                    stageTimings
                );
                
                boolean success = failures.isEmpty();
                String errorMessage = success ? null : 
                    "Compilation failed with " + failures.size() + " errors";
                
                return new CompilationResult(success, errorMessage, stats, generatedFiles, failures);
                
            } catch (Exception e) {
                failures.put("ORCHESTRATOR", e);
                
                long totalDuration = System.currentTimeMillis() - startTime;
                CompilationStats stats = new CompilationStats(
                    totalDuration, 0, 0, 0, 0, 1, stageTimings);
                
                return new CompilationResult(false, e.getMessage(), stats, generatedFiles, failures);
            }
        }, transpilerPool);
    }
    
    /**
     * Execute transpilation phase with optimal parallelism
     */
    private CompletableFuture<Map<String, Path>> executeTranspilePhase(
            Collection<BytecodeClass> classes, Map<CompilationStage, Long> stageTimings) {
        
        long phaseStart = System.currentTimeMillis();
        
        // Topologically sort classes by dependencies
        List<BytecodeClass> sortedClasses = topologicalSort(classes);
        
        // Create dependency graph for parallel execution
        Map<String, CompletableFuture<Path>> transpileFutures = new HashMap<>();
        Map<String, Path> cppFiles = new ConcurrentHashMap<>();
        
        for (BytecodeClass clazz : sortedClasses) {
            // Wait for dependencies to complete transpilation
            List<CompletableFuture<Path>> dependencyFutures = clazz.getDependencies().stream()
                .filter(transpileFutures::containsKey)
                .map(transpileFutures::get)
                .collect(Collectors.toList());
            
            CompletableFuture<Path> classFuture = CompletableFuture.allOf(
                dependencyFutures.toArray(new CompletableFuture[0])
            ).thenComposeAsync(v -> {
                return CompletableFuture.supplyAsync(() -> {
                    try {
                        long taskStart = System.currentTimeMillis();
                        Path cppFile = transpileClass(clazz);
                        long taskDuration = System.currentTimeMillis() - taskStart;
                        totalTranspileTime.addAndGet(taskDuration);
                        completedTasks.incrementAndGet();
                        cppFiles.put(clazz.getName(), cppFile);
                        return cppFile;
                    } catch (Exception e) {
                        failedTasks.incrementAndGet();
                        throw new RuntimeException("Failed to transpile " + clazz.getName(), e);
                    }
                }, transpilerPool);
            }, transpilerPool);
            
            transpileFutures.put(clazz.getName(), classFuture);
        }
        
        // Wait for all transpilation to complete
        return CompletableFuture.allOf(
            transpileFutures.values().toArray(new CompletableFuture[0])
        ).thenApply(v -> {
            long phaseDuration = System.currentTimeMillis() - phaseStart;
            stageTimings.put(CompilationStage.TRANSPILE, phaseDuration);
            return cppFiles;
        });
    }
    
    /**
     * Execute compilation phase with intelligent load balancing
     */
    private CompletableFuture<Map<String, Path>> executeCompilePhase(
            Map<String, Path> cppFiles, Map<CompilationStage, Long> stageTimings) {
        
        long phaseStart = System.currentTimeMillis();
        
        // Group files for optimal compiler batch processing
        List<List<Map.Entry<String, Path>>> batches = createOptimalBatches(
            new ArrayList<>(cppFiles.entrySet()), maxParallelism);
        
        Map<String, Path> objectFiles = new ConcurrentHashMap<>();
        
        List<CompletableFuture<Void>> batchFutures = batches.stream()
            .map(batch -> CompletableFuture.runAsync(() -> {
                for (Map.Entry<String, Path> entry : batch) {
                    try {
                        long taskStart = System.currentTimeMillis();
                        Path objectFile = compileFile(entry.getValue(), entry.getKey());
                        long taskDuration = System.currentTimeMillis() - taskStart;
                        totalCompileTime.addAndGet(taskDuration);
                        completedTasks.incrementAndGet();
                        objectFiles.put(entry.getKey(), objectFile);
                    } catch (Exception e) {
                        failedTasks.incrementAndGet();
                        throw new RuntimeException("Failed to compile " + entry.getKey(), e);
                    }
                }
            }, compilerPool))
            .collect(Collectors.toList());
        
        return CompletableFuture.allOf(batchFutures.toArray(new CompletableFuture[0]))
            .thenApply(v -> {
                long phaseDuration = System.currentTimeMillis() - phaseStart;
                stageTimings.put(CompilationStage.COMPILE, phaseDuration);
                return objectFiles;
            });
    }
    
    /**
     * Execute linking phase with optimization
     */
    private CompletableFuture<Path> executeLinkPhase(
            Map<String, Path> objectFiles, Map<CompilationStage, Long> stageTimings) {
        
        long phaseStart = System.currentTimeMillis();
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path executable = linkFiles(new ArrayList<>(objectFiles.values()));
                long phaseDuration = System.currentTimeMillis() - phaseStart;
                totalLinkTime.addAndGet(phaseDuration);
                stageTimings.put(CompilationStage.LINK, phaseDuration);
                return executable;
            } catch (Exception e) {
                throw new RuntimeException("Linking failed", e);
            }
        }, linkerPool);
    }
    
    /**
     * Execute post-compilation optimization phase
     */
    private CompletableFuture<List<Path>> executeOptimizePhase(
            Path executable, Map<CompilationStage, Long> stageTimings) {
        
        long phaseStart = System.currentTimeMillis();
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Path> optimizedFiles = new ArrayList<>();
                optimizedFiles.add(executable);
                
                if (config.useOptimizations()) {
                    // Perform post-link optimizations
                    Path optimizedExecutable = optimizeExecutable(executable);
                    optimizedFiles.add(optimizedExecutable);
                }
                
                long phaseDuration = System.currentTimeMillis() - phaseStart;
                stageTimings.put(CompilationStage.OPTIMIZE, phaseDuration);
                return optimizedFiles;
            } catch (Exception e) {
                throw new RuntimeException("Optimization failed", e);
            }
        }, compilerPool);
    }
    
    /**
     * Generate compilation performance report
     */
    public void generatePerformanceReport(CompilationStats stats, Path reportPath) throws IOException {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(reportPath))) {
            writer.println("# Clearwing VM Compilation Performance Report");
            writer.println("Generated at: " + new Date());
            writer.println();
            
            writer.printf("## Overall Statistics\n");
            writer.printf("- Total Duration: %.2f seconds\n", stats.totalDuration / 1000.0);
            writer.printf("- Completed Tasks: %d\n", stats.completedTasks);
            writer.printf("- Failed Tasks: %d\n", stats.failedTasks);
            writer.printf("- Success Rate: %.1f%%\n", 
                         (stats.completedTasks * 100.0) / (stats.completedTasks + stats.failedTasks));
            writer.printf("- Throughput: %.2f tasks/second\n", stats.throughput);
            writer.println();
            
            writer.printf("## Phase Breakdown\n");
            writer.printf("- Transpilation: %.2f seconds (%.1f%%)\n", 
                         stats.transpileTime / 1000.0,
                         (stats.transpileTime * 100.0) / stats.totalDuration);
            writer.printf("- Compilation: %.2f seconds (%.1f%%)\n",
                         stats.compileTime / 1000.0,
                         (stats.compileTime * 100.0) / stats.totalDuration);
            writer.printf("- Linking: %.2f seconds (%.1f%%)\n",
                         stats.linkTime / 1000.0,
                         (stats.linkTime * 100.0) / stats.totalDuration);
            writer.println();
            
            writer.printf("## Configuration\n");
            writer.printf("- Max Parallelism: %d\n", maxParallelism);
            writer.printf("- Available Processors: %d\n", Runtime.getRuntime().availableProcessors());
            writer.printf("- Debug Symbols: %s\n", config.hasDebugSymbols());
            writer.printf("- Optimizations: %s\n", config.useOptimizations());
            writer.printf("- Line Numbers: %s\n", config.hasLineNumbers());
            writer.println();
            
            writer.printf("## Recommendations\n");
            if (stats.transpileTime > stats.compileTime * 2) {
                writer.println("- Consider increasing transpiler parallelism");
            }
            if (stats.compileTime > stats.transpileTime * 2) {
                writer.println("- Consider using faster compiler or more compile cores");
            }
            if (stats.linkTime > (stats.transpileTime + stats.compileTime) * 0.5) {
                writer.println("- Consider using faster linker (e.g., lld, gold)");
            }
            if (stats.throughput < 1.0) {
                writer.println("- Consider increasing parallelism or using incremental builds");
            }
        }
    }
    
    public void shutdown() {
        transpilerPool.shutdown();
        compilerPool.shutdown();
        linkerPool.shutdown();
        
        try {
            if (!transpilerPool.awaitTermination(30, TimeUnit.SECONDS)) {
                transpilerPool.shutdownNow();
            }
            if (!compilerPool.awaitTermination(30, TimeUnit.SECONDS)) {
                compilerPool.shutdownNow();
            }
            if (!linkerPool.awaitTermination(30, TimeUnit.SECONDS)) {
                linkerPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            transpilerPool.shutdownNow();
            compilerPool.shutdownNow();
            linkerPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    // Private helper methods
    
    private List<BytecodeClass> topologicalSort(Collection<BytecodeClass> classes) {
        Map<String, BytecodeClass> classMap = classes.stream()
            .collect(Collectors.toMap(BytecodeClass::getName, c -> c));
        
        List<BytecodeClass> sorted = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> visiting = new HashSet<>();
        
        for (BytecodeClass clazz : classes) {
            if (!visited.contains(clazz.getName())) {
                topologicalSortHelper(clazz.getName(), classMap, visited, visiting, sorted);
            }
        }
        
        return sorted;
    }
    
    private void topologicalSortHelper(String className, Map<String, BytecodeClass> classMap,
                                      Set<String> visited, Set<String> visiting,
                                      List<BytecodeClass> sorted) {
        if (visiting.contains(className)) {
            return; // Circular dependency - skip for now
        }
        
        if (visited.contains(className)) {
            return;
        }
        
        visiting.add(className);
        
        BytecodeClass clazz = classMap.get(className);
        if (clazz != null) {
            for (String dependency : clazz.getDependencies()) {
                if (classMap.containsKey(dependency)) {
                    topologicalSortHelper(dependency, classMap, visited, visiting, sorted);
                }
            }
            sorted.add(clazz);
        }
        
        visiting.remove(className);
        visited.add(className);
    }
    
    private <T> List<List<T>> createOptimalBatches(List<T> items, int maxBatches) {
        List<List<T>> batches = new ArrayList<>();
        int batchSize = Math.max(1, items.size() / maxBatches);
        
        for (int i = 0; i < items.size(); i += batchSize) {
            int end = Math.min(i + batchSize, items.size());
            batches.add(new ArrayList<>(items.subList(i, end)));
        }
        
        return batches;
    }
    
    private Path transpileClass(BytecodeClass clazz) throws IOException {
        // This would integrate with the existing transpiler
        String className = clazz.getName();
        Path cppFile = outputPath.resolve(className.replace('/', '_') + ".cpp");
        
        // Generate C++ code (placeholder implementation)
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(cppFile))) {
            writer.println("// Generated C++ code for " + className);
            writer.println("#include \"Clearwing.h\"");
            writer.println();
            
            // Add debug info if enabled
            if (config.hasDebugSymbols()) {
                writer.println("// Java class: " + className);
                writer.println("// Generated at: " + new Date());
                writer.println();
            }
            
            // Generate class implementation
            writer.println("// Class implementation would go here");
        }
        
        return cppFile;
    }
    
    private Path compileFile(Path cppFile, String className) throws IOException, InterruptedException {
        Path objectFile = outputPath.resolve(className.replace('/', '_') + ".o");
        
        List<String> command = new ArrayList<>();
        command.add("g++");
        command.add("-c");
        command.add("-std=c++20");
        command.add("-O2");
        command.add("-fPIC");
        
        if (config.hasDebugSymbols()) {
            command.add("-g");
            command.add("-DDEBUG");
        }
        
        command.add("-I" + outputPath.resolve("include"));
        command.add(cppFile.toString());
        command.add("-o");
        command.add(objectFile.toString());
        
        ProcessBuilder pb = new ProcessBuilder(command);
        Process process = pb.start();
        
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Compilation failed for " + className);
        }
        
        return objectFile;
    }
    
    private Path linkFiles(List<Path> objectFiles) throws IOException, InterruptedException {
        Path executable = outputPath.resolve("clearwing_app");
        
        List<String> command = new ArrayList<>();
        command.add("g++");
        command.add("-o");
        command.add(executable.toString());
        
        for (Path objectFile : objectFiles) {
            command.add(objectFile.toString());
        }
        
        ProcessBuilder pb = new ProcessBuilder(command);
        Process process = pb.start();
        
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Linking failed");
        }
        
        return executable;
    }
    
    private Path optimizeExecutable(Path executable) throws IOException, InterruptedException {
        if (!config.useOptimizations()) {
            return executable;
        }
        
        Path optimizedExecutable = outputPath.resolve(executable.getFileName() + "_optimized");
        
        // Run strip to remove debug symbols in release mode
        if (!config.hasDebugSymbols()) {
            List<String> command = Arrays.asList("strip", "-o", 
                                                optimizedExecutable.toString(), 
                                                executable.toString());
            
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();
            
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                // If strip fails, just copy the original
                Files.copy(executable, optimizedExecutable, StandardCopyOption.REPLACE_EXISTING);
            }
        } else {
            Files.copy(executable, optimizedExecutable, StandardCopyOption.REPLACE_EXISTING);
        }
        
        return optimizedExecutable;
    }
}