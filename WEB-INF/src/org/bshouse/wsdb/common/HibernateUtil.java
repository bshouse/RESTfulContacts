package org.bshouse.wsdb.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

	private static SessionFactory sessionFactory;
	
	private static HibernateUtil instance = null;
	
	private HibernateUtil() {
		
	}
	
	public static HibernateUtil getInstance() {
		if(instance == null) {
			instance = new HibernateUtil();
		}
		return instance;
	}
	
	public static SessionFactory getSessionFactory() {
		if(sessionFactory == null) {
			Configuration config = new Configuration();
			config.configure();
			String db = System.getenv(Constants.DB_ENV_VAR);
			
			//Determine if the Production DB was requested. 
			if(!Constants.DB_PROD.equals(db)) {
				//Test
				config.setProperty("hibernate.connection.url", config.getProperty("hibernate.connection.url")+Constants.DB_TEST);
				config.setProperty("hibernate.default_schema",Constants.DB_TEST);
			} else {
				//Production
				config.setProperty("hibernate.connection.url", config.getProperty("hibernate.connection.url")+Constants.DB_PROD);
				config.setProperty("hibernate.default_schema",Constants.DB_PROD);
			}
			sessionFactory = config.buildSessionFactory(
					new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build());
		}
		return sessionFactory;
	}
	
	public static Session getSession() {
		if(sessionFactory == null) {
			return getSessionFactory().openSession();
		}
		return sessionFactory.openSession();
	}
	
	public static <T> List<T> castList(Class<? extends T> clazz, Collection<?> untypedList) {
		List<T> typedList = new ArrayList<T>(untypedList.size());
		for(Object o: untypedList) {
			typedList.add(clazz.cast(o));
		}
		return typedList;
	}
}
