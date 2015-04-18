package org.nthdimenzion.presentation;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Samir on 4/18/2015.
 */
@Configuration
@EnableSwagger
public class SwaggerConfiguration {

    @Autowired
    private SpringSwaggerConfig springSwaggerConfig;

    @Bean//"/quotation/grouplife/.*","/core/.*"
    public SwaggerSpringMvcPlugin customImplementation() {
        return new SwaggerSpringMvcPlugin(this.springSwaggerConfig).apiInfo(apiInfo()).includePatterns("/*/.*");
    }

    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo("PLA API", "API for PLA",
                "PLA API terms of service", "samir_padhy@nthdimenzion.com",
                "PLA API Licence Type", "PLA API License URL");
        return apiInfo;
    }
}
