package com.padillatomas.libraryeventsproducer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class AutoCreateConfig {

    /*
     * Will create TOPICs
     * Make sure we have Zookeper and Broker are running
     * Ex: 9092, 9093, 9094
     */
    @Bean
    public NewTopic libraryEventsTopic()
    {
        return TopicBuilder.name("library-events")
                .partitions(3)
                .replicas(3)
                .build();
    }

}
