package com.github.actions.utils;

import android.content.Context;
import java.io.*;
import java.util.*;

public class FileManager {
    
    public static List<File> listFiles(File dir) {
        List<File> files = new ArrayList<>();
        if (dir != null && dir.exists() && dir.isDirectory()) {
            File[] fileArray = dir.listFiles();
            if (fileArray != null) {
                Arrays.sort(fileArray, (f1, f2) -> {
                    if (f1.isDirectory() && !f2.isDirectory()) return -1;
                    if (!f1.isDirectory() && f2.isDirectory()) return 1;
                    return f1.getName().compareToIgnoreCase(f2.getName());
                });
                files.addAll(Arrays.asList(fileArray));
            }
        }
        return files;
    }
    
    public static boolean createFile(File parent, String name) {
        try {
            File file = new File(parent, name);
            return file.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }
    
    public static boolean createFolder(File parent, String name) {
        File folder = new File(parent, name);
        return folder.mkdirs();
    }
    
    public static boolean deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    deleteFile(f);
                }
            }
        }
        return file.delete();
    }
    
    public static String readFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
    
    public static boolean writeFile(File file, String content) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
