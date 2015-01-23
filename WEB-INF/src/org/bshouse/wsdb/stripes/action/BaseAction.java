package org.bshouse.wsdb.stripes.action;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;

public class BaseAction implements ActionBean {

	ActionBeanContext cntx = null;
	
	
	@Override
	public ActionBeanContext getContext() {
		return cntx;
	}

	@Override
	public void setContext(ActionBeanContext context) {
		cntx = context;
		
	}
	

}
