package com.cyou.fz.mcms.process.web.common;

import org.springframework.context.annotation.Bean;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by cnJason on 2016/12/6.
 */
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket swaggerSpringMvcPlugin(){
        return new Docket(DocumentationType.SWAGGER_2).select().build().apiInfo(
                new ApiInfo("mcms-api","mcms-api","v0.1","","","","")
        );
    }
}
