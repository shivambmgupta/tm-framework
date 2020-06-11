package com.thinking.machines.utility;

import java.io.*;
import java.util.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import com.thinking.machines.annotation.*;

public class AnnotationProcessor {

	private AnnotationProcessor(){}

	public static String processPathAnnotation(Class<?> clazz, Method method) {
   		com.thinking.machines.annotation.Path tempPath;
   		String temp;
	   	String classPath = "";
   		tempPath = (com.thinking.machines.annotation.Path)clazz.getAnnotation(com.thinking.machines.annotation.Path.class);
 	  	if(tempPath != null) { 
      		temp = tempPath.value().trim();
      		classPath = temp.startsWith("/") ? temp : "/" + temp;
   		}
		else return null;
   		String methodPath = "";
   		tempPath = method.getAnnotation(com.thinking.machines.annotation.Path.class);
   		if(tempPath != null) {
      		temp = tempPath.value().trim();
      		methodPath = temp.startsWith("/") ? temp : "/" + temp;
   		}
   		else return null; 
   		return classPath + methodPath;
	}
	public static String processForwardAnnotation(Class<?> clazz, Method method) {
   	    com.thinking.machines.annotation.Forward forward = method.getAnnotation(com.thinking.machines.annotation.Forward.class);
       	if(forward == null) return null;
       	String temp = forward.value().trim();
        if(temp.endsWith(".jsp")) return temp;
        temp = temp.startsWith("/") ? temp : "/" + temp;
    		if(!temp.substring(1).contains("/")) {
       		  com.thinking.machines.annotation.Path tempPath;
       		  String classPath = "";
       		  tempPath = (com.thinking.machines.annotation.Path)clazz.getAnnotation(com.thinking.machines.annotation.Path.class);
       		  if(tempPath != null) { 
        		classPath = tempPath.value().trim();
        		classPath = classPath.startsWith("/") ? classPath : "/" + classPath;
       		  }
       		temp = classPath + temp;
    	       }
    	       return temp;
 	}
 	public static String processSecuredAnnotation(Class<?> clazz, Method method) {
   		com.thinking.machines.annotation.Secured secured;
   		secured = method.getAnnotation(com.thinking.machines.annotation.Secured.class);
   		if(secured == null) {
    		secured = (com.thinking.machines.annotation.Secured)clazz.getAnnotation(com.thinking.machines.annotation.Secured.class);
   		}  
   		if(secured == null) return null;
   
   		return secured.value().trim();
 	}
 	public static boolean processGetFileAnnotation(Class<?> clazz, Method method) {
   		com.thinking.machines.annotation.GetFiles getFiles;
   		getFiles = method.getAnnotation(com.thinking.machines.annotation.GetFiles.class);
   		if(getFiles == null) return false;
   		else return true;
 	}
 	public static String processResponseTypeAnnotation(Class<?> clazz, Method method) {
   		com.thinking.machines.annotation.ResponseType responseType;
   		responseType = method.getAnnotation(com.thinking.machines.annotation.ResponseType.class);
   		if(responseType == null) return null;
   		return responseType.value().trim();
 	}
}