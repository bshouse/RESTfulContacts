package org.bshouse.wsdb.stripes.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.bshouse.wsdb.common.ResolutionUrl;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.HttpCache;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

@UrlBinding("/")
@HttpCache(allow=false)
public class RootAction extends BaseAction {

	@DefaultHandler
	public Resolution home() {
		final String URI = getContext().getRequest().getRequestURI();
		
		if(URI.endsWith(".js")) {
			try {
				return new StreamingResolution("text/javascript",new FileReader("./"+URI));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else if(URI.endsWith(".css")) {
			try {
				return new StreamingResolution("text/css",new FileReader("./"+URI));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else if(URI.endsWith(".jsp")) {
			return new StreamingResolution(URI);

		} else if(URI.endsWith(".png")) {
			try {
				return new StreamingResolution("image/png") {
					protected void stream(HttpServletResponse response) throws Exception {
						FileInputStream fr = new FileInputStream(new File("./"+URI));
						OutputStream os = response.getOutputStream();
						IOUtils.copy(fr,os);
						IOUtils.closeQuietly(fr);
						IOUtils.closeQuietly(os);
				     }
				};
						
						//,new FileReader("./"+URI));
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if(URI.equals("/")) {
			return ResolutionUrl.INDEX_JSP;
			
		}
		return new ErrorResolution(HttpServletResponse.SC_NOT_FOUND);

		
	}
}
