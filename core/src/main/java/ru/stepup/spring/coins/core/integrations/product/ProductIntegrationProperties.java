package ru.stepup.spring.coins.core.integrations.product;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@Validated
@ConfigurationProperties(prefix = "integrations.product")
public class ProductIntegrationProperties {

    public ProductIntegrationProperties() { }

    @NotNull
    private Client client;

    @Validated
    public static class Client{

        public Client() { }

        @NotEmpty
        String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
