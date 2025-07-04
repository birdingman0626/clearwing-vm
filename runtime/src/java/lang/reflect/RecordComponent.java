package java.lang.reflect;

import java.lang.annotation.Annotation;

/**
 * Minimal RecordComponent implementation for JDK 16+ compatibility
 */
public final class RecordComponent implements AnnotatedElement {
    private final String name;
    private final Class<?> type;
    private final Class<?> declaringRecord;
    private final String signature;
    
    RecordComponent(Class<?> declaringRecord, String name, Class<?> type, String signature) {
        this.declaringRecord = declaringRecord;
        this.name = name;
        this.type = type;
        this.signature = signature;
    }
    
    public String getName() {
        return name;
    }
    
    public Class<?> getType() {
        return type;
    }
    
    public String getGenericSignature() {
        return signature;
    }
    
    public Type getGenericType() {
        return type;
    }
    
    public Class<?> getDeclaringRecord() {
        return declaringRecord;
    }
    
    public Method getAccessor() throws SecurityException {
        try {
            return declaringRecord.getMethod(name);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
    
    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return null;
    }
    
    @Override
    public Annotation[] getAnnotations() {
        return new Annotation[0];
    }
    
    @Override
    public Annotation[] getDeclaredAnnotations() {
        return new Annotation[0];
    }
    
    @Override
    public String toString() {
        return type.getTypeName() + " " + name;
    }
}