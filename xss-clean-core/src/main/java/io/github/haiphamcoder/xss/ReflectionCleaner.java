package io.github.haiphamcoder.xss;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * The ReflectionCleaner class provides methods for cleaning objects using
 * reflection.
 * It allows cleaning of objects by applying XSS protection policies to their
 * fields.
 */
public class ReflectionCleaner {

    private static final String JAVA_PREFIX = "java.";

    /**
     * Private constructor to prevent instantiation.
     */
    private ReflectionCleaner() {
    }

    /**
     * Cleans the given object by applying the given sanitizer to its fields.
     *
     * @param object    The object to be cleaned.
     * @param sanitizer The sanitizer to be applied to the object.
     */
    public static void clean(Object object, UnaryOperator<String> sanitizer) {
        if (object == null) {
            return;
        }

        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        cleanRecursive(object, sanitizer, visited);
    }

    /**
     * Cleans the given object by applying the given sanitizer to its fields.
     *
     * @param object    The object to be cleaned.
     * @param sanitizer The sanitizer to be applied to the object.
     * @param visited   The set of visited objects.
     */
    private static void cleanRecursive(Object object, UnaryOperator<String> sanitizer, Set<Object> visited) {
        if (object == null || visited.contains(object)) {
            return;
        }

        visited.add(object);

        Class<?> clazz = object.getClass();

        if (clazz.isArray()) {
            cleanArray(object, sanitizer, visited);
            return;
        }

        if (object instanceof Collection<?> collection) {
            cleanCollection(collection, sanitizer, visited);
            return;
        }

        if (object instanceof Map<?, ?> map) {
            cleanMap(map, sanitizer, visited);
            return;
        }

        if (!isJavaType(clazz)) {
            cleanBean(object, clazz, sanitizer, visited);
        }
    }

    /**
     * Checks if the given class is a Java type.
     *
     * @param clazz The class to check.
     * @return True if the class is a Java type, false otherwise.
     */
    private static boolean isJavaType(Class<?> clazz) {
        return clazz.isPrimitive() || clazz.getName().startsWith(JAVA_PREFIX);
    }

    /**
     * Cleans the given array by applying the given sanitizer to its elements.
     *
     * @param arrayObject The array to be cleaned.
     * @param sanitizer   The sanitizer to be applied to the array.
     * @param visited     The set of visited objects.
     */
    private static void cleanArray(Object arrayObject, UnaryOperator<String> sanitizer, Set<Object> visited) {
        int length = Array.getLength(arrayObject);
        for (int i = 0; i < length; i++) {
            Object element = Array.get(arrayObject, i);
            if (element instanceof String string) {
                Array.set(arrayObject, i, sanitizer.apply(string));
            } else {
                cleanRecursive(element, sanitizer, visited);
            }
        }
    }

    /**
     * Cleans the given collection by applying the given sanitizer to its elements.
     *
     * @param collection The collection to be cleaned.
     * @param sanitizer  The sanitizer to be applied to the collection.
     * @param visited    The set of visited objects.
     */
    private static void cleanCollection(Collection<?> collection, UnaryOperator<String> sanitizer,
            Set<Object> visited) {
        List<Object> list = new ArrayList<>();
        for (Object element : collection) {
            if (element instanceof String string) {
                list.add(sanitizer.apply(string));
            } else {
                cleanRecursive(element, sanitizer, visited);
                list.add(element);
            }
        }
        @SuppressWarnings("unchecked")
        Collection<Object> target = (Collection<Object>) collection;
        target.clear();
        target.addAll(list);
    }

    /**
     * Cleans the given map by applying the given sanitizer to its values.
     *
     * @param map       The map to be cleaned.
     * @param sanitizer The sanitizer to be applied to the map.
     * @param visited   The set of visited objects.
     */
    private static void cleanMap(Map<?, ?> map, UnaryOperator<String> sanitizer, Set<Object> visited) {
        @SuppressWarnings("unchecked")
        Map<Object, Object> writable = (Map<Object, Object>) map;
        for (Map.Entry<Object, Object> entry : writable.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String string) {
                entry.setValue(sanitizer.apply(string));
            } else {
                cleanRecursive(value, sanitizer, visited);
            }
        }
    }

    /**
     * Cleans the given bean by applying the given sanitizer to its fields.
     *
     * @param object    The bean to be cleaned.
     * @param clazz     The class of the bean.
     * @param sanitizer The sanitizer to be applied to the bean.
     * @param visited   The set of visited objects.
     */
    private static void cleanBean(Object object, Class<?> clazz, UnaryOperator<String> sanitizer,
            Set<Object> visited) {
        cleanBeanProperties(object, clazz, sanitizer, visited);
        cleanBeanFields(object, clazz, sanitizer, visited);
    }

    /**
     * Cleans the given bean by applying the given sanitizer to its properties.
     *
     * @param object    The bean to be cleaned.
     * @param clazz     The class of the bean.
     * @param sanitizer The sanitizer to be applied to the bean.
     * @param visited   The set of visited objects.
     */
    private static void cleanBeanProperties(Object object, Class<?> clazz, UnaryOperator<String> sanitizer,
            Set<Object> visited) {
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(clazz, Object.class).getPropertyDescriptors()) {
                var getter = pd.getReadMethod();
                var setter = pd.getWriteMethod();
                if (getter == null || setter == null || !Modifier.isPublic(getter.getModifiers())
                        || !Modifier.isPublic(setter.getModifiers())) {
                    continue;
                }
                Object value = getter.invoke(object);
                if (value instanceof String string) {
                    setter.invoke(object, sanitizer.apply(string));
                } else {
                    cleanRecursive(value, sanitizer, visited);
                    setter.invoke(object, value);
                }
            }
        } catch (Exception ignore) {
            // Ignore
        }
    }

    /**
     * Cleans the given bean by applying the given sanitizer to its fields.
     *
     * @param object    The bean to be cleaned.
     * @param clazz     The class of the bean.
     * @param sanitizer The sanitizer to be applied to the bean.
     * @param visited   The set of visited objects.
     */
    private static void cleanBeanFields(Object object, Class<?> clazz, UnaryOperator<String> sanitizer,
            Set<Object> visited) {
        try {
            for (Field field : clazz.getDeclaredFields()) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers)) {
                    continue;
                }
                Object value = field.get(object);
                if (value instanceof String string) {
                    field.set(object, sanitizer.apply(string));
                } else {
                    cleanRecursive(value, sanitizer, visited);
                }
            }
        } catch (Exception ignore) {
            // Ignore
        }
    }

}
