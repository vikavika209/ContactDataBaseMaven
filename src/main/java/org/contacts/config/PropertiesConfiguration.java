package org.contacts.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath: jdbc.properties")
public class PropertiesConfiguration {
}
