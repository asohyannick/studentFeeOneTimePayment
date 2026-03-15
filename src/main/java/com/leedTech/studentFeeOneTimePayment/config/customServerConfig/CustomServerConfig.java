package com.leedTech.studentFeeOneTimePayment.config.customServerConfig;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class CustomServerConfig {

			@Bean
			public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
				return factory -> factory.addConnectorCustomizers(connector -> {
					connector.setMaxParameterCount(200);
					connector.setMaxPartCount(200);
					connector.setMaxPostSize(52428800);
				});
			}
}