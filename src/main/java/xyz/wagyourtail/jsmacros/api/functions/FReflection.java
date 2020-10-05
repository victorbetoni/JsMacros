package xyz.wagyourtail.jsmacros.api.functions;

import org.apache.commons.lang3.StringUtils;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.api.Functions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 
 * Functions for getting and using raw java classes, methods and functions.
 * 
 * An instance of this class is passed to scripts as the {@code reflection} variable.
 * 
 * @since 1.2.3
 * 
 * @author Wagyourtail
 *
 */
public class FReflection extends Functions {
    private Map<String, Class<?>> loadedClasses = new HashMap<>();
    
    public FReflection(String libName) {
        super(libName);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @see FReflection#getClass(String, String)
     * 
     * @since 1.2.3
     * 
     * @param name
     * @return
     * @throws ClassNotFoundException
     */
    public Class<?> getClass(String name) throws ClassNotFoundException {
        try {
            switch (name) {
                case "boolean":
                    return boolean.class;
                case "byte":
                    return byte.class;
                case "short":
                    return short.class;
                case "int":
                    return int.class;
                case "long":
                    return long.class;
                case "float":
                    return float.class;
                case "double":
                    return double.class;
                case "char":
                    return char.class;
                case "void":
                    return void.class;
                default:
                    return Class.forName(name);
            }
        } catch (ClassNotFoundException e) {
            if (loadedClasses.containsKey(name)) return loadedClasses.get(name);
            else throw e;
        }
    }
    
    /**
     * 
     * Use this to specify a class with intermediary and yarn names of classes for cleaner code.
     * 
     * @since 1.2.3
     * 
     * @param name first try
     * @param name2 second try
     * @return a {@link java.lang.Class Class} reference.
     * @throws ClassNotFoundException
     */
    public Class<?> getClass(String name, String name2) throws ClassNotFoundException {
        try {
            return getClass(name);
        } catch (ClassNotFoundException e) {
            return Class.forName(name2);
        }
    }
    
    /**
     * @see FReflection#getDeclaredMethod(Class, String, String, Class...)
     * 
     * @since 1.2.3
     * 
     * @param c
     * @param name
     * @param parameterTypes
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    public Method getDeclaredMethod(Class<?> c, String name,  Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        return c.getDeclaredMethod(name,  parameterTypes);
    }
    
    /**
     * Use this to specify a method with intermediary and yarn names of classes for cleaner code.
     * 
     * @since 1.2.3
     * 
     * @param c
     * @param name
     * @param name2
     * @param parameterTypes
     * @return a {@link java.lang.reflect.Method Method} reference.
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    public Method getDeclaredMethod(Class<?> c, String name, String name2,  Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        try {
            return c.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            return c.getDeclaredMethod(name2, parameterTypes);
        }
    }
    
    /**
     * @see FReflection#getDeclaredField(Class, String, String)
     * 
     * @since 1.2.3
     * 
     * @param c
     * @param name
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    public Field getDeclaredField(Class<?> c, String name) throws NoSuchFieldException, SecurityException {
        return c.getDeclaredField(name);
    }
    
    /**
     * Use this to specify a field with intermediary and yarn names of classes for cleaner code.
     * 
     * @since 1.2.3
     * 
     * @param c
     * @param name
     * @param name2
     * @return a {@link java.lang.reflect.Field Field} reference.
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    public Field getDeclaredField(Class<?> c, String name, String name2) throws NoSuchFieldException, SecurityException {
        try {
            return c.getDeclaredField(name);
        } catch (NoSuchFieldException | SecurityException e) {
            return c.getDeclaredField(name2);
        }
    }
    
    /**
     * Invoke a method on an object (can be {@code null}) with auto type coercion for numbers.
     * 
     * @since 1.2.3
     * 
     * @param m
     * @param c
     * @param objects
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public Object invokeMethod(Method m, Object c, Object...objects) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?>[] params = m.getParameterTypes();
        for (int i = 0; i < objects.length; ++i) {
            if ((params[i] == int.class || params[i] == Integer.class) && !(objects[i] instanceof Integer)) {
                objects[i] = ((Number)objects[i]).intValue();
            } else if ((params[i] == float.class || params[i] == Float.class) && !(objects[i] instanceof Float)) {
                objects[i] = ((Number)objects[i]).floatValue();
            } else if ((params[i] == double.class || params[i] == Double.class) && !(objects[i] instanceof Double)) {
                objects[i] = ((Number)objects[i]).doubleValue();
            } else if ((params[i] == short.class || params[i] == Short.class) && !(objects[i] instanceof Short)) {
                objects[i] = ((Number)objects[i]).shortValue();
            } else if ((params[i] == long.class || params[i] == Long.class) && !(objects[i] instanceof Long)) {
                objects[i] = ((Number)objects[i]).longValue();
            } else if ((params[i] == char.class || params[i] == Character.class) && !(objects[i] instanceof Character)) {
                objects[i] = (char) ((Number)objects[i]).intValue();
            } else if ((params[i] == byte.class || params[i] == Byte.class) && !(objects[i] instanceof Byte)) {
                objects[i] = ((Number)objects[i]).byteValue();
            }
        }
        return m.invoke(c, objects);
    }

    /**
     * Loads a jar file to be accessible with this library.
     * 
     * @since 1.2.6
     * 
     * @param file relative to the macro folder.
     * @throws IOException
     */
    public void loadJarFile(String file) throws IOException {
        File jarFile = new File(jsMacros.config.macroFolder, file);
        if (!jarFile.exists()) throw new FileNotFoundException("Jar File Not Found");
        try (URLClassLoader loader = new URLClassLoader(new URL[] {new URL("jar:file:"+jarFile.getCanonicalPath() + "!/")})) {
            try (JarFile jar = new JarFile(jarFile)) {
                Enumeration<JarEntry> entries = jar.entries();
                while(entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (!entry.getName().endsWith(".class")) continue;
                    String className = StringUtils.removeEndIgnoreCase(entry.getName().replaceAll("/", "."), ".class");
                    try {
                        loadedClasses.put(className, loader.loadClass(className));
                    } catch (ClassNotFoundException e) {
                    }
                }
            } catch (IOException e) {
                throw e;
            }
        } catch (IOException e) {
            throw e;
        }
    }
}