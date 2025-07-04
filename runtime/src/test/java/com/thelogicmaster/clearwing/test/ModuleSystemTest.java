package com.thelogicmaster.clearwing.test;

import java.lang.module.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for JDK 9+ Module system
 */
class ModuleSystemTest {

    @Test
    @DisplayName("ModuleDescriptor.Builder creates descriptor with name")
    void testModuleDescriptorBuilder() {
        ModuleDescriptor descriptor = ModuleDescriptor.newModule("test.module")
                .requires("java.base")
                .exports("com.test")
                .opens("com.test.internal")
                .provides("com.test.Service", "com.test.ServiceImpl")
                .build();

        assertNotNull(descriptor);
        assertEquals("test.module", descriptor.name());
        assertNotNull(descriptor.requires());
        assertNotNull(descriptor.exports());
        assertNotNull(descriptor.opens());
        assertNotNull(descriptor.provides());
    }

    @Test
    @DisplayName("ModuleDescriptor.Requires stores module name")
    void testModuleDescriptorRequires() {
        ModuleDescriptor.Requires requires = new ModuleDescriptor.Requires("java.base");
        assertEquals("java.base", requires.name());
    }

    @Test
    @DisplayName("ModuleDescriptor.Exports stores package name")
    void testModuleDescriptorExports() {
        ModuleDescriptor.Exports exports = new ModuleDescriptor.Exports("com.test");
        assertEquals("com.test", exports.source());
    }

    @Test
    @DisplayName("ModuleDescriptor.Opens stores package name")
    void testModuleDescriptorOpens() {
        ModuleDescriptor.Opens opens = new ModuleDescriptor.Opens("com.test.internal");
        assertEquals("com.test.internal", opens.source());
    }

    @Test
    @DisplayName("ModuleDescriptor.Provides stores service and providers")
    void testModuleDescriptorProvides() {
        ModuleDescriptor.Provides provides = new ModuleDescriptor.Provides("com.test.Service", "com.test.ServiceImpl1", "com.test.ServiceImpl2");
        assertEquals("com.test.Service", provides.service());
        String[] providers = provides.providers();
        assertEquals(2, providers.length);
        assertEquals("com.test.ServiceImpl1", providers[0]);
        assertEquals("com.test.ServiceImpl2", providers[1]);
    }

    @Test
    @DisplayName("Module creation and basic properties")
    void testModuleCreation() {
        ModuleDescriptor descriptor = ModuleDescriptor.newModule("test.module").build();
        Module module = new Module("test.module", descriptor, null);

        assertEquals("test.module", module.getName());
        assertSame(descriptor, module.getDescriptor());
        assertNull(module.getClassLoader());
        assertTrue(module.isNamed());
    }

    @Test
    @DisplayName("Module with null name is unnamed")
    void testUnnamedModule() {
        Module module = new Module(null, null, null);
        assertNull(module.getName());
        assertFalse(module.isNamed());
    }

    @Test
    @DisplayName("Module with empty name is unnamed")
    void testEmptyNameModule() {
        Module module = new Module("", null, null);
        assertEquals("", module.getName());
        assertFalse(module.isNamed());
    }

    @Test
    @DisplayName("Module readability checks (simplified implementation)")
    void testModuleReadability() {
        ModuleDescriptor descriptor1 = ModuleDescriptor.newModule("module1").build();
        ModuleDescriptor descriptor2 = ModuleDescriptor.newModule("module2").build();
        Module module1 = new Module("module1", descriptor1, null);
        Module module2 = new Module("module2", descriptor2, null);

        // Our simplified implementation always returns true
        assertTrue(module1.canRead(module2));
        assertTrue(module2.canRead(module1));
    }

    @Test
    @DisplayName("Module export/open checks (simplified implementation)")
    void testModuleExportOpen() {
        ModuleDescriptor descriptor = ModuleDescriptor.newModule("test.module").build();
        Module module = new Module("test.module", descriptor, null);
        Module otherModule = new Module("other.module", null, null);

        // Our simplified implementation always returns true
        assertTrue(module.isExported("com.test"));
        assertTrue(module.isExported("com.test", otherModule));
        assertTrue(module.isOpen("com.test"));
        assertTrue(module.isOpen("com.test", otherModule));
    }

    @Test
    @DisplayName("Module mutation methods do not throw (simplified implementation)")
    void testModuleMutation() {
        ModuleDescriptor descriptor = ModuleDescriptor.newModule("test.module").build();
        Module module = new Module("test.module", descriptor, null);
        Module otherModule = new Module("other.module", null, null);

        // These should not throw exceptions in our simplified implementation
        assertDoesNotThrow(() -> module.addReads(otherModule));
        assertDoesNotThrow(() -> module.addExports("com.test"));
        assertDoesNotThrow(() -> module.addExports("com.test", otherModule));
        assertDoesNotThrow(() -> module.addOpens("com.test"));
        assertDoesNotThrow(() -> module.addOpens("com.test", otherModule));
    }

    @Test
    @DisplayName("Module toString format")
    void testModuleToString() {
        Module namedModule = new Module("test.module", null, null);
        assertEquals("module test.module", namedModule.toString());

        Module unnamedModule = new Module(null, null, null);
        assertEquals("module unnamed", unnamedModule.toString());
    }

    @Test
    @DisplayName("Module annotations (empty in simplified implementation)")
    void testModuleAnnotations() {
        Module module = new Module("test.module", null, null);

        assertNull(module.getAnnotation(Override.class));
        assertEquals(0, module.getAnnotations().length);
        assertEquals(0, module.getDeclaredAnnotations().length);
    }
}