package com.bedrock.reactive.matchservice.controller;

import com.bedrock.reactive.matchservice.model.Match;
import com.bedrock.reactive.matchservice.service.KafkaService;
import com.bedrock.reactive.matchservice.service.MatchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MatchController {

  private final MatchService matchservice;
  private final KafkaService kafkaService;
  private final ObjectMapper objectMapper;

  @GetMapping("/match/{id}")
  public Mono<Match> getMatchById(@PathVariable("id") Long id) {
    log.info("getMatchById - id received: {}", id);
    return matchservice.findMatchById(id);
  }

  @PostMapping(
      value = "/match",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public Mono<String> saveMatchDetails(@RequestBody Match match) {
    log.info("saveMatchDetails - match details received: {}", match);
    return matchservice.saveMatchDetails(match);
  }

  @GetMapping(value = "/match/{id}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ServerSentEvent<Match>> streamMatchEvents(@PathVariable("id") Long id) {
    log.info("streamMatchEvents - id received: {}", id);
    var heartbeatStream = Flux
        .interval(Duration.ofSeconds(10))
        .map(this::toHeartBeatServerSentEvent)
        .log();

    return kafkaService
        .getEventPublisher()
        .map(stringServerSentEvent -> jsonStrToMatch(stringServerSentEvent.data()))
        .filter(match -> match != null)
        .filter(match -> match.getMatchId().equals(id))
        .map(this::matchToServerSentEvent)
        .mergeWith(heartbeatStream)
        .log();
  }

  private ServerSentEvent<Match> toHeartBeatServerSentEvent(Long tick) {
    return matchToServerSentEvent(
        new Match(0l, "Heart-Beat-Match-" + tick, LocalDateTime.now(), "Alive", null, null));
  }

  private ServerSentEvent<Match> matchToServerSentEvent(Match match) {
    return ServerSentEvent
        .<Match>builder()
        .data(match)
        .build();
  }

  private Match jsonStrToMatch(String jsonStr) {
    Match match = null;
    try {
      match = objectMapper.readValue(jsonStr, Match.class);
    } catch (IOException ex) {
      log.error("parsing exception", ex);
      return null;
    }

    return match;
  }


}
