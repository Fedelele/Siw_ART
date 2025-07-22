package it.uniroma3.siw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Controller
public class WebMvcController implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/artwork-cover/**")
                .addResourceLocations("file:C:/Users/wufed/Desktop/uploads-siw-art/artwork-cover/");
        registry.addResourceHandler("/museum-photo/**")
                .addResourceLocations("file:C:/Users/wufed/Desktop/uploads-siw-art/museum-photo/");
        registry.addResourceHandler("/artist-photo/**")
                .addResourceLocations("file:C:/Users/wufed/Desktop/uploads-siw-art/artist-photo/");


    }
}
