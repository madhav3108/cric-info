package com.example.cric_info.Service.impl;

import com.example.cric_info.Entity.Match;
import com.example.cric_info.Repository.MatchRepo;
import com.example.cric_info.Service.MatchService;
import org.springframework.stereotype.Service;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

@Service
public class MatchServiceImpl implements MatchService {

    private final MatchRepo matchRepo;

    public MatchServiceImpl(MatchRepo matchRepo) {
        this.matchRepo = matchRepo;
    }

    @Override
    public List<Match> getLiveMatchScores() { // Removed generic type <Document>
        List<Match> matches = new ArrayList<>();
        try {
            String url = "https://www.cricbuzz.com/cricket-match/live-scores";
            Document document = Jsoup.connect(url).get();
            Elements liveScoreElements = document.select("div.cb-mtch-lst.cb-tms-itm");

            for (Element match : liveScoreElements) {
                String teamsHeading = match.select("h3.cb-lv-scr-mtch-hdr a").text();
                String matchNumberVenue = match.select("span").text();
                Elements matchBatTeamInfo = match.select("div.cb-hmscg-bat-txt");
                String battingTeam = matchBatTeamInfo.select("div.cb-hmscg-tm-nm").text();
                String score = matchBatTeamInfo.select("div.cb-hmscg-tm-nm + div").text();
                Elements bowlTeamInfo = match.select("div.cb-hmscg-bwl-txt");
                String bowlTeam = bowlTeamInfo.select("div.cb-hmscg-tm-nm").text();
                String bowlTeamScore = bowlTeamInfo.select("div.cb-hmscg-tm-nm + div").text();
                String textLive = match.select("div.cb-text-live").text();
                String textComplete = match.select("div.cb-text-complete").text();
                String matchLink = match.select("a.cb-lv-scrs-well.cb-lv-scrs-well-live").attr("href");

                Match match1 = new Match();
                match1.setTeamHeading(teamsHeading);
                match1.setMatchVenue(matchNumberVenue);
                match1.setBattingTeam(battingTeam);
                match1.setBattingTeamScore(score);
                match1.setBowlingTeam(bowlTeam);
                match1.setBowlingTeamScore(bowlTeamScore);
                match1.setLiveText(textLive);
                match1.setMatchLink(matchLink);
                match1.setTextComplete(textComplete);
                match1.setMatchStatus();
                // Assume setMatchStatus is implemented elsewhere in Match

                matches.add(match1);
                updateMatch(match1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return matches;
    }

    private void updateMatch(Match match1) {
        Match match = this.matchRepo.findByTeamHeading(match1.getTeamHeading()).orElse(null);
        if (match == null) {
            this.matchRepo.save(match1);
        } else {
            match1.setMatchId(match.getMatchId());
            this.matchRepo.save(match1);
        }
    }

    @Override
    public List<List<String>> getWTCPointTable() {
        List<List<String>> pointTable = new ArrayList<>();
        String tableURL = "https://www.cricbuzz.com/cricket-stats/points-table/test/world-test-championship-2023-2025";
        try {
            Document document = Jsoup.connect(tableURL).get();
            Elements table = document.select("table.cb-srs-pnts");
            Elements tableHeads = table.select("thead>tr>*");
            List<String> headers = new ArrayList<>();
            tableHeads.forEach(element -> headers.add(element.text()));
            pointTable.add(headers);

            Elements bodyTrs = table.select("tbody>tr");
            bodyTrs.forEach(tr -> {
                List<String> points = new ArrayList<>();
                Elements tds = tr.select("td");
                String team = tds.get(0).select("div.cb-col-84").text();
                points.add(team);
                tds.forEach(td -> {
                    if (!td.hasClass("cb-srs-pnts-name")) {
                        points.add(td.text());
                    }
                });
                pointTable.add(points);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pointTable;
    }

    @Override
    public List<Match> getAllMatches() {
        return this.matchRepo.findAll();
    }
}
