package com.example.cric_info.Service;

import com.example.cric_info.Entity.Match;

import java.util.List;

public interface MatchService {
    List<Match> getLiveMatchScores();
    List<List<String>> getWTCPointTable(); // Updated method name in interface
    List<Match> getAllMatches();
}
