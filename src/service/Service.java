package com.thinking.machines.service;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.lang.annotation.*;
import com.thinking.machines.annotation.*;
import com.thinking.machines.utility.AnnotationProcessor;

public class Service {

  private Class clazz;
  private Method method;
  private Object object;
  private String servicePath;
  public boolean loadable;

  public String forwardValue;
  public String responseTypeValue;
  public String securedValue;
  public boolean getFileAnnotation;

  public com.thinking.machines.annotation.RequestData requestData;

  public Service() {
     this.clazz = null;
     this.method = null;
     this.object = null;
     this.servicePath = null;
     this.loadable = false;
     this.setAnnotations();
  }

  public Service(Class clazz, Method method) {
     this.clazz = clazz;
     this.method = method;
     this.object = null;
     this.servicePath = null;
     this.loadable = false;
     this.setAnnotations();
  }

  public void setAnnotations() {
     if(this.clazz == null || this.method == null) {
        this.servicePath = null;
        this.forwardValue = null;
        this.requestData = null;
        this.responseTypeValue = null;
        this.securedValue = null;
        return;
     }
     else {
        this.servicePath = AnnotationProcessor.processPathAnnotation(clazz, method);
        if(servicePath == null) {
          this.loadable = false;
          return;
        }
        this.forwardValue = AnnotationProcessor.processForwardAnnotation(clazz, method);
        this.securedValue = AnnotationProcessor.processSecuredAnnotation(clazz, method);
        this.responseTypeValue = AnnotationProcessor.processResponseTypeAnnotation(clazz, method);
        this.getFileAnnotation = AnnotationProcessor.processGetFileAnnotation(clazz, method);
        this.loadable = true;
      }
  }

  public String getServicePath() {
     return this.servicePath;
  }

  public Class getServiceClass() {
     return this.clazz;
  }

  public Method getServiceMethod() {
     return this.method;
  }

  public Object getServiceObject() throws Exception {
   if(this.object == null)
     this.object = this.clazz.newInstance();
   return this.object;
  }

  public void setServicePath(String path) {
     this.servicePath= path;
  }

  public void setServiceClass(Class clazz) {
     this.clazz = clazz;
  }

  public void setServiceMethod(Method method) {
     this.method = method;
  }

}
