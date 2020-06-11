# TMFramework

*This project aims to reduce the efforts to write server-side code or backend code for web development. This project is specifically for java based webservers.*

## Getting Started

Do you want to ease the server-side coding and spare yourself from that burdensome job? If yes, then you're at the right place. Following will brief you with the project.

## Prerequisites

The user has to download the jar file and put it into the following folder:

```
some-directory\tomcat-folder\webapps\your-web-folder-WEB-INF\lib\
```

The user has to add the following snippet to their ```web.xml``` file

```
    <servlet>
           <servlet-name>Configuraion</servlet-name>
           <servlet-class>com.thinking.machines.conf.TMConf</servlet-class>
           <load-on-startup>0</load-on-startup>
    </servlet>

    <servlet>
           <servlet-name>Service</servlet-name>
           <servlet-class>com.thinking.machines.servlet.MasterServlet</servlet-class>
    </servlet>
  
    <servlet-mapping>
           <servlet-name>Service</servlet-name>
           <url-pattern>/service/*</url-pattern>
    </servlet-mapping>

```

## Usage and Examples

All the user has to do is to use annotations with simple java classes and methods. That's it. We're taking care of the rest!

Following are the annotations can be used.


1. ***Path***: The path annotation is mandatory. One has to annotate both class and method. The class and method path values make up a service method, which is used as URL-pattern after the word 'service'. The service path must be unique. In case of having multiple services having the same service path, the first encountered service is kept and others are ignored.

The sample snippet is as follows
```
@Path("/classPath")
public class Sample {

     @Path("/methodPath")
    public String getSomething() {
         //Code
    }
}

```

2. ***Secured***: This annotation is optional. Both method and class can be secured annotated. To use this annotation, the user has to create a class implementing an interface ```Security```. The following two methods need to be implemented:

```
 public boolean isAuthenticate(HttpServletResponse response, HttpServletRequest request, 
                              ServletContext context, HttpSession session);
 public String doService(HttpServletResponse response, HttpServletRequest request,
                                  ServletContext context, HttpSession session);
```
The value for this annotation is **complete name** for this created class.

Usage:

```
@Path("/classValue")
@Secured("com.java.security.ClassTwo") //All methods of the class are secured.
public class Sample {

    @Path("/pathValue")
    @Secured("com.java.security.ClassTwo") //For this particular method this class is used for security check.
    public String getSomething() {
        //Code
    }
    
}

An interface that needs to be implemented is ```com.thinking.machines.interface.Security```.

```

3. ***Forward***: The forward annotation is optional and is used when the user has to forward the request to another service or the ```jsp``` file. The methods can be forward annotated not classes.

The value of the forward annotation includes either the ```jsp``` file or the service path of another service. 
If the forwarded service is of the same class then only the method name of the forwarded service can be used as a value.

```
@Path(/Sample)
public class Sample {

    @Path("/Ice")
    @Forward("tohtml")
    public String getIce() {
        //Code
    }
 
    @Path("tohtml")
    @ResponseType("html")
    public String iceToHtml() {
        //Code
    }

}
```
Note: The request is forwarded after the invokation of the current service method.

4. ***ResponseType***: The annotation ResponseType is optional, however, it's always good to use it. The ResponseType annotation can be used only over the methods.

Following are the permitted ResponseType values:

- JSON
- Html
- text
- Nothing

If no ResponseType annotation is used with the service method, the framework goes with the default value: ```text```.

Usage
```
@Path("/updateGender")
@ResponseType("JSON")
public EmployeeBean updateGender(EmployeeBean emp) {
    (emp.getGender().equalsIgnoreCase("Male") ? emp.setGender("Female") ? emp.setGender("Male");
    return emp;
}
```
5. ***RequestData***: This annotation is used with the parameters of the service method. All the parameters expect HttpServletRequest, HttpServletResponse, ServletContext, HttpSession must have RequestData annotation.

The value of this RequestData annotation must match the parameter in HttpServletRequest.

The other exceptions are:
- If ```POST``` type request is made, the ```JSON``` object is read and is directly parsed to the object of type first parameter of the service method.
- If ```GetFiles``` annotation is used, the parameter ```File``` or ```File[]``` need not have RequestData annotation in service method.

Usage:
```
@Path("/get")
public String getSomething(@RequestData("Name") String name, HttpServletRequest request, @RequestData("Salary") float salary) {
        //Code
 }

```

6. ***GetFiles***: This annotation is used when there is some file(s) uploaded by the ```HttpServeltRequest``` object and the user has to access it. All user has to do is to add ```File``` or ```File[]``` parameter to the service method, with or without RequestData annotation.

This annotation has no value.

```
@Path("/accessFiles")
@GetFiles
public void accesstFiles(HttpServletResponse response, File[] files, HttpServletRequest request) {
        //Code
}
```

## About Tool

This framework is provided with the tool that generates two ```PDFs``` viz, ```Services.pff``` and ```Errors.pdf```. The tool takes two command-line arguments. The first argument is a path from where ```classloader``` has to read all the services. The second argument is optional and is a path where to store the generated ```PDFs```.

The ```Services.pdf``` contains all the metadata about all services, the ```classloader``` has encountered in the provided path.
The ```Errors.pdf``` contains all the syntactical errors regarding the framework.

If the second argument is not provided, the tool creates the folder ```tmpFiles``` in the working directory and stores the ```PDFs``` there.

The class for this tool is ```com.thinking.machines.tool.FrameworkTool.class``` present in the ```jar``` file.

## Author

* **Shivam Gupta**, Student, Thinking Machines

## Acknowledgments

- [Spring Framework](https://github.com/spring-projects/spring-framework)
- [Thinking Machines](https://thinkingmachines.in/)
