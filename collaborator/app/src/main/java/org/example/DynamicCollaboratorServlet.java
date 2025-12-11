package org.example;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

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
	private Map<String, byte[]> pageDb = new HashMap<>();
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		resp.setHeader("Access-Control-Allow-Origin", "*");

		if ("/mq.js".compareTo(req.getServletPath()) == 0) {
			resp.setContentType("text/javascript; encoding=UTF-8");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ServletOutputStream out = resp.getOutputStream();
			int size = 0;
			try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream("static/mq.js"))) {
				int val = 0;
				while ((val = bis.read()) != -1) {
					 bos.write(val);
					 size++;
				}
			}
			resp.setContentLength(size);
			bos.writeTo(out);
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		if ("/mq".compareTo(req.getServletPath()) == 0) {
			String pageName = req.getParameter("page");
			byte[] contents = req.getParameter("contents").getBytes(StandardCharsets.UTF_8);
			String key = req.getParameter("key");
			if (pageName.length() > 7 && pageName.substring(0, 7).compareTo("http://") == 0) {
				pageName = pageName.substring(7);
			} else if (pageName.length() > 8 && pageName.substring(0, 8).compareTo("https://") == 0) {
				pageName = pageName.substring(8);
			}
			String dbkey = Pattern.compile("[.\\/]").matcher(key).replaceAll(m -> "-")
					+ "." + pageName.replace('.', '_').replace('/', '.');
			if (key != "" && !pageDb.containsKey(dbkey)) {
				pageDb.put(dbkey, contents);
				System.out.println("putting dbkey:" + dbkey);
			} else {

			}
		}

		if ("/mq.dump".compareTo(req.getServletPath()) == 0) {
			String tmpdir = System.getProperty("java.io.tmpdir");
			for (Map.Entry<String, byte[]> e : pageDb.entrySet()) {
				String[] parts = e.getKey().split("\\.");
				Path dest = Path.of(tmpdir);
				for (String p : parts) {
					if (!p.isEmpty()) {
						dest = dest.resolve(p);
					}
				}
				try {
					Files.createDirectories(dest);
				} catch (IOException ex) {
					ex.printStackTrace();
					continue;
				}
				byte[] contents = e.getValue();
				CRC32 crc = new CRC32();
				crc.update(contents);
				dest = dest.resolve(Long.toString(crc.getValue()));
				if (!Files.exists(dest)) {
					try (FileOutputStream fos = new FileOutputStream(dest.toFile())) {
						fos.write(contents);
						System.out.println("File " + dest + " written " + contents.length + " bytes");
					} catch (IOException ex){
						ex.printStackTrace();
					}
				}
			}
		}
	}

}
