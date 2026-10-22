package com.gathin.tvhome;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

final class WallpaperRotation {
    static List<File> images(File directory) {
        List<File> result = new ArrayList<>();
        File[] entries = directory == null ? null : directory.listFiles();
        if (entries == null) return result;
        Arrays.sort(entries, Comparator.comparing(File::getName));
        for (File entry : entries) {
            String name = entry.getName().toLowerCase(Locale.ROOT);
            if (entry.isFile() && entry.length() > 0 && entry.length() <= 10 * 1024 * 1024
                    && (name.endsWith(".jpg") || name.endsWith(".jpeg")
                    || name.endsWith(".png") || name.endsWith(".webp"))) {
                result.add(entry);
                if (result.size() == 100) break;
            }
        }
        return result;
    }

    /** Discovers all wallpapers across external storage (/sdcard/Wallpapers) and theme files. */
    static List<File> allImages(Context context) {
        List<File> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        List<File> dirs = new ArrayList<>();

        File ext = Environment.getExternalStorageDirectory();
        if (ext != null) {
            dirs.add(new File(ext, "Wallpapers"));
            dirs.add(new File(ext, "Pictures/Wallpapers"));
            dirs.add(new File(ext, "Pictures"));
        }
        File appDir = Config.dir(context);
        if (appDir != null) {
            dirs.add(new File(appDir, "wallpapers"));
        }

        for (File dir : dirs) {
            for (File img : images(dir)) {
                if (seen.add(img.getName())) {
                    result.add(img);
                }
            }
        }

        if (appDir != null) {
            File single = new File(appDir, Config.WALLPAPER_NAME);
            if (single.isFile() && single.length() > 0 && seen.add(single.getName())) {
                result.add(single);
            }
        }
        return result;
    }

    static File find(List<File> images, String name) {
        if (name == null || images == null) return null;
        for (File image : images) if (image.getName().equals(name)) return image;
        return null;
    }

    static File pick(List<File> images, String previous, Random random) {
        List<File> candidates = new ArrayList<>(images);
        if (candidates.size() > 1) candidates.removeIf(image -> image.getName().equals(previous));
        return candidates.isEmpty() ? null : candidates.get(random.nextInt(candidates.size()));
    }

    static boolean due(long previous, long now, long interval) {
        return interval > 0 && (previous == 0 || now < previous || now - previous >= interval);
    }
}
