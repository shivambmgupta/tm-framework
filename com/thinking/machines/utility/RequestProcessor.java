package com.thinking.machines.utility;

import java.beans.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.annotation.*;
import com.google.gson.*;
import com.thinking.machines.annotation.*;
import com.thinking.machines.service.*;
import com.thinking.machines.interfaces.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.output.*;


public class RequestProcessor{


 public static String processPostRequest(HttpServletResponse response, HttpServletRequest request, ServletContext servletContext) throws Exception {
   Map<String, Service> map = (Map<String, Service>)servletContext.getAttribute("pathToServiceMap");
   if(map == null)
       throw new Exception("Error! No java(path annonated) classes found.");

   String requestURL = request.getRequestURI();
   String resURL = requestURL.substring(requestURL.indexOf("service") + "service".length());
   Service service = map.get(resURL);
   return processPostService(response, request, servletContext, service, null);
 }

 public static String processGetRequest(HttpServletResponse response, HttpServletRequest request, ServletContext servletContext) throws Exception {
   Map<String, Service> map = (Map<String, Service>)servletContext.getAttribute("pathToServiceMap");
   if(map == null)
       throw new Exception("Error! No java(path annonated) classes found.");

   String requestURL = request.getRequestURI();
   String resURL = requestURL.substring(requestURL.indexOf("service") + "service".length());
   Service service = map.get(resURL);
   return processGetService(response, request, servletContext, service);
 }

 public static String processGetService(HttpServletResponse response, HttpServletRequest request,
						 ServletContext servletContext, Service service) throws Exception {

   if(service == null) throw new Exception("Address not found");

   Map<String, Service> map = (Map<String, Service>)servletContext.getAttribute("pathToServiceMap");

   String queryString = request.getQueryString() != null ? request.getQueryString() : "";

   Class clazz = service.getServiceClass();
   Method method = service.getServiceMethod();
   Object object = service.getServiceObject();

   if(method == null || clazz == null) throw new Exception("Address not found");
   if(object == null)  object = clazz.newInstance();

   /**
    * A security clause is added.
    * In this clause, a security check will be made
    * if the check passes then control should proceed,
    * otherwise it should bypass.
    */
      
   String securedValue = service.securedValue;

   if(securedValue != null) {
    Class<?> c = Class.forName(securedValue);
    Object o = c.newInstance();
    if(o == null)
      throw new Exception("Security error!");
    if(!(boolean)c.getMethod("isAuthenticate", HttpServletResponse.class, 
                                              HttpServletRequest.class, ServletContext.class,
                                              HttpSession.class)
                                              .invoke(o, response, request, servletContext, request.getSession()))

      return (String)c.getMethod("doService", HttpServletResponse.class, 
                                              HttpServletRequest.class, ServletContext.class,
                                              HttpSession.class)
					      .invoke(o, response, request, servletContext, request.getSession());
   }

   String forwardedPath = service.forwardValue;
   Service forwardedService = null;
   if(forwardedPath != null) {
     forwardedService =  forwardedPath.endsWith(".jsp") ? null : map.get(forwardedPath);
   }

  Gson gson = new Gson(); //This is to parse response String if responseType is JSON
  int parameterCount = method.getParameterCount();
  Object[] parameters = new Object[parameterCount];
  Class<?> returnType = method.getReturnType();
  String responseString = "";
  Object responseObject = null;
  Annotation[][] annotations = method.getParameterAnnotations();
  Class[] paramTypes = method.getParameterTypes();
  Annotation[] annotationRow;
 
  String responseTypeValue = service.responseTypeValue;
  if(responseTypeValue == null) {
   responseTypeValue = "text/plain";
  }

  if(queryString.equals("")) {
   int i = 0;
   while(i < parameterCount){
      if(paramTypes[i] == javax.servlet.http.HttpServletResponse.class)
        parameters[i] = response;
      else if(paramTypes[i] == javax.servlet.http.HttpServletRequest.class)
        parameters[i] = request;
      else if(paramTypes[i] == javax.servlet.ServletContext.class)
        parameters[i] = servletContext;
      else if(paramTypes[i] == javax.servlet.http.HttpSession.class)
        parameters[i] = request.getSession();
      else parameters[i] = null;
      ++i;
   }
   if(returnType == void.class) {
     method.invoke(object, parameters);
     if(forwardedService != null) {
       responseString = processGetService(response, request, servletContext, forwardedService);
     }
     else if(forwardedPath != null && forwardedPath.endsWith(".jsp"))
       request.getRequestDispatcher(forwardedPath).forward(request, response);
     return responseString;
   }
   else{

     //this for request having no qs and methods return type is not void.

     responseObject = method.invoke(object, parameters);
     if(responseTypeValue.equalsIgnoreCase("JSON")) {
      response.setContentType("application/JSON");
      responseString = gson.toJson(responseObject);
     }
     else if(responseTypeValue.equalsIgnoreCase("html")) {
      response.setContentType("text/html");
      responseString = responseObject.toString();
     }
     else {
      response.setContentType("text/plain");
      responseString = responseObject.toString();
     }

     if(forwardedService != null)
       responseString = processGetService(response, request, servletContext, forwardedService);
     else if(forwardedPath != null && forwardedPath.endsWith(".jsp"))
       request.getRequestDispatcher(forwardedPath).forward(request, response);
     return responseString;
  }   
 }

  /**
   * If control's here, then it means it has some QueryString.
   */

  String[] nameValuePairs = queryString.split("&");
  Map<String, String> nameValueMap = new HashMap<>();
  String name, value;
  int indexOfEqual;
  for(String nameValuePair : nameValuePairs) {
     indexOfEqual = nameValuePair.indexOf('=');
     name = nameValuePair.substring(0, indexOfEqual);
     value = nameValuePair.substring(indexOfEqual + 1);
     nameValueMap.put(name, value);
  }

 /**
  * As per design it should have either RequestData annotation,
  * or it must be either HttpServletResponse, HttpServletRequest,
  * ServletContext, or HttpSession.
  */

  int i = 0;
  while(i < parameterCount) {
   annotationRow = annotations[i];
   String anonValue = getRequestDataAnnonValue(annotationRow);
   if(anonValue == null) {
      if(paramTypes[i] == javax.servlet.http.HttpServletResponse.class)
        parameters[i] = response;
      else if(paramTypes[i] == javax.servlet.http.HttpServletRequest.class)
        parameters[i] = request;
      else if(paramTypes[i] == javax.servlet.ServletContext.class)
        parameters[i] = servletContext;
      else if(paramTypes[i] == javax.servlet.http.HttpSession.class)
       parameters[i] = request.getSession();
      else throw new Exception("parameter should either have RequestData annotaion or must of be of either request/response/servletContext/Session type");
      ++i;
      continue;
   }
   value = nameValueMap.get(anonValue);

   nameValueMap.remove(anonValue); //This is done to remove the bug. 
				                           //If user has sent some name-value that does not match parameter
				                           //in the method.
   if(value == null)
     throw new Exception("QS has some missing name-value pair as described in method.");

   PropertyEditor editor = PropertyEditorManager.findEditor(paramTypes[i]);
   editor.setAsText(value);
   parameters[i] = editor.getValue();
   ++i;
  } //end of while

  if(nameValueMap.size() != 0) {
    throw new Exception("Query string may have some parameter that does not match with the method.");
  }

  if(returnType == void.class) {
    method.invoke(object, parameters);
    if(forwardedService != null)
      responseString = processGetService(response, request, servletContext, forwardedService);
    else if(forwardedPath != null && forwardedPath.endsWith(".jsp"))
      request.getRequestDispatcher(forwardedPath).forward(request, response);
    return responseString;
  }

  responseObject = method.invoke(object, parameters);
  if(responseTypeValue.equalsIgnoreCase("JSON")) {
   response.setContentType("application/JSON");
   responseString = gson.toJson(responseObject);
  }
  else if(responseTypeValue.equalsIgnoreCase("html")) {
   response.setContentType("text/html");
   responseString = responseObject.toString();
  }
  else {
   response.setContentType("text/plain");
   responseString = responseObject.toString();
  }
  if(forwardedService != null)
    responseString = processGetService(response, request, servletContext, forwardedService);
  else if(forwardedPath != null && forwardedPath.endsWith(".jsp"))
    request.getRequestDispatcher(forwardedPath).forward(request, response);
  return responseString;
 }

 private static  String getRequestDataAnnonValue(Annotation[] annos){
   if(annos == null || annos.length == 0) return null;
   for(Annotation annotation : annos)
    if(RequestData.class.isInstance(annotation)){
      return ((RequestData)annotation).value();
    }
    return null;
 }
 
 private static String processPostService(HttpServletResponse response, HttpServletRequest request,
                                             ServletContext servletContext, Service service, String JsonStr) throws Exception {


   Map<String, Service> map = (Map<String, Service>)servletContext.getAttribute("pathToServiceMap");

   if(service == null) throw new Exception("Address not found");
   
   Class clazz = service.getServiceClass();
   Method method = service.getServiceMethod();
   if(clazz == null || method == null) throw new Exception("Address not found.");
   Object object = service.getServiceObject();

   /**
    * A security clause is added.
    * In this clause, a security check will be made
    * if the check passes then control should proceed,
    * otherwise it should bypass.
    */
      
    String securedValue = service.securedValue;

    if(securedValue != null) {
     Class<?> c = Class.forName(securedValue);
     Object o = c.newInstance();
     if(o == null)
       throw new Exception("Security error!");
     if(!(boolean)c.getMethod("isAuthenticate",HttpServletResponse.class, 
                                               HttpServletRequest.class, ServletContext.class,
                                               HttpSession.class)
                                              .invoke(o, response, request, servletContext, request.getSession()))

       return (String)c.getMethod("doService", HttpServletResponse.class, 
                                               HttpServletRequest.class, ServletContext.class,
                                               HttpSession.class)
                                              .invoke(o, response, request, servletContext, request.getSession());
    }

   if(JsonStr == null && service.getFileAnnotation == false) {
    StringBuffer sb = new StringBuffer();
    String line = null;
    BufferedReader reader = request.getReader();
    while((line = reader.readLine()) != null) sb.append(line);
    JsonStr = sb.toString();
   }

   Class returnType = method.getReturnType();

   Object responseObject = null;
   String responseString = "";

   Class[] paramTypes = method.getParameterTypes();

   int parameterCount = method.getParameterCount();
   Object[] parameters = new Object[parameterCount];


   Gson gson = new Gson();
   parameters[0] = gson.fromJson(JsonStr, paramTypes[0]);

   String forwardedPath = service.forwardValue;
   Service forwardedService = null;
   if(forwardedPath != null) {
     forwardedService = forwardedPath.endsWith(".jsp") ? null : map.get(forwardedPath);
   }


   File[] files = null;
   if(service.getFileAnnotation) {
     files = processGetFilesRequest(response, request, servletContext, service);
   }

   int i = service.getFileAnnotation ? 0 : 1;  //as first parameter will be Class JSON corresponds to.
   while(i < parameterCount) {

      if(paramTypes[i] == javax.servlet.http.HttpServletResponse.class)
        parameters[i] = response;

      else if(paramTypes[i] == javax.servlet.http.HttpServletRequest.class)
        parameters[i] = request;

      else if(paramTypes[i] == javax.servlet.ServletContext.class)
        parameters[i] = servletContext;

      else if(paramTypes[i] == javax.servlet.http.HttpSession.class)
       parameters[i] = request.getSession();

      else if(paramTypes[i] == java.io.File.class && service.getFileAnnotation)
        parameters[i] = files[0];

      else if(paramTypes[i] == java.io.File[].class && service.getFileAnnotation)
        parameters[i] = files;

      else parameters[i] = null;

      ++i;
   }

   if(returnType == void.class) 
   {
     method.invoke(object, parameters);
     if(forwardedService != null)
       responseString = processPostService(response, request, servletContext, forwardedService, JsonStr);
     else if(forwardedPath != null && forwardedPath.endsWith(".jsp"))
       request.getRequestDispatcher(forwardedPath).forward(request, response);
     return responseString;
   }
   responseObject = method.invoke(object, parameters);

   String responseTypeValue = service.responseTypeValue;
   if(responseTypeValue == null)
     responseTypeValue = "text/plain";
 
   if(responseTypeValue.equalsIgnoreCase("JSON"))
   {
     response.setContentType("application/JSON");
     responseString = gson.toJson(responseObject);
   }
   else if(responseTypeValue.equalsIgnoreCase("html"))
   {
    response.setContentType("text/html");
    responseString = responseObject.toString();
   }
   else
   {
    response.setContentType("text/plain");
    responseString = responseObject.toString();
   }
   if(forwardedService != null)
     responseString = processPostService(response, request, servletContext, forwardedService, JsonStr);
   else if(forwardedPath != null && forwardedPath.endsWith(".jsp"))
     request.getRequestDispatcher(forwardedPath).forward(request, response);
   return responseString;
 }

 private static File[] processGetFilesRequest(HttpServletResponse response, HttpServletRequest request,
                                                               ServletContext servletContext, Service service) throws Exception
 {
  try{
  if(service == null) throw new Exception("null service exception");

  Class<?> clazz = service.getServiceClass();
  Method method = service.getServiceMethod();

  if(clazz == null || method == null)
    throw new Exception("null class or method exception");

  boolean isMultipart;
  long fileSizeThreshold = 500 * 1024;
  int memSizeThreshold = 4 * 1024;


  String uploadPath = servletContext.getRealPath("") + "WEB-INF" + File.separator + "uploads";

  File uploadDir = new File(uploadPath);
  if(!uploadDir.exists()) uploadDir.mkdir();

  /**
   * tempDir stores the files whose size > maxFileSize
   */
  String tempPath = "c://temp";

  File tempDir = new File(tempPath);
  if(!tempDir.exists()) tempDir.exists(); 

  DiskFileItemFactory factory = new DiskFileItemFactory();

  factory.setSizeThreshold(memSizeThreshold);
  factory.setRepository(tempDir);

  ServletFileUpload upload = new ServletFileUpload(factory);
  upload.setSizeMax(fileSizeThreshold);

  List<FileItem> fileItems = upload.parseRequest(request);
  Iterator<FileItem> iterator = fileItems.iterator();

  List<File> files = new ArrayList<>();

  File file;
  FileItem item;
  int i = 0;
  while(iterator.hasNext()) {

    item = (FileItem)iterator.next();
    if(!item.isFormField()) {
      file = new File(uploadPath + File.separator + item.getName());
      item.write(file);
      files.add(file);
    }

  }
    return files.toArray(new File[files.size()]);
}catch(Exception e){e.printStackTrace();}
  return null;
 }
}
