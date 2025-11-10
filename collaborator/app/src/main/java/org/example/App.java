package org.example;

import java.io.File;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class App {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	private static final String IP = "127.0.2.1";
	private static final int PORT = 8080;

    public static void main(String[] args) throws LifecycleException {
    	// https://www.baeldung.com/tomcat-programmatic-setup
    	Tomcat tomcat = new Tomcat();
    	tomcat.setHostname(IP);
    	tomcat.setPort(PORT);
    	
    	// Note. AppBase: directory where Tomcat looks for web applications 
    	// for this host. It is used to load WARs. Not required here.
    	//tomcat.getHost().setAppBase("webapps");
    	    	
    	// Note. ContextPath: 'mount' path of the application relative to the host
    	// DocBase:	disk location that contains the app content. It can be a
    	// temporary or empty location if servlets are registered 
    	// programmatically (no web.xml). In this case, use addContext instead
    	// of addWebapp. (addWebapp parses web.xml)
    	
    	// null ContextPath - will use the tomcat default, tomcat.PORT
    	Context collaboratorDynamicApp = tomcat.addContext("/cd", null);
    	Tomcat.addServlet(collaboratorDynamicApp, "collaboratorDynamic", new DynamicCollaboratorServlet());
    	// "/" makes it the default servlet
    	// "" just matches the root
    	// "*.do" matches the extension - need to use the wildcard API
    	collaboratorDynamicApp.addServletMappingDecoded("", "collaboratorDynamic");
    	
    	// In this example 'static' is relative to the app working directory
    	Context collaboratorStaticApp = tomcat.addWebapp("/cs", new File("static").getAbsolutePath());
    	
    	tomcat.getConnector();
    	tomcat.start();
    	tomcat.getServer().await();
    	
    }
}
