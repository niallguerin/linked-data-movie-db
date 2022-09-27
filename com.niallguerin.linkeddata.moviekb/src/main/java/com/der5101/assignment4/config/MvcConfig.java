package com.der5101.assignment4.config;

//Spring Framework imports
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan
//Spring Boot Guide Reference
//http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-web-applications.html#boot-features-spring-mvc-auto-configuration
public class MvcConfig extends WebMvcConfigurerAdapter 
{

 public MvcConfig() 
 {
     super();
 }

 // The addViewController allows automatic HTTP GET request mapping WITHOUT needing an explicit Controller.
 // Do not forget to include entries here for http redirects to Views that have no explicit Controller OR
 // to allow a user directly access e.g. the home client page at http://localhost:8080/client

 @Override
 public void addViewControllers(final ViewControllerRegistry registry) 
 {
     super.addViewControllers(registry);
     // See full reply by Dave Syer (Spring Boot major developer contributor)
     // http://stackoverflow.com/questions/27381781/java-spring-boot-how-to-map-my-app-root-to-index-html
     registry.addViewController("/client").setViewName("client");
     registry.addViewController("/client.html");
     
     // handle sparql response back to result view template
     registry.addViewController("/result").setViewName("result");
     registry.addViewController("/result.html");
     
     // handle sparql response back to image view template
     registry.addViewController("/image").setViewName("image");
     registry.addViewController("/image.html");
     
     // handle about project view template
     registry.addViewController("/about").setViewName("about");
     registry.addViewController("/about.html");
 }
}