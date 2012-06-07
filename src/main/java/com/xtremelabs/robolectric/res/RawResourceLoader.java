package com.xtremelabs.robolectric.res;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class RawResourceLoader {

    private ResourceExtractor resourceExtractor;
    private ResourceDirs resourceDirs;

    public RawResourceLoader(ResourceExtractor resourceExtractor, File resourceDir) {
        this(resourceExtractor, new ResourceDirs(resourceDir));
    }

    public RawResourceLoader(ResourceExtractor resourceExtractor, ResourceDirs resourceDirs) {
        this.resourceExtractor = resourceExtractor;
        this.resourceDirs = resourceDirs;
    }

    public InputStream getValue(int resourceId) {
        String resourceFileName = resourceExtractor.getResourceName(resourceId);
        if (resourceFileName == null) {
            throw new IllegalArgumentException("No resource " + toHex(resourceId));
        }
        String resourceName = resourceFileName.substring("/raw".length());

        for (File resourceDir : resourceDirs.getDirs()) {
            File rawResourceDir = new File(resourceDir, "raw");

            try {
                File[] files = rawResourceDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        String name = file.getName();
                        int dotIndex = name.indexOf(".");
                        final String fileBaseName;
                        if (dotIndex >= 0) {
                            fileBaseName = name.substring(0, dotIndex);
                        } else {
                            fileBaseName = name;
                        }
                        if (fileBaseName.equals(resourceName)) {
                            return new FileInputStream(file);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                return null;
            }
        }
        return null;
    }

    private static String toHex(int resourceId) {
        return "0x" + Integer.toHexString(resourceId);
    }

}
