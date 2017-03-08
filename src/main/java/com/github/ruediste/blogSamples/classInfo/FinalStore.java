package com.github.ruediste.blogSamples.classInfo;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class FinalStore<T> implements ClassInfoStore<T> {

    private static class InfoMap<T> extends HashMap<Class<?>, T> {
        private static final long serialVersionUID = 1L;

    }

    private static Map<ClassLoader, WeakReference<Map<FinalStore<?>, InfoMap<?>>>> infoMapsLookup = new WeakHashMap<>();
    private Map<ClassLoader, WeakReference<InfoMap<?>>> infoMapLookup = new WeakHashMap<>();
    private Map<Class<?>, WeakReference<T>> infoLookup = new WeakHashMap<>();

    public static class StoreHelper {
        public static Object infoMaps;
    }

    @Override
    public T get(Class<?> cls) {
        WeakReference<T> ref = infoLookup.get(cls);
        if (ref == null)
            return null;
        else
            return ref.get();
    }

    private final static byte[] helperBytecode;
    private final static Method defineClassMethod;
    static {
        // load bytecode
        try (InputStream in = StoreHelper.class.getClassLoader()
                .getResourceAsStream(StoreHelper.class.getName().replace('.', '/') + ".class")) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] bb = new byte[1024];
            int read;
            while ((read = in.read(bb)) > 0) {
                baos.write(bb, 0, read);
            }
            helperBytecode = baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // get define method
        try {
            defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class,
                    Integer.TYPE, Integer.TYPE);
            defineClassMethod.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void store(Class<?> cls, T value) {
        ClassLoader loader = cls.getClassLoader();

        WeakReference<InfoMap<?>> infoMapRef = infoMapLookup.get(loader);
        InfoMap<T> infoMap;
        if (infoMapRef == null) {
            infoMap = new InfoMap<>();
            getInfoMaps(loader).put(this, infoMap);
            infoMapLookup.put(loader, new WeakReference<FinalStore.InfoMap<?>>(infoMap));
        } else {
            infoMap = (InfoMap<T>) infoMapRef.get();
        }

        infoMap.put(cls, value);
        infoLookup.put(cls, new WeakReference<>(value));
    }

    private Map<FinalStore<?>, InfoMap<?>> getInfoMaps(ClassLoader loader) {
        // get info maps
        WeakReference<Map<FinalStore<?>, InfoMap<?>>> infoMapsRef = FinalStore.infoMapsLookup.get(loader);
        Map<FinalStore<?>, InfoMap<?>> infoMaps;
        if (infoMapsRef == null) {
            try {
                Class<?> helperCls = (Class<?>) defineClassMethod.invoke(loader, StoreHelper.class.getName(),
                        helperBytecode, 0, helperBytecode.length);
                Field field = helperCls.getField("infoMaps");
                infoMaps = new WeakHashMap<>();
                field.set(null, infoMaps);
                FinalStore.infoMapsLookup.put(loader, new WeakReference<Map<FinalStore<?>, InfoMap<?>>>(infoMaps));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else
            infoMaps = infoMapsRef.get();
        return infoMaps;
    }

}
