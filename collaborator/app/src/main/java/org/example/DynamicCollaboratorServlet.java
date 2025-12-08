package org.example;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Note. This is redundant in embedded Tomcat scenarios but allows automatic 
// detection in WAR deployments
@WebServlet("")
public class DynamicCollaboratorServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Map<String, String> pageDb = new HashMap<>();
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		if ("/mq.js".compareTo(req.getServletPath()) == 0) {
			resp.setContentType("text/javascript; encoding=UTF-8");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ServletOutputStream out = resp.getOutputStream();
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream("static/mq.js"));
			int val = 0;
			int size = 0;
			while ((val = bis.read()) != -1) {
				 bos.write(val);
				 size++;
			}
			resp.setContentLength(size);
			bos.writeTo(out);
		}
		if ("/mq".compareTo(req.getServletPath()) == 0) {
			String pageName = req.getParameter("page");
			String contents = req.getParameter("contents");
			String key = req.getParameter("key");
			String dbkey = Pattern.compile("[.\\/]").matcher(key).replaceAll(m -> "-")
					+ "." + pageName.replace('.', '_').replace('/', '.');
			if (key != "" && !pageDb.containsKey(dbkey)) {
				pageDb.put(dbkey, contents);
				System.out.println("dbkey:" + dbkey + "; contents: " + contents);
			} else {

			}
		}
		if ("/mq.dump".compareTo(req.getServletPath()) == 0) {
			for (Map.Entry<String, String> e : pageDb.entrySet()) {
				System.out.println(e.getKey() + ":" + e.getValue());
			}
		}
	}

}
