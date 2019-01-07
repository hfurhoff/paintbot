package se.cygni.snake.game;

import com.google.common.eventbus.EventBus;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import se.cygni.game.Tile;
import se.cygni.game.TileMultipleContent;
import se.cygni.game.WorldState;
import se.cygni.game.enums.Action;
import se.cygni.game.testutil.SnakeTestUtil;
import se.cygni.game.transformation.KeepOnlyObjectsOfType;
import se.cygni.game.transformation.KeepOnlySnakeWithId;
import se.cygni.game.transformation.PerformCharacterAction;
import se.cygni.game.worldobject.*;
import se.cygni.game.worldobject.Character;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.api.request.RegisterPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

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
        game.registerPlayer(new RegisterPlayer("emil", new GameSettings()));
        game.registerPlayer(new RegisterPlayer("lisa", new GameSettings()));

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
