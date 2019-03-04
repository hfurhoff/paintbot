package se.cygni.paintbot.apiconversion;

import org.junit.Test;
import se.cygni.paintbot.eventapi.ApiMessageParser;
import se.cygni.paintbot.eventapi.model.TournamentGamePlan;
import se.cygni.paintbot.game.GameFeatures;
import se.cygni.paintbot.game.PlayerManager;
import se.cygni.paintbot.game.TournamentPlanTest;
import se.cygni.paintbot.player.IPlayer;
import se.cygni.paintbot.tournament.TournamentPlan;

import java.util.Set;

public class TournamentPlanConverterTest {

    @Test
    public void testGetTournamentPlan() throws Exception {
        GameFeatures gf = new GameFeatures();

        Set<IPlayer> players = TournamentPlanTest.getPlayers(20);
        PlayerManager playerManager = new PlayerManager();
        playerManager.addAll(players);

        TournamentPlan tp = new TournamentPlan(gf, playerManager);


        TournamentGamePlan tgp = TournamentPlanConverter.getTournamentPlan(tp, "test-tournament", "1234");
        System.out.println(ApiMessageParser.encodeMessage(tgp));
    }
}