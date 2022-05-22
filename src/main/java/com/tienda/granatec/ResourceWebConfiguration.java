package com.tienda.granatec;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/*Esta clase tiene la funcion de mandar datos a la parte cliente */
@Configuration
public class ResourceWebConfiguration implements WebMvcConfigurer{
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/images/**").addResourceLocations("file:images/");//apunta al directorio donde se encuentra las img
	}
}
