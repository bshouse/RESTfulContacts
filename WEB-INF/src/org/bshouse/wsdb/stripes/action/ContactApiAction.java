package org.bshouse.wsdb.stripes.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bshouse.wsdb.beans.Contact;
import org.bshouse.wsdb.common.Constants;
import org.bshouse.wsdb.common.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.google.gson.Gson;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.HttpCache;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

@UrlBinding("/api/1.0/contact/{id}")
@HttpCache(allow=false)
public class ContactApiAction extends BaseAction {

	private Session db = HibernateUtil.getSession();
	private Gson g = new Gson();
	private Map<String,Object> json = new HashMap<String,Object>();
	private String id = Constants.BLANK_STRING;
	private Contact contact;
	
	
	@DefaultHandler
	public Resolution rest() {
		String method = getContext().getRequest().getMethod().toUpperCase();
System.out.println("Start ContactApiAction rest("+method+")");
		if("GET".equals(method)) { //Lookup Contact
			list();
		} else if("POST".equals(method)) { //Add a new contact
			add();
		} else if("PUT".equals(method)) { //Update an existing contact
			update();
		} else if("DELETE".equals(method)) { //Delete an existing contact
			delete();
		} else {
			json.put("success",false);
			json.put("message", "Unsupported method requested: "+method);
		}
		db.close();
System.out.println("Result ContactApiAction rest("+method+")");
		return new StreamingResolution("application/json",g.toJson(json));
	}
	
	
	private void list() {
System.out.println("list() started");		
		Criteria c = db.createCriteria(Contact.class);
		if(StringUtils.isNotBlank(id)) {
			//Load the requested contact
			c.add(Restrictions.eq("id", Long.parseLong(id)));
		}
System.out.println("list() criteria created");
		List<Contact> cl = HibernateUtil.castList(Contact.class, c.list());
System.out.println("list() contacts loaded");
		json.put("success",true);
		json.put("data", cl);
	}
	
	public void add() {
		if(contact != null) {
			if(contact.valid().length() == 0) {
				if(contact.getId() == -1L) {
					db.save(contact);
					json.put("success",true);
					json.put("data", contact);
				} else {
					json.put("success",false);
					json.put("message", "Contact already exists");
				}
			} else {
				json.put("success",false);
				json.put("message", contact.valid());
			}
			
		} else {
			json.put("success",false);
			json.put("message", "Add failed because required data is missing");
		}
	}
	
	public void update() {
		System.out.println("Contact: "+contact.getId() + " - "+id);
		if(contact != null && contact.getId().equals(Long.parseLong(id))) {
			if(contact.valid().length() == 0) {
				if(contact.getId() > -1L) {
					db.update(contact);
					json.put("success",true);
					json.put("data", contact);
				} else {
					json.put("success",false);
					json.put("message", "Contact does not exist");
				}
			} else {
				json.put("success",false);
				json.put("message", contact.valid());
			}
			
		} else {
			json.put("success",false);
			json.put("message", "Update failed because required data is missing");
		}
	}
	
	public void delete() {
System.out.println("delete() ID: "+id);
		if(StringUtils.isNotBlank(id) && StringUtils.isNumeric(id)) {
			Long cid = Long.parseLong(id);
			Criteria c = db.createCriteria(Contact.class);
			c.add(Restrictions.eq("id", cid));
			List<Contact> cl = HibernateUtil.castList(Contact.class, c.list());
			if(cl.size() == 1) {
				db.delete(cl.get(0));
				db.flush();
				json.put("success",true);
				json.put("message", "Contact deleted");
			} else {
				json.put("success",false);
				json.put("message", "Contact does not exist");
			}
			
		} else {
			json.put("success",false);
			json.put("message", "Delete failed because required data is missing");
		}
	}

	
	/*
	 * 
	 * Getters and Setters for request data
	 * 
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

}
