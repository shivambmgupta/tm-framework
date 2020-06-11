package com.thinking.machines.conf;
import java.io.*;
import java.util.*;
import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.thinking.machines.service.*;
import com.thinking.machines.utility.*;
public class TMConf extends HttpServlet{
 private Map<String, Service> pathToServiceMap;
 public void init(){
  try{
   ServletContext servletContext = getServletContext();
   String path = servletContext.getRealPath("") + "WEB-INF" + File.separator + "classes";
   pathToServiceMap = ClassFileLoader.load(path);
   servletContext.setAttribute("pathToServiceMap", pathToServiceMap);
  }catch(Exception exception) {
    exception.printStackTrace();
  }
 }
}
