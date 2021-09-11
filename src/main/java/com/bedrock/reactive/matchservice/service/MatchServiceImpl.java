package com.bedrock.reactive.matchservice.service;

import com.bedrock.reactive.matchservice.model.Match;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

  private static final String KEY = "matches";

  private final ReactiveRedisTemplate<String, Match> matchReactiveRedisTemplate;
  private final KafkaSender<String, String> kafkaSender;

  private final ObjectMapper objectMapper;

  @Value("${kafka.livescore.topic}")
  private String topicName;


  @Override
  public Mono<Match> findMatchById(Long id) {
    return reactiveMatchHashOperations()
        .get(KEY, id.toString());
  }

  @Override
  public Mono<String> saveMatchDetails(Match match) {
    return reactiveMatchHashOperations()
        .put(KEY, match.getMatchId().toString(), match)
        .log()
        .flatMap(b -> kafkaSender
            .send(Mono.just(matchToSenderRecord(match)))
            .next()
            .log()
            .map(longSenderResult -> longSenderResult.exception() == null))
        .map(b -> b ? "OK" : "ERROR");
  }


  private SenderRecord<String, String, Long> matchToSenderRecord(Match match) {
    final String matchJsonStr;
    try {
      matchJsonStr = objectMapper.writeValueAsString(match);
    } catch (JsonProcessingException ex) {
      throw new RuntimeException(ex);
    }

    return SenderRecord.create(new ProducerRecord<>(topicName, matchJsonStr), match.getMatchId());
  }

  private ReactiveHashOperations<String, String, Match> reactiveMatchHashOperations() {
    return matchReactiveRedisTemplate
        .opsForHash();
  }


}
