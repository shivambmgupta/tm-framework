package com.thinking.machines.servlet;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.google.gson.*;
import com.thinking.machines.service.*;
import com.thinking.machines.utility.*;

public class MasterServlet extends HttpServlet{

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        String res= "";

        try {
           out = response.getWriter();
           res = RequestProcessor.processGetRequest(response, request, getServletContext());
        } catch(Exception exception) {
           res = exception.getMessage();
        }

        out.println(res);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        String res = "";

        try {
           out = response.getWriter();
           res = RequestProcessor.processPostRequest(response, request, getServletContext());
        } catch(Exception exception) {
           res = exception.getMessage();
        }

        out.println(res);
    }
}
