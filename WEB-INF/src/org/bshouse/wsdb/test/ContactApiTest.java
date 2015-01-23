package org.bshouse.wsdb.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.bshouse.wsdb.beans.Contact;
import org.bshouse.wsdb.common.Constants;
import org.bshouse.wsdb.common.HibernateUtil;
import org.bshouse.wsdb.server.Servers;
import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ContactApiTest {

	private Session db = HibernateUtil.getSession();
	private static Servers wsdb;
	private static String baseUrl = new String("http://"+Constants.SERVER_IP+":"+Constants.WEBSERVER_PORT);
	
	@BeforeClass
	public static void startServers() {
		wsdb = new Servers();
		wsdb.start();
	}
	
	@AfterClass
	public static void stopServers() {
		wsdb.stop();
	}
	
	private void cleanDb() {
		
		db.createSQLQuery("delete from contact").executeUpdate();
		db.flush();
	}
	
	private void addContact(String fname) {
		Contact c = new Contact();
		c.setNameFirst(fname);
		db.save(c);
	}
	
	@Test
	public void testContactList() throws ClientProtocolException, IOException {
		cleanDb();
		HttpClient httpclient = HttpClients.createDefault();
		HttpGet get = new HttpGet(baseUrl+"/api/1.0/contact");
		HttpResponse hr = httpclient.execute(get);
		String content = IOUtils.toString(hr.getEntity().getContent());
		System.out.println("1. testContactList: "+content);
		assertTrue(content.equals("{\"message\":[],\"success\":true}"));
		
		
		addContact("Bill");
		addContact("Bob");
		db.flush();
		get = new HttpGet(baseUrl+"/api/1.0/contact");
		hr = httpclient.execute(get);
		content = IOUtils.toString(hr.getEntity().getContent());
		System.out.println("2. testContactList: "+content);
		assertTrue(content.endsWith(",\"success\":true}"));
	}
	
	@Test
	public void testContactAdd() throws ClientProtocolException, IOException {
		cleanDb();
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost post = new HttpPost(baseUrl+"/api/1.0/contact");

		List<NameValuePair> nvpl = new ArrayList<NameValuePair>(5);
		nvpl.add(new BasicNameValuePair("contact.id","-1"));
		nvpl.add(new BasicNameValuePair("contact.nameFirst","First"));
		nvpl.add(new BasicNameValuePair("contact.nameLast","Last"));
		nvpl.add(new BasicNameValuePair("contact.numberCell","1-303-555-1212"));
		nvpl.add(new BasicNameValuePair("contact.email","user@mail.com"));
		nvpl.add(new BasicNameValuePair("contact.bday","10/31/2000"));
		
		post.setEntity(new UrlEncodedFormEntity(nvpl));
		HttpResponse hr = httpclient.execute(post);
		String content = IOUtils.toString(hr.getEntity().getContent());
		System.out.println("testAddContact: "+content);
		assertTrue(content.endsWith(",\"success\":true}"));
	}
	
	@Test
	public void testContactAddOverflow() throws ClientProtocolException, IOException {
		cleanDb();
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost post = new HttpPost(baseUrl+"/api/1.0/contact");

		List<NameValuePair> nvpl = new ArrayList<NameValuePair>(5);
		nvpl.add(new BasicNameValuePair("contact.id","-1"));
		nvpl.add(new BasicNameValuePair("contact.nameFirst","First"));
		nvpl.add(new BasicNameValuePair("contact.nameLast","Last"));
		nvpl.add(new BasicNameValuePair("contact.numberCell","1-303-555-121234sdfgsdfg5wegsdgf434534534534534"));
		nvpl.add(new BasicNameValuePair("contact.email","user@mail.com"));
		
		post.setEntity(new UrlEncodedFormEntity(nvpl));
		HttpResponse hr = httpclient.execute(post);
		String content = IOUtils.toString(hr.getEntity().getContent());
		System.out.println("testAddOverflowContact: "+content);
		assertTrue(content.equals("{\"message\":\"\\nCellphone number must not exceed 30 characters.\",\"success\":false}"));
	}
	
	@Test
	public void testContactAddBadDateFormat() throws ClientProtocolException, IOException {
		//cleanDb();
		
		//Invalid date format
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost post = new HttpPost(baseUrl+"/api/1.0/contact");

		List<NameValuePair> nvpl = new ArrayList<NameValuePair>(5);
		nvpl.add(new BasicNameValuePair("contact.id","-1"));
		nvpl.add(new BasicNameValuePair("contact.nameFirst","First"));
		nvpl.add(new BasicNameValuePair("contact.nameLast","Last"));
		nvpl.add(new BasicNameValuePair("contact.numberCell","1-303-555-1212"));
		nvpl.add(new BasicNameValuePair("contact.email","user@mail.com"));
		nvpl.add(new BasicNameValuePair("contact.bday","2000/10/31"));
		
		post.setEntity(new UrlEncodedFormEntity(nvpl));
		HttpResponse hr = httpclient.execute(post);
		String content = IOUtils.toString(hr.getEntity().getContent());
		System.out.println("testAddBadDateContact: "+content);
		assertTrue(content.endsWith("{\"message\":\"\\nBirthday must be a valid date formatted like MM/DD/YYYY.\",\"success\":false}"));
		
		
		//Invalid Date
		nvpl = new ArrayList<NameValuePair>(5);
		nvpl.add(new BasicNameValuePair("contact.id","-1"));
		nvpl.add(new BasicNameValuePair("contact.nameFirst","First"));
		nvpl.add(new BasicNameValuePair("contact.nameLast","Last"));
		nvpl.add(new BasicNameValuePair("contact.numberCell","1-303-555-1212"));
		nvpl.add(new BasicNameValuePair("contact.email","user@mail.com"));
		nvpl.add(new BasicNameValuePair("contact.bday","10/32/2000"));
		
		post.setEntity(new UrlEncodedFormEntity(nvpl));
		hr = httpclient.execute(post);
		content = IOUtils.toString(hr.getEntity().getContent());
		System.out.println("testAddBadDateContact: "+content);
		assertTrue(content.endsWith("{\"message\":\"\\nBirthday must be a valid date formatted like MM/DD/YYYY.\",\"success\":false}"));
	}
	
	
	@Test
	public void testContactAddBadDate() throws ClientProtocolException, IOException {
		//cleanDb();
		
		//Invalid date format
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost post = new HttpPost(baseUrl+"/api/1.0/contact");

		//Invalid Date
		List<NameValuePair> nvpl = new ArrayList<NameValuePair>(5);
		nvpl.add(new BasicNameValuePair("contact.id","-1"));
		nvpl.add(new BasicNameValuePair("contact.nameFirst","First"));
		nvpl.add(new BasicNameValuePair("contact.nameLast","Last"));
		nvpl.add(new BasicNameValuePair("contact.numberCell","1-303-555-1212"));
		nvpl.add(new BasicNameValuePair("contact.email","user@mail.com"));
		nvpl.add(new BasicNameValuePair("contact.bday","10/32/2000"));
		
		post.setEntity(new UrlEncodedFormEntity(nvpl));
		HttpResponse hr = httpclient.execute(post);
		String content = IOUtils.toString(hr.getEntity().getContent());
		System.out.println("testAddBadDateContact: "+content);
		assertTrue(content.endsWith("{\"message\":\"\\nBirthday must be a valid date formatted like MM/DD/YYYY.\",\"success\":false}"));
	}

	
	@Test
	public void testContactUpdate() throws ClientProtocolException, IOException, URISyntaxException {
		cleanDb();
		
		//Add a contact to edit
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost post = new HttpPost(baseUrl+"/api/1.0/contact");

		List<NameValuePair> nvpl = new ArrayList<NameValuePair>(5);
		nvpl.add(new BasicNameValuePair("contact.id","-1"));
		nvpl.add(new BasicNameValuePair("contact.nameFirst","First"));
		nvpl.add(new BasicNameValuePair("contact.nameLast","Last"));
		nvpl.add(new BasicNameValuePair("contact.numberCell","1-303-555-1212"));
		nvpl.add(new BasicNameValuePair("contact.email","user@mail.com"));
		
		post.setEntity(new UrlEncodedFormEntity(nvpl));
		HttpResponse hr = httpclient.execute(post);
		String content = IOUtils.toString(hr.getEntity().getContent());
		System.out.println("testUpdateContact(Add): "+content);
		assertTrue(content.endsWith(",\"success\":true}"));
		
		//Parse the ID
		String id = StringUtils.substringBetween(content,"\"id\":", ",");

		System.out.println("ID: "+id);
		
		//Edit contact
		HttpPut put = new HttpPut(baseUrl+"/api/1.0/contact/"+id);
		List<NameValuePair> unvpl = new ArrayList<NameValuePair>(5);
		unvpl.add(new BasicNameValuePair("contact.id",id));
		unvpl.add(new BasicNameValuePair("contact.nameFirst","First"));
		unvpl.add(new BasicNameValuePair("contact.nameLast","Last"));
		unvpl.add(new BasicNameValuePair("contact.numberCell","1-303-555-1212"));
		unvpl.add(new BasicNameValuePair("contact.email","email@mail.com"));
		put.setEntity(new UrlEncodedFormEntity(unvpl));
		hr = httpclient.execute(put);
		content = IOUtils.toString(hr.getEntity().getContent());
		System.out.println("testUpdateContact: "+content);
		assertTrue(content.endsWith(",\"success\":true}") && content.indexOf("email@mail.com") > 0);
	}
	
	@Test
	public void testContactDelete() throws ClientProtocolException, IOException {
		cleanDb();
		
		//Add a contact to edit
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost post = new HttpPost(baseUrl+"/api/1.0/contact");

		List<NameValuePair> nvpl = new ArrayList<NameValuePair>(5);
		nvpl.add(new BasicNameValuePair("contact.id","-1"));
		nvpl.add(new BasicNameValuePair("contact.nameFirst","First"));
		nvpl.add(new BasicNameValuePair("contact.nameLast","Last"));
		nvpl.add(new BasicNameValuePair("contact.numberCell","1-303-555-1212"));
		nvpl.add(new BasicNameValuePair("contact.email","user@mail.com"));
		
		post.setEntity(new UrlEncodedFormEntity(nvpl));
		HttpResponse hr = httpclient.execute(post);
		String content = IOUtils.toString(hr.getEntity().getContent());
		System.out.println("testDeleteContact(Add): "+content);
		assertTrue(content.endsWith(",\"success\":true}"));
		
		//Parse the ID
		String id = StringUtils.substringBetween(content,"\"id\":", ",");

		System.out.println("ID: "+id);
		
		//Delete contact
		HttpDelete del = new HttpDelete(baseUrl+"/api/1.0/contact/"+id);
		hr = httpclient.execute(del);
		content = IOUtils.toString(hr.getEntity().getContent());
		System.out.println("testDeleteContact: "+content);
		assertTrue(content.endsWith(",\"success\":true}"));
	}

	@Test
	public void testContactDeleteInvalid() throws ClientProtocolException, IOException {
		cleanDb();
		HttpClient httpclient = HttpClients.createDefault();
		//Delete contact
		HttpDelete del = new HttpDelete(baseUrl+"/api/1.0/contact/abc123");
		HttpResponse hr = httpclient.execute(del);
		String content = IOUtils.toString(hr.getEntity().getContent());
		System.out.println("testDeleteInvalidContact: "+content);
		assertTrue(content.equals("{\"message\":\"Delete failed because required data is missing\",\"success\":false}"));
	}
	
}
