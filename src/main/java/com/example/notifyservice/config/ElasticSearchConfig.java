package com.example.notifyservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.client.reactive.ReactiveRestClients;
import org.springframework.data.elasticsearch.config.AbstractReactiveElasticsearchConfiguration;

@Configuration
public class ElasticSearchConfig extends AbstractReactiveElasticsearchConfiguration {
    @Value("${elasticsearch.host}")
    private String EsHost;
    @Override
    public ReactiveElasticsearchClient reactiveElasticsearchClient() {
        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(EsHost) //
                .build();
        return ReactiveRestClients.create(clientConfiguration);
    }
}
