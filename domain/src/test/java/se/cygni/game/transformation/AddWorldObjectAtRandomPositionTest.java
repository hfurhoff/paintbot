package se.cygni.game.transformation;

import org.junit.Test;
import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.testutil.PaintbotTestUtil;
import se.cygni.game.worldobject.Empty;
import se.cygni.game.worldobject.Obstacle;
import se.cygni.game.worldobject.PowerUp;

import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class AddWorldObjectAtRandomPositionTest {

    @Test
    public void testAddRandomWorldObjectTransform() throws Exception {
        WorldState world = new WorldState(3,3);

        PowerUp powerUp = new PowerUp();
        AddWorldObjectAtRandomPosition randomFood = new AddWorldObjectAtRandomPosition(powerUp);
        WorldState transformedWorld = randomFood.transform(world);

        int noofTilesNotEmpty = 0;
        int noofTilesFood = 0;

        for (Tile tile : transformedWorld.getTiles()) {
            if (!(tile.getContent() instanceof Empty)) {
                noofTilesNotEmpty++;
            }
            if (tile.getContent() instanceof PowerUp) {
                noofTilesFood++;
                assertEquals(powerUp, tile.getContent());
            }
        }

        assertEquals(1, noofTilesNotEmpty);
        assertEquals(1, noofTilesFood);
    }

    @Test
    public void testAddRandomSeveralWorldObjectsTransform() throws Exception {
        WorldState world = new WorldState(3,3);

        Obstacle obstacle = new Obstacle();
        AddWorldObjectAtRandomPosition randomObstacle = new AddWorldObjectAtRandomPosition(obstacle);
        WorldState transformedWorld = randomObstacle.transform(world);

        PowerUp powerUp = new PowerUp();
        AddWorldObjectAtRandomPosition randomFood = new AddWorldObjectAtRandomPosition(powerUp);
        transformedWorld = randomFood.transform(transformedWorld);

        int noofTilesNotEmpty = 0;
        int noofTilesFood = 0;
        int noofTilesObstacle = 0;

        for (Tile tile : transformedWorld.getTiles()) {
            if (!(tile.getContent() instanceof Empty)) {
                noofTilesNotEmpty++;
            }
            if (tile.getContent() instanceof PowerUp) {
                noofTilesFood++;
                assertEquals(powerUp, tile.getContent());
            }
            if (tile.getContent() instanceof Obstacle) {
                noofTilesObstacle++;
                assertEquals(obstacle, tile.getContent());
            }
        }

        assertEquals(2, noofTilesNotEmpty);
        assertEquals(1, noofTilesFood);
        assertEquals(1, noofTilesObstacle);
    }

    @Test
    public void testSameWordStateIsReturnedIfAlreadyFull() throws Exception {

        // Create world full of Obstacles
        WorldState ws = PaintbotTestUtil.createWorld(Obstacle.class, 10, 10, IntStream.range(0,100).toArray());

        PowerUp powerUp = new PowerUp();
        AddWorldObjectAtRandomPosition randomFood = new AddWorldObjectAtRandomPosition(powerUp);

        ws = randomFood.transform(ws);

        assertEquals(100, ws.listObstaclePositions().length);
    }

    @Test
    public void testWithOnlyOneFreeTile() throws Exception {

        // Create world full of Obstacles
        WorldState ws = PaintbotTestUtil.createWorld(Obstacle.class, 10, 10, IntStream.range(0,100).toArray());
        ws = PaintbotTestUtil.replaceWorldObjectAt(ws, new Empty(), 50);

        PowerUp powerUp = new PowerUp();
        AddWorldObjectAtRandomPosition randomFood = new AddWorldObjectAtRandomPosition(powerUp);

        ws = randomFood.transform(ws);

        assertEquals(99, ws.listObstaclePositions().length);
        assertEquals(1, ws.listPowerUpPositions().length);
    }
}