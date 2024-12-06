package com.swproject.hereforus.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Hereforus API 명세서",
                description = "데이트 코스 추천 서비스 명세서",
                version = "v1"
        ),
        servers = {
                @Server(url = "https://14.63.178.28.nip.io", description = "hereforus https 서버.")
        }
)
@Configuration
public class SwaggerConfig {
}
