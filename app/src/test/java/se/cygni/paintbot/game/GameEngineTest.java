package se.cygni.paintbot.game;

import com.google.common.eventbus.EventBus;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import se.cygni.game.testutil.PaintbotTestUtil;
import se.cygni.game.transformation.KeepOnlyPaintbotWithId;
import se.cygni.paintbot.api.model.GameSettings;
import se.cygni.paintbot.api.request.RegisterPlayer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class GameEngineTest {

    private GameEngine gameEngine;
    private Game game;

    @Before
    public void setup() {
        GameFeatures gameFeatures = new GameFeatures();
        gameFeatures.setTimeInMsPerTick(1000);
        gameFeatures.setMaxNoofPlayers(25);
        gameFeatures.setTrainingGame(true);

        GameManager gameManager = new GameManager(new EventBus());
        game = gameManager.createGame(gameFeatures);

        gameEngine = game.getGameEngine();
    }

    @Test
    public void testGame() {
        RegisterPlayer emil = new RegisterPlayer("emil", new GameSettings());
        emil.setReceivingPlayerId("id-"+emil.getPlayerName());
        game.registerPlayer(emil);
        RegisterPlayer lisa = new RegisterPlayer("lisa", new GameSettings());
        lisa.setReceivingPlayerId("id-"+lisa.getPlayerName());
        game.registerPlayer(lisa);
        game.startGame();
    }

    @Test @Ignore
    public void testSimpleGame() {
        game.startGame();

        do {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }
        } while (game.getGameEngine().isGameRunning());
    }
}
