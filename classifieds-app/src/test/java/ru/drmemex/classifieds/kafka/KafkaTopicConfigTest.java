package ru.drmemex.classifieds.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.drmemex.classifieds.kafka.KafkaTopic.ADVERTISEMENT_LIFECYCLE_TOPIC;
import static ru.drmemex.classifieds.kafka.KafkaTopic.USER_LIFECYCLE_TOPIC;

class KafkaTopicConfigTest {

    private final KafkaTopicConfig kafkaTopicConfig =
            new KafkaTopicConfig();

    @Test
    void advertisementLifecycleTopic_ShouldCreateTopicWithCorrectConfiguration() {

        NewTopic topic = kafkaTopicConfig.advertisementLifecycleTopic();

        assertEquals(
                ADVERTISEMENT_LIFECYCLE_TOPIC,
                topic.name()
        );

        assertEquals(
                3,
                topic.numPartitions()
        );

        assertEquals(
                (short) 1,
                topic.replicationFactor()
        );
    }

    @Test
    void userLifecycleTopic_ShouldCreateTopicWithCorrectConfiguration() {

        NewTopic topic = kafkaTopicConfig.userLifecycleTopic();

        assertEquals(
                USER_LIFECYCLE_TOPIC,
                topic.name()
        );

        assertEquals(
                3,
                topic.numPartitions()
        );

        assertEquals(
                (short) 1,
                topic.replicationFactor()
        );
    }
}