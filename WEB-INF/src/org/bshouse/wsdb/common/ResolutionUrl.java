package org.bshouse.wsdb.common;

import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;

public class ResolutionUrl {

	public static final Resolution INDEX = new RedirectResolution("/index.jsp");
	public static final Resolution INDEX_JSP = new ForwardResolution("/WEB-INF/jsp/index.jsp");
	
}
