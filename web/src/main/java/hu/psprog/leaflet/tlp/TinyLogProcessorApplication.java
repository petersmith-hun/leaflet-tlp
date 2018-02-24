package hu.psprog.leaflet.tlp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

/**
 * Spring Boot entry point.
 *
 * @author Peter Smith
 */
@SpringBootApplication
public class TinyLogProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(TinyLogProcessorApplication.class, args);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer applicationConfigPropertySource() {

        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setLocation(new ClassPathResource("version.properties"));

        return configurer;
    }
}
