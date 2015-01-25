package org.bshouse.wsdb.server;

import java.io.File;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.bshouse.wsdb.common.Settings;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.hsqldb.persist.HsqlProperties;

public class Servers {

	private HsqlProperties hp;
	private org.hsqldb.Server server = null;
	private Server s = null;

	
	public void start() {
		Settings.loadAppProperties(getClass().getClassLoader().getResourceAsStream("app.properties"));
		
		startDb();
		s = new Server();
		//Create an HTTP server that runs on port 80
		
		ServerConnector sc = new ServerConnector(s);
		if(StringUtils.isNotBlank(Settings.getWebserverIpAddress())) {
			sc.setHost(Settings.getWebserverIpAddress());
		}
		if(StringUtils.isNotBlank(Settings.getWebserverPortHttp())) {
			sc.setPort(Integer.parseInt(Settings.getWebserverPortHttp()));
		}
		s.setConnectors(new Connector[] { sc });
		
		
		//Add a WebApp
		WebAppContext wac = new WebAppContext();
		wac.setDescriptor("WEB-INF/web.xml");
		wac.setResourceBase(".");
		wac.setContextPath("/");
		wac.setParentLoaderPriority(true);
		
		
		s.setHandler(wac);
		
		try {
			s.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void join() {
		if(s == null) {
			return;
		}
		//Start the server running
		try {
			s.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		try {
			if(s != null) {
				s.stop();
			}
			if(server != null) {
				server.stop();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void startDb() {
		File dbDir = new File("WEB-INF/db/dummy");
		Properties p = new Properties();
		p.put("server.address", Settings.getDatabaseIpAddress());
		p.put("server.port", Settings.getDatabasePort());
		p.put("server.database.0","file:"+dbDir.getAbsolutePath()+
				";hsqldb.sqllog=3;sql.enforce_names=true;user=dummy;password=dp4hsdb");
		p.put("server.dbname.0", "dummy");
		p.put("server.database.1","file:"+dbDir.getAbsolutePath()+
				";hsqldb.sqllog=3;sql.enforce_names=true;user=dummy;password=dp4hsdb");
		p.put("server.dbname.1", "prod");
		p.put("server.silent","false");
		p.put("server.trace", "true");
		p.put("server.no_system_exit", "true");
		p.put("server.remote_open", "false");
		p.put("server.acl", "WEB-INF/src/properties/server.acl");
		
		hp = new HsqlProperties(p);
		
		server = new org.hsqldb.Server();
		try {
			server.setProperties(hp);
			server.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Servers s = new Servers();
		s.start();
		s.join();
	}


}
