package com.restaurant.user.kafka.producers;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.BaseEvent;
import com.restaurant.common.events.user.ProfileUpdatedEvent;
import com.restaurant.common.events.user.UserLoggedInEvent;
import com.restaurant.common.events.user.UserRegisteredEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserEventProducer {

    private final KafkaTemplate<String, BaseEvent> kafkaTemplate;

    public UserEventProducer(KafkaTemplate<String, BaseEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishUserRegisteredEvent(UserRegisteredEvent event) {
        kafkaTemplate.send(KafkaTopics.USER_REGISTRATION, event.getUserId(), event);
    }

    public void publishUserLoggedInEvent(UserLoggedInEvent event) {
        kafkaTemplate.send(KafkaTopics.USER_LOGIN, event.getUserId(), event);
    }

    public void publishProfileUpdatedEvent(ProfileUpdatedEvent event) {
        kafkaTemplate.send(KafkaTopics.USER_PROFILE, event.getUserId(), event);
    }
}