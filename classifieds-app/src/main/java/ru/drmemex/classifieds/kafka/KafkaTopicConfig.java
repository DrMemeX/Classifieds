package ru.drmemex.classifieds.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static ru.drmemex.classifieds.kafka.KafkaTopic.ADVERTISEMENT_LIFECYCLE_TOPIC;
import static ru.drmemex.classifieds.kafka.KafkaTopic.USER_LIFECYCLE_TOPIC;

@Configuration
public class KafkaTopicConfig {

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
