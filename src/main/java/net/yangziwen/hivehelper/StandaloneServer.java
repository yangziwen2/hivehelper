package net.yangziwen.hivehelper;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.security.ProtectionDomain;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.component.AbstractLifeCycle.AbstractLifeCycleListener;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.webapp.WebAppContext;

public class StandaloneServer {
	
	public static void main(String[] args) throws Exception {
		
		String contextPath = System.getProperty("contextpath", "/");
		if(!contextPath.startsWith("/")) {
			contextPath = "/" + contextPath;
		}
		final int port = Integer.getInteger(System.getProperty("port"), 8060);
		
		if(!isPortAvailable("0.0.0.0", port)) {
			System.out.println(String.format("当前端口[%d]已被占用，请按回车键退出。", port));
			new BufferedReader(new InputStreamReader(System.in)).readLine();
			System.exit(0);
		}

		Server server = createServer(contextPath, port);
		
		// server启动完成后打开浏览器
		server.addLifeCycleListener(new AbstractLifeCycleListener() {
			@Override
			public void lifeCycleStarted(LifeCycle event) {
				openApp(port);
			}
		});

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(100);
		}
	}

	private static Server createServer(String contextPath, int port) {
		// use Eclipse JDT compiler
		System.setProperty("org.apache.jasper.compiler.disablejsr199", "true");

		Server server = new Server(port);
		server.setStopAtShutdown(true);

		ProtectionDomain protectionDomain = Server.class.getProtectionDomain();
		URL location = protectionDomain.getCodeSource().getLocation();

		String warFile = location.toExternalForm();
		WebAppContext context = new WebAppContext(warFile, contextPath);
		context.setServer(server);

		// 设置work dir,war包将解压到该目录，jsp编译后的文件也将放入其中。
		String currentDir = new File(location.getPath()).getParent();
		File workDir = new File(currentDir, "work");
		context.setTempDirectory(workDir);

		server.setHandler(context);
		return server;
	}
	
	private static void openApp(int port) {
		String url = "http://localhost:" + port;
		Boolean autoOpen = false;
		try {
			autoOpen = Boolean.valueOf(System.getProperty("autoOpen"));
		} catch (Exception e) {
		}
		if(!Boolean.TRUE.equals(autoOpen)) {
			return;
		}
		String cmd = "cmd /c start " + url;
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static boolean isPortAvailable(String host, int port) {
		Socket s = new Socket();
		try {
			s.bind(new InetSocketAddress(host, port));
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			if(s != null) {
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
