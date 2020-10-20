package com.test;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerLite {
	
	static {
		urlServletMap = new HashMap<String, String>();
		
		classLoader = new ClassLoader() {
			@Override
			protected Class<?> findClass(String name) throws ClassNotFoundException {
				try {
					String cname = name.replace('.', ServerLite.sep);
					String path = ServerLite.dir + ServerLite.sep + "servlets" + ServerLite.sep + cname;
					FileInputStream fis = new FileInputStream(path);
					byte[] bytes = new byte[fis.available()];
					fis.read(bytes);
					fis.close();
					return defineClass(name, bytes, 0, bytes.length);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		};
	}
	public final static String dir = System.getProperty("user.dir");
	public final static char sep = File.separatorChar;
	public static Map<String, String> urlServletMap;
	public static ClassLoader classLoader;
	
	public ServerLite() {
		
	}
	
	public void start() {
		try {
			ServerSocket serverSocket = new ServerSocket(9000);
			ExecutorService threadPool = Executors.newFixedThreadPool(20);
			
			threadPool.execute(new Runnable() {
				
				@Override
				public void run() {
					try {
						Desktop.getDesktop().browse(URI.create("http://localhost:9000/index.html"));
					} catch (IOException e) {
						
					}
				}
			});
			
			System.out.println("Server Start!");
			while(true) {
				Socket socket = serverSocket.accept();
				threadPool.execute(new Worker(socket));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new ServerLite().start();
	}
}
