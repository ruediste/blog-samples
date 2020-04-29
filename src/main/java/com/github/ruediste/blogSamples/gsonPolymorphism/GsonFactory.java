package com.github.ruediste.blogSamples.gsonPolymorphism;

import com.github.ruediste.blogSamples.gsonPolymorphism.GsonPolymorphAdapter.PolymorphStyle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonFactory {

	public static Gson buildGson(PolymorphStyle style, ClassLoader cl, String pkg) {
		return new GsonBuilder().registerTypeAdapterFactory(new GsonPolymorphAdapter(style, cl, pkg)).create();
	}
}
