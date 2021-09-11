package com.bedrock.reactive.matchservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchEvent {

  int minute;
  String type;
  String team;
  String playerName;

}
