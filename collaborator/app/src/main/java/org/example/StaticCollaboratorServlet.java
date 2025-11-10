package org.example;

import java.io.IOException;

// Note. Tomcat 10+: Use jakarta, not javax
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class StaticCollaboratorServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html; encoding=UTF-8");
		resp.getWriter().println("Collaborator Static");
	}
}
