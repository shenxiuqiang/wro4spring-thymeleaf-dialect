package org.sevensource.wro4spring.thymeleaf.dialect.processor;

import org.sevensource.wro4spring.WroContextSupport;
import org.sevensource.wro4spring.WroDeliveryConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.processor.AttributeNameProcessorMatcher;

import ro.isdc.wro.model.resource.ResourceType;


public class Wro4jStyleAttrProcessor extends AbstractWro4jAttrProcessor {

	private final static Logger logger = LoggerFactory.getLogger(Wro4jStyleAttrProcessor.class);

	public static final String ATTR_NAME = "style";
	public static final String ELEM_NAME = "link";
	private final static AttributeNameProcessorMatcher matcher = new AttributeNameProcessorMatcher(ATTR_NAME, ELEM_NAME);

	public Wro4jStyleAttrProcessor(WroDeliveryConfiguration wroDeliveryConfiguration, WroContextSupport wroContextSupport) {
		super(wroDeliveryConfiguration, wroContextSupport, matcher);
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