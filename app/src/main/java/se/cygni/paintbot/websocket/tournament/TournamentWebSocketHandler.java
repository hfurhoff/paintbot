package se.cygni.paintbot.websocket.tournament;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.cygni.paintbot.tournament.TournamentManager;
import se.cygni.paintbot.websocket.BaseGameSocketHandler;

public class TournamentWebSocketHandler extends BaseGameSocketHandler {

    private static Logger log = LoggerFactory.getLogger(TournamentWebSocketHandler.class);

    private TournamentManager tournamentManager;

    @Autowired
    public TournamentWebSocketHandler(TournamentManager tournamentManager) {
        this.tournamentManager = tournamentManager;

        log.info("Started tournament web socket handler");

        // Get an eventbus and register this handler
        this.setOutgoingEventBus(tournamentManager.getOutgoingEventBus());
        this.setIncomingEventBus(tournamentManager.getIncomingEventBus());
    }

    @Override
    protected void playerLostConnection() {
        tournamentManager.playerLostConnection(getPlayerId());
    }
}
