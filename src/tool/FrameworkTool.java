package com.thinking.machines.tool;

import java.io.*;
import java.util.*;
import com.thinking.machines.service.*;
import com.thinking.machines.utility.*;

public class FrameworkTool {

	private Map<String, Service> map;
	private String path;
	private String toSave;
	private List<MetaService> allServices;
	private List<MetaService> badServices;

	public FrameworkTool(String path, String toSave) throws Exception {
		this.path = path;
		this.toSave = toSave;
		this.map = ClassFileLoader.load(this.path);
		this.allServices = new LinkedList<>();
		this.badServices = new LinkedList<>();
		this.classifyServices();
	}

	private void classifyServices() {
		List<String> errors;
		MetaService mService;

		for(Service service : this.map.values()) {
			errors = new LinkedList<>();
			mService = new MetaService();
			mService.service = service;

			if(service.getServiceClass() == null) {
				errors.add("Service class is null");
				mService.hasBadClass = true;
			}
			if(service.getServiceMethod() == null) {
				errors.add("Service method is null");
				mService.hasBadMethod = true;
			}
			if(service.forwardValue != null) {
				if(!(service.forwardValue.endsWith(".jsp") || map.get(service.forwardValue) != null)) {
					if(service.forwardValue.contains("."))
						errors.add(service.forwardValue.substring(service.forwardValue.indexOf(".")) + " type of file is not forwardable");
					else 
						errors.add("Service path: " + service.forwardValue + " not found!");
					mService.hasBadForward = true;
				}
			}
			if(service.securedValue != null) {
              	try {
               		Class.forName(service.securedValue);
               	} catch(Exception exception) {
               		errors.add(exception.getMessage());
               		mService.hasBadSecured = true;
               }
			}
			if(service.responseTypeValue != null) {
				if(!(service.responseTypeValue.equalsIgnoreCase("json") || service.responseTypeValue.equalsIgnoreCase("html") || service.responseTypeValue.equalsIgnoreCase("text") || service.responseTypeValue.equalsIgnoreCase("Nothing"))) {
					errors.add("Invalid response type annotation value.");
					mService.hasBadResponseType = true;
				}
			}

			allServices.add(mService);
			if(mService.isBadService()) badServices.add(mService);
			mService.errors = errors; //for good services it will be empty
		}  //End of for loop

		new ServiceWriter(toSave, allServices).start();
		new ErrorWriter(toSave, badServices).start();

		System.out.println("PDFs has been created.");

	}

	public static void main(String args[]) {
		try {

			if(args.length < 1) {
				System.err.println("Usage[Path-for-class-loader, Path-to-save(optional)]");
				return;
		    }

		    String pathToLoad = args[0];
		    String pathToSave;

		    if(args.length >= 2) {
		    	pathToSave = args[1];
		    }else {
		    	pathToSave = System.getProperty("user.dir");
		    }

		    pathToSave += File.separator + "tmpFiles" + File.separator;
		    
		    File file = new File(pathToSave);
		    if(!file.exists()) file.mkdir();

			new FrameworkTool(pathToLoad, pathToSave);

		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}
}