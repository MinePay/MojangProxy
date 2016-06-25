package net.minepay.mcapi.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
@EnableAsync
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
    @Value("${executor.base-pool-size:2}")
    private int basePoolSize;
    @Value("${executor.max-pool-size:12}")
    private int maxPoolSize;
    @Value("${executor.keep-alive:3}")
    private int keepAliveSize;

    /**
     * Provides a pooled task executor.
     * @return an executor.
     */
    @Bean
    @Nonnull
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(this.basePoolSize);
        executor.setMaxPoolSize(this.maxPoolSize);
        executor.setKeepAliveSeconds(this.keepAliveSize);
        return executor;
    }

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
