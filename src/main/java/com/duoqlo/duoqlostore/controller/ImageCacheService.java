package com.duoqlo.duoqlostore.controller;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageCacheService {
    private static final ImageCacheService instance = new ImageCacheService();

    private final Map<String, List<Image>> cache = new HashMap<>();

    private ImageCacheService() {}

    public static ImageCacheService getInstance() {
        return instance;
    }

    public List<Image> getImages(String path) {
        return cache.get(path);
    }

    public void putImages(String path, List<Image> images) {
        cache.put(path, images);
    }

    public void clear() { cache.clear(); }
}
