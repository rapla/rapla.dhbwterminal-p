package org.rapla.plugin.dhbwterminal;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.rapla.components.util.IOUtil;
import org.rapla.entities.configuration.RaplaConfiguration;
import org.rapla.facade.RaplaComponent;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.framework.RaplaLocale;
import org.rapla.servletpages.RaplaPageGenerator;

public class SteleKursUebersichtPageGenerator extends RaplaComponent implements RaplaPageGenerator, TerminalConstants {

	
	public SteleKursUebersichtPageGenerator(RaplaContext context) throws RaplaException {
		super(context);
	}

	public void generatePage(ServletContext context,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException 
	{
		
		RaplaLocale raplaLocale = getRaplaLocale();
		response.setContentType("text/html; charset=" + raplaLocale.getCharsetNonUtf() );
		java.io.PrintWriter out = response.getWriter();
		try 
		{

	        //		response.setContentType("content-type text/html charset=utf-8");
	        RaplaConfiguration raplaConfig  = (RaplaConfiguration)getQuery().getPreferences(null).getEntry("org.rapla.plugin");
	        Configuration pluginConfig = null;
            if ( raplaConfig != null) {
                pluginConfig = raplaConfig.find("class", TerminalPlugin.class.getName());
            }
            if ( pluginConfig == null) {
            	pluginConfig = new DefaultConfiguration("plugin");
            } 
                
	        String title = pluginConfig.getChild( "ueberschrift").getValue(TerminalConstants.KURS_UEBERSCHRIFT);
	        String no_courses = pluginConfig.getChild( "keinekurse").getValue(TerminalConstants.NO_COURSES);
			
	        out.println("<html>");
			out.println("<head>");
			out.println("  <title>" + title + "</title>");
	
			out.println("  <link REL=\"stylesheet\" href=\"rapla?page=resource&name=kursuebersicht.css\" type=\"text/css\">");
			URL cssFile = context.getResource(TerminalConstants.CUSTOM_CSS_FILE_PATH);
			if ( cssFile != null)
			{
				out.println("  <link REL=\"stylesheet\" href=\""+ TerminalConstants.CUSTOM_CSS_FILE_PATH + "\" type=\"text/css\">");
			}
			out.println("  <link REL=\"stylesheet\" href=\"default.css\" type=\"text/css\">");
			// tell the html page where its favourite icon is stored
			out.println("    <link REL=\"shortcut icon\" type=\"image/x-icon\" href=\"/images/favicon.ico\">");
			out.println("  <meta HTTP-EQUIV=\"Content-Type\" content=\"text/html; charset=" + raplaLocale.getCharsetNonUtf() + "\">");
			out.println("</head>");
			out.println("<body>");
			out.println("  <h1 class=\"title\">" + title + "</h1>");
			out.println("<marquee scrollamount=\"1\" scrolldelay=\"1\" direction=\"up\" >");
			CourseExporter allocatableExporter;
			allocatableExporter = new CourseExporter( raplaLocale, getClientFacade());
			BufferedWriter buf = new BufferedWriter(out);
		
			allocatableExporter.printKurseAmTag( buf, no_courses);
			buf.append("</marquee>");
			buf.append("</body>");
			buf.append("</html>");
			buf.close();
		} 
		catch (RaplaException ex) {
			out.println( IOUtil.getStackTraceAsString( ex ) );
			throw new ServletException( ex );
		}
		
		out.close();
	}

}
