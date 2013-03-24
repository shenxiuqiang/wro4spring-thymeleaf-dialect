package org.sevensource.wro4spring.thymeleaf.dialect.processor;

import org.sevensource.wro4spring.thymeleaf.Wro4jDialectConfiguration;
import org.sevensource.wro4spring.wro4j.WroContextSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.processor.AttributeNameProcessorMatcher;

import ro.isdc.wro.model.resource.ResourceType;


public class Wro4jStyleAttrProcessor extends AbstractWro4jAttrProcessor {

	private final static Logger logger = LoggerFactory.getLogger(Wro4jStyleAttrProcessor.class);

	public static final String ATTR_NAME = "style";
	public static final String ELEM_NAME = "link";

	public Wro4jStyleAttrProcessor(Wro4jDialectConfiguration wro4jDialectConfiguration, WroContextSupport wroContextSupport) {
		super(wro4jDialectConfiguration, wroContextSupport, new AttributeNameProcessorMatcher(ATTR_NAME, ELEM_NAME));
	}

	@Override
	protected String getRenderedAttribute(String uri, boolean isDevelopment) {
		return "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + uri + "\"/>\n";
		
	}

	@Override
	protected ResourceType getWro4jResourceType() {
		return ResourceType.CSS;
	}
}