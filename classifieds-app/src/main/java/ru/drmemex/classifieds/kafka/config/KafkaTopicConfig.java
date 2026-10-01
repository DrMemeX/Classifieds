package ru.drmemex.classifieds.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String ADVERTISEMENT_LIFECYCLE_TOPIC = "classifieds.advertisement.lifecycle.v1";
    public static final String USER_LIFECYCLE_TOPIC = "classifieds.user.lifecycle.v1";

    @Bean
    public NewTopic advertisementLifecycleTopic() {
        return TopicBuilder.name(ADVERTISEMENT_LIFECYCLE_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userLifecycleTopic() {
        return TopicBuilder.name(USER_LIFECYCLE_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
