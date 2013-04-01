package org.sevensource.wro4spring.thymeleaf.dialect.processor;

import org.sevensource.wro4spring.WroContextSupport;
import org.sevensource.wro4spring.WroDeliveryConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.processor.AttributeNameProcessorMatcher;

import ro.isdc.wro.model.resource.ResourceType;


public class Wro4jScriptAttrProcessor extends AbstractWro4jAttrProcessor {

	private final static Logger logger = LoggerFactory.getLogger(Wro4jScriptAttrProcessor.class);

	public static final String ATTR_NAME = "script";
	private static final AttributeNameProcessorMatcher matcher = new AttributeNameProcessorMatcher(ATTR_NAME, ATTR_NAME);

	public Wro4jScriptAttrProcessor(WroDeliveryConfiguration wroDeliveryConfiguration, WroContextSupport wroContextSupport) {
		super(wroDeliveryConfiguration, wroContextSupport, matcher);
	}

	@Override
	protected String getRenderedAttribute(String uri, boolean isDevelopment) {
		return "<script src=\"" + uri + "\"></script>\n";
	}

	@Override
	protected ResourceType getWro4jResourceType() {
		return ResourceType.JS;
	}
}