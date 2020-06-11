package com.thinking.machines.utility;

import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.lang.reflect.*;
import com.thinking.machines.service.*;

public class ClassFileLoader {
 
 private static final String CLASS_FILE_SUFFIX = ".class";
 private static final char PACKAGE_SEPARATOR = '.';

 public static Map<String, Service> load(String path) {
    File currFile = new File(path);
    File[] subFiles = currFile.listFiles();
    if(subFiles == null || subFiles.length == 0) return null;
    Map<String, Service> map  = new HashMap<>();
    for(File file : subFiles)  
      map.putAll(find(file, ""));
    return map; 
 }

 private static Map<String, Service> find(File file, String currPath) {
   Map<String, Service> map = new HashMap<>();
   String res = (currPath != "")? currPath + PACKAGE_SEPARATOR + file.getName() : file.getName();
   if(file.isDirectory()) {
     for (File child : file.listFiles()) {
       map.putAll(find(child, res));
     }
   }
   else if (res.endsWith(CLASS_FILE_SUFFIX)) {
     int endIndex = res.length() - CLASS_FILE_SUFFIX.length();
     String className = res.substring(0, endIndex);
     try {
      Class c = Class.forName(className);
      Method[] methods = c.getDeclaredMethods();
      for(Method method : methods) {
        Service service = new Service(c, method);
        if(service.loadable) {
          map.put(service.getServicePath(), service);
        }
      }
     } catch(Exception ignore){}
   }
   return map;
 }
}
