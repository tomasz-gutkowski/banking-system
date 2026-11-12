package tgutkowski.project.backend.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class OpenApiConfig {
    private static final String AUTHORIZATION_HEADER = "Validate your JWT from /auth/login";

    @Bean
    public OpenAPI apiInfo(){
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(AUTHORIZATION_HEADER))
                .components(new Components().addSecuritySchemes(AUTHORIZATION_HEADER,
                        new SecurityScheme()
                                .name(AUTHORIZATION_HEADER)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .info(new Info()
                        .title("Banking System API Documentation")
                        .description("OpenAPI Documentation for a Banking System project")
                        .version("1.0"))
                .tags(List.of(
                        new Tag().name(TagNames.AUTH).description(TagNames.AUTH_DESCRIPTION),
                        new Tag().name(TagNames.ACCOUNT).description(TagNames.ACCOUNT_DESCRIPTION),
                        new Tag().name(TagNames.TRANSFER).description(TagNames.TRANSFER_DESCRIPTION),
                        new Tag().name(TagNames.ADMIN).description(TagNames.ADMIN_DESCRIPTION)
                ))
                ;


    }
    public static final class TagNames {
        private TagNames() {}
        public static final String AUTH = "Authorization";
        public static final String ACCOUNT = "Account";
        public static final String TRANSFER = "Transfer";
        public static final String ADMIN = "Admin";

        public static final String AUTH_DESCRIPTION = "Authorization and registration endpoints";
        public static final String ACCOUNT_DESCRIPTION = "Bank account creation and management";
        public static final String TRANSFER_DESCRIPTION = "Transfer and funds management";
        public static final String ADMIN_DESCRIPTION = "Admin role exclusive endpoints";
    }
}
