package se.cygni.game.transformation;

import org.junit.Test;
import se.cygni.game.WorldState;
import se.cygni.game.testutil.PaintbotTestUtil;
import se.cygni.game.worldobject.Obstacle;
import se.cygni.game.worldobject.PowerUp;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class RemoveRandomWorldObjectTest {

    @Test
    public void testRemoveObstacle() throws Exception {
        WorldState ws = PaintbotTestUtil.createWorld(Obstacle.class, 10, 10, 9, 12, 18);
        ws = PaintbotTestUtil.replaceWorldObjectAt(ws, new PowerUp(), 10);

        RemoveRandomWorldObject<Obstacle> removeTransformation = new RemoveRandomWorldObject<>(Obstacle.class);

        ws = removeTransformation.transform(ws);

        assertArrayEquals(new int[]{10}, ws.listPowerUpPositions());
        assertEquals(2, ws.listObstaclePositions().length);
    }

    @Test
    public void testNothingChangesIfObjectTypeDoesntExists() throws Exception {
        WorldState ws = PaintbotTestUtil.createWorld(Obstacle.class, 10, 10, 9, 12, 18);

        RemoveRandomWorldObject<PowerUp> removeTransformation = new RemoveRandomWorldObject<>(PowerUp.class);

        ws = removeTransformation.transform(ws);

        assertArrayEquals(new int[]{9, 12, 18}, ws.listObstaclePositions());
        assertEquals(0, ws.listPowerUpPositions().length);
    }

    @Test
    public void testTransformWithNulls() throws Exception {
        WorldState ws = PaintbotTestUtil.createWorld(Obstacle.class, 10, 10, 9, 12, 18);
        RemoveRandomWorldObject<PowerUp> removeTransformation = new RemoveRandomWorldObject<>(null);
        ws = removeTransformation.transform(ws);
    }
}