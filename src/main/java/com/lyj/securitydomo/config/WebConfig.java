package com.lyj.securitydomo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 실제 저장 경로를 `/view/` 경로로 매핑
        registry.addResourceHandler("/view/**")
                .addResourceLocations
                        ("file:/Users/sin-inseon/2024Workspace/plzProject-main/src/main/resources/static/upload");
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
    }


}
