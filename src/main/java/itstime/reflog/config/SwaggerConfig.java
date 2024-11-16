package itstime.reflog.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI SwaggerAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());

    }

    private Info apiInfo(){
        return new Info()
                .title("Reflog Swagger")
                .description("Reflog Server API 명세서")
                .version("1.0.0");
    }
}
