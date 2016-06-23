package net.minepay.mcapi.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Configures Spring's web component to fit in with the application requirements.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    /**
     * {@inheritDoc}
     */
    @Override
    public void configureMessageConverters(@Nonnull List<HttpMessageConverter<?>> converters) {
        {
            MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
            converter.setPrettyPrint(true);
            converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
        }

        {
            MappingJackson2XmlHttpMessageConverter converter = new MappingJackson2XmlHttpMessageConverter();
            converter.setPrettyPrint(true);
            converter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_XML));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configureContentNegotiation(@Nonnull ContentNegotiationConfigurer configurer) {
        configurer
                .defaultContentType(MediaType.APPLICATION_JSON_UTF8)
                .mediaType("json", MediaType.APPLICATION_JSON_UTF8)
                .mediaType("xml", MediaType.TEXT_XML)
                .ignoreAcceptHeader(true) // make Night happy
                .favorParameter(false)
                .favorPathExtension(true)
                .ignoreUnknownPathExtensions(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCorsMappings(@Nonnull CorsRegistry registry) {
        registry.addMapping("**")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedOrigins("*");
    }
}
