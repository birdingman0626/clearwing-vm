package java.lang.module;

import java.util.Set;
import java.util.HashSet;
import java.util.Optional;

/**
 * Minimal ModuleDescriptor implementation for JDK 9+ compatibility
 */
public class ModuleDescriptor {
    private final String name;
    private final Set<Requires> requires;
    private final Set<Exports> exports;
    private final Set<Opens> opens;
    private final Set<Provides> provides;
    
    private ModuleDescriptor(String name) {
        this.name = name;
        this.requires = new HashSet<>();
        this.exports = new HashSet<>();
        this.opens = new HashSet<>();
        this.provides = new HashSet<>();
    }
    
    public String name() {
        return name;
    }
    
    public Set<Requires> requires() {
        return requires;
    }
    
    public Set<Exports> exports() {
        return exports;
    }
    
    public Set<Opens> opens() {
        return opens;
    }
    
    public Set<Provides> provides() {
        return provides;
    }
    
    public static Builder newModule(String name) {
        return new Builder(name);
    }
    
    public static class Builder {
        private final ModuleDescriptor descriptor;
        
        Builder(String name) {
            this.descriptor = new ModuleDescriptor(name);
        }
        
        public Builder requires(String module) {
            descriptor.requires.add(new Requires(module));
            return this;
        }
        
        public Builder exports(String pkg) {
            descriptor.exports.add(new Exports(pkg));
            return this;
        }
        
        public Builder opens(String pkg) {
            descriptor.opens.add(new Opens(pkg));
            return this;
        }
        
        public Builder provides(String service, String... providers) {
            descriptor.provides.add(new Provides(service, providers));
            return this;
        }
        
        public ModuleDescriptor build() {
            return descriptor;
        }
    }
    
    public static class Requires {
        private final String name;
        
        Requires(String name) {
            this.name = name;
        }
        
        public String name() {
            return name;
        }
    }
    
    public static class Exports {
        private final String source;
        
        Exports(String source) {
            this.source = source;
        }
        
        public String source() {
            return source;
        }
    }
    
    public static class Opens {
        private final String source;
        
        Opens(String source) {
            this.source = source;
        }
        
        public String source() {
            return source;
        }
    }
    
    public static class Provides {
        private final String service;
        private final String[] providers;
        
        Provides(String service, String... providers) {
            this.service = service;
            this.providers = providers;
        }
        
        public String service() {
            return service;
        }
        
        public String[] providers() {
            return providers;
        }
    }
}