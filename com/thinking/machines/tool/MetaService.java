package com.thinking.machines.tool;

import java.util.*;
import com.thinking.machines.service.*;

public class MetaService {

   public Service service;
   public List<String> errors;

   public boolean hasBadForward;
   public boolean hasBadSecured;
   public boolean hasBadResponseType;
   public boolean hasBadClass;
   public boolean hasBadMethod;

   public boolean isBadService() {
   	return hasBadForward || hasBadSecured || hasBadResponseType || hasBadClass || hasBadMethod;
   }

}