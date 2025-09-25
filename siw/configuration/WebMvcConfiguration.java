package it.uniroma3.siw.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//Tells Spring MVC how to serve static resources stored outside the project's classpath (like images on my device)
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    //Method used to map URL patterns to resource locations
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/artwork-cover/**")
                //Mapping that tells Spring where to find the file requested in the URL
                .addResourceLocations("file:C:/Users/wufed/Desktop/uploads-siw-art/artwork-cover/");
        registry.addResourceHandler("/museum-photo/**")
                .addResourceLocations("file:C:/Users/wufed/Desktop/uploads-siw-art/museum-photo/");
        registry.addResourceHandler("/artist-photo/**")
                .addResourceLocations("file:C:/Users/wufed/Desktop/uploads-siw-art/artist-photo/");


    }
}
