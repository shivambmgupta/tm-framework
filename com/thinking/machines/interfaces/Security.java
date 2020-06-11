package com.thinking.machines.interfaces;
import javax.servlet.*;
import javax.servlet.http.*;
public interface Security {
 public boolean isAuthenticate(HttpServletResponse response, HttpServletRequest request, 
 					         ServletContext context, HttpSession session);
 public String doService(HttpServletResponse response, HttpServletRequest request,
 				                 ServletContext context, HttpSession session);
}