package com.xtremelabs.robolectric.res;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ResourceDirs {

    private final List<File> resourceDirs;

    /**
     * @param resourceDir not null
     */
    public ResourceDirs(File resourceDir) {
        this(resourceDir, Collections.<File>emptyList());
    }

    /**
     * @param resourceDir not null
     * @param libraryResourceDirs not null, may be empty
     */
    public ResourceDirs(File resourceDir, List<File> libraryResourceDirs) {
        this(combine(resourceDir, libraryResourceDirs));
    }

    /**
     * @param resourceDirs not null, may be empty
     */
    private ResourceDirs(List<File> resourceDirs) {
        this.resourceDirs = Collections.unmodifiableList(new ArrayList<File>(resourceDirs));
    }

    private static List<File> combine(File resourceDir, List<File> libraryResourceDirs) {
        List<File> allDirs = new ArrayList<File>();
        if (resourceDir != null)
            allDirs.add(resourceDir);
        allDirs.addAll(libraryResourceDirs);
        return allDirs;
    }

    public List<File> getDirs() {
        return resourceDirs;
    }

    public File[] listFiles(FileFilter filter) {
        List<File> allFiles = getMatchingFiles(filter);
        return allFiles.toArray(new File[allFiles.size()]);
    }

    private List<File> getMatchingFiles(FileFilter filter) {
        List<File> allFiles = new ArrayList<File>();
        for (File dir : resourceDirs) {
            File[] files = dir.listFiles(filter);
            if (files != null) {
                allFiles.addAll(Arrays.<File>asList(files));
            }
        }
        return allFiles;
    }

    public boolean hasSomeExistingFiles() {
        for (File file : resourceDirs) {
            if (file.exists()) {
                return true;
            }
        }
        return false;
    }

    public ResourceDirs getValueResourceDirs() {
        return getByName("values");
    }

    private ResourceDirs getByName(final String name) {
        return new ResourceDirs(getMatchingFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().equals(name);
            }
        }));
    }

    public ResourceDirs getPreferenceResourceDirs() {
        return getByName("xml");
    }

}
