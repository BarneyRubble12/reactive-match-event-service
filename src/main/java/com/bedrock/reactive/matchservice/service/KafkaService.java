package com.bedrock.reactive.matchservice.service;

import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.kafka.receiver.KafkaReceiver;

@Service
@RequiredArgsConstructor
public class KafkaService {

  private final KafkaReceiver<String, String> kafkaReceiver;

  @Getter
  private ConnectableFlux<ServerSentEvent<String>> eventPublisher;

  @PostConstruct
  public void postConstruct() {
    eventPublisher = kafkaReceiver
        .receive()
        .map(record -> ServerSentEvent.builder(record.value()).build())
        .publish();

    // Subscribes to the receiver and starts consumming data
    eventPublisher.connect();
  }

}
