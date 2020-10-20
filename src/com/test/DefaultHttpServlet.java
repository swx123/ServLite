package com.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultHttpServlet extends HttpServlet {

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		SimpleHttpServletResponse sresp = (SimpleHttpServletResponse) resp;
		OutputStream os = sresp.getOs();
		
		byte[] res = response(uri);
		if(res != null) {
			os.write(res);
			os.flush();
		}
	}
	
	private byte[] response(String resource) {
		File file = new File(ServerLite.dir + ServerLite.sep + resource);
		if(!file.exists()) {
			System.out.println(resource + "²»´æÔÚ");
			return null;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("HTTP/1.1 200 OK\n");
		sb.append("cache-control: max-age=" + Integer.MAX_VALUE + "\n");
		String mime = null;
		try {
			mime = Files.probeContentType(file.toPath());
			if(mime == null) {
				mime = "application/octet-stream";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb.append("Content-type: " + mime + '\n');
		sb.append("Content-Length: " + file.length() + '\n');
		sb.append('\n');
		byte[] head = sb.toString().getBytes();
		byte[] res = new byte[(int) (head.length + file.length())];
		System.arraycopy(head, 0, res, 0, head.length);
		
		try {
			FileInputStream fis = new FileInputStream(file);
			fis.read(res, head.length, fis.available());
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
}
