package utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.util.Map;
import java.io.StringWriter;
import java.io.Writer;
import java.io.IOException;
import java.io.File;

public class PageGenerator {

	private static final String HTML_DIR = "public_html";   //”казываем где хранитьс€ статичный контент
	private static final Configuration CFG = new Configuration();
	
	public static String getPage(String filename, Map<String, Object> data) {
		
		Writer stream = new StringWriter();
		try{
			Template template = CFG.getTemplate(HTML_DIR + File.separator + filename);
			template.process(data, stream);
		}
		catch(IOException|TemplateException e) {
			e.printStackTrace();
		}
		return stream.toString(); 
	}
}
