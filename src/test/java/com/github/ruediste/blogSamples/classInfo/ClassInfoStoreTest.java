package com.github.ruediste.blogSamples.classInfo;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

public class ClassInfoStoreTest {

	private static class CustomClassLoader extends ClassLoader {

		private Class<?> cls;

		public CustomClassLoader(ClassLoader parent, Class<?> cls) {
			super(parent);
			this.cls = cls;
		}

		@Override
		protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
			Class<?> result = findLoadedClass(name);
			if (result != null)
				return result;
			if (cls.getName().equals(name)) {
				try (InputStream in = cls.getClassLoader().getResourceAsStream(name.replace('.', '/') + ".class")) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] bb = new byte[1024];
					int read;
					while ((read = in.read(bb)) > 0) {
						baos.write(bb, 0, read);
					}
					bb = baos.toByteArray();
					return defineClass(name, bb, 0, bb.length);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			return super.loadClass(name, resolve);
		}
	}

	private Class<?> load(Class<?> cls) {
		try {
			return new CustomClassLoader(cls.getClassLoader(), cls).loadClass(cls.getName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private static class A {
		int value;
	}

	private static class Info {
		Field field;
	}

	@Test
	public void test() throws Exception {
		// create a class information store
		ClassInfoStore<String> store = new WeakHashMapClassInfoStore<>();

		// load a class with a separate class loader
		Class<?> a = load(A.class);

		// store some data
		store.store(a, "Hello World");
		assertNotNull(store.get(a));

		// create a weak reference which will turn null as
		// soon as the class is garbage collected
		WeakReference<Class<?>> ref = new WeakReference<Class<?>>(a);

		// clear the reference to the class and perform a GC
		a = null;
		System.gc();

		// check that the class has been unloaded
		assertNull(ref.get());
	}

	@Test
	public void testWithBackref() throws Exception {
		// create a class information store
		ClassInfoStore<Info> store = new FinalStore<>();

		// load a class with a separate class loader
		Class<?> a = load(A.class);

		// store some data
		Info info = new Info();
		info.field = a.getDeclaredField("value");
		store.store(a, info);
		assertNotNull(store.get(a));

		// check that the store survives a GC
		info = null;
		System.gc();
		assertNotNull(store.get(a));

		// create a weak reference which will turn null as
		// soon as the class is garbage collected
		WeakReference<Class<?>> ref = new WeakReference<Class<?>>(a);

		// clear the reference to the class and perform a GC
		a = null;
		System.gc();

		// check that the class has been collected
		assertNull(ref.get());
	}
}
