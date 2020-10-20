package com.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Worker implements Runnable {
	
	private Socket socket;
	
	public Worker(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			
			HttpServletRequest request = new SimpleHttpServletRequest(is);
			HttpServletResponse response = new SimpleHttpServletResponse(os);
			
			String uri = request.getRequestURI();
			HttpServlet servlet = null;
			for(String key : ServerLite.urlServletMap.keySet()) {
				if(uri.startsWith(key)) {
					try {
						Class<?> loadClass = ServerLite.classLoader.loadClass(ServerLite.urlServletMap.get(key));
						servlet = (HttpServlet) loadClass.newInstance();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
			if(servlet == null) {
				servlet = new DefaultHttpServlet();
			}
			
			servlet.service(request, response);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				socket.shutdownInput();
				socket.shutdownOutput();
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
}
