package org.example;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Note. This is redundant in embedded Tomcat scenarios but allows automatic 
// detection in WAR deployments
@WebServlet("")
public class DynamicCollaboratorServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/xml; encoding=UTF-8");
		resp.getWriter().println("<version>1.0.0</version>");
	}

}
