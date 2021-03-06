package se.cygni.paintbot.websocket.training;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.cygni.paintbot.game.Game;
import se.cygni.paintbot.game.GameManager;
import se.cygni.paintbot.websocket.BaseGameSocketHandler;

public class TrainingWebSocketHandler extends BaseGameSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(TrainingWebSocketHandler.class);

    private final GameManager gameManager;
    private final Game game;

    @Autowired
    public TrainingWebSocketHandler(GameManager gameManager) {
        this.gameManager = gameManager;
        log.info("Started training web socket handler");

        game = gameManager.createTrainingGame();

        setOutgoingEventBus(game.getOutgoingEventBus());
        setIncomingEventBus(game.getIncomingEventBus());
    }


    @Override
    protected void playerLostConnection() {
        log.info("{} lost connection", getPlayerId());
        game.playerLostConnection(getPlayerId());
        game.abort();
    }
}
