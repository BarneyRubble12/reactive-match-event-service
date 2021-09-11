package com.bedrock.reactive.matchservice.service;

import com.bedrock.reactive.matchservice.model.Match;
import reactor.core.publisher.Mono;

public interface MatchService {

  Mono<Match> findMatchById(Long id);
  Mono<String> saveMatchDetails(Match match);


}
