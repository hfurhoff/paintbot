package se.cygni.game.transformation;

import org.junit.Ignore;
import org.junit.Test;
import se.cygni.game.WorldState;
import se.cygni.game.enums.Action;
import se.cygni.game.exception.ObstacleCollision;
import se.cygni.game.exception.PaintbotCollision;
import se.cygni.game.exception.TransformationException;
import se.cygni.game.exception.WallCollision;
import se.cygni.game.testutil.PaintbotTestUtil;
import se.cygni.game.worldobject.*;
import se.cygni.game.worldobject.Character;
import se.cygni.game.worldobject.CharacterImpl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class PerformCharacterActionTest {

    @Test
    public void testSimpleMoveRight() throws Exception {
        WorldState ws = new WorldState(10, 10);

        int startPos = 15;
        int expectedEndPos = 16;

        CharacterImpl head = new CharacterImpl("test", "id", startPos);
        ws = PaintbotTestUtil.replaceWorldObjectAt(ws, head, startPos);
        PerformCharacterAction performCharacterAction = new PerformCharacterAction(head, Action.RIGHT);
        ws = performCharacterAction.transform(ws);

        assertEquals(expectedEndPos, head.getPosition());
        assertArrayEquals(new int[]{expectedEndPos}, ws.listPositionsWithContentOf(CharacterImpl.class));
    }

    @Test
    public void testSimpleMoveLeft() throws Exception {
        WorldState ws = new WorldState(10, 10);

        int startPos = 15;
        int expectedEndPos = 14;

        CharacterImpl head = new CharacterImpl("test", "id", startPos);
        ws = PaintbotTestUtil.replaceWorldObjectAt(ws, head, startPos);
        PerformCharacterAction performCharacterAction = new PerformCharacterAction(head, Action.LEFT);
        ws = performCharacterAction.transform(ws);

        assertEquals(expectedEndPos, head.getPosition());
        assertArrayEquals(new int[]{expectedEndPos}, ws.listPositionsWithContentOf(CharacterImpl.class));
    }

    @Test
    public void testSimpleMoveUp() throws Exception {
        WorldState ws = new WorldState(10, 10);

        int startPos = 15;
        int expectedEndPos = 5;

        CharacterImpl head = new CharacterImpl("test", "id", startPos);
        ws = PaintbotTestUtil.replaceWorldObjectAt(ws, head, startPos);
        PerformCharacterAction performCharacterAction = new PerformCharacterAction(head, Action.UP);
        ws = performCharacterAction.transform(ws);

        assertEquals(expectedEndPos, head.getPosition());
        assertArrayEquals(new int[]{expectedEndPos}, ws.listPositionsWithContentOf(CharacterImpl.class));
    }

    @Test
    public void testSimpleMoveDown() throws Exception {
        WorldState ws = new WorldState(10, 10);

        int startPos = 15;
        int expectedEndPos = 25;

        CharacterImpl head = new CharacterImpl("test", "id", startPos);
        ws = PaintbotTestUtil.replaceWorldObjectAt(ws, head, startPos);
        PerformCharacterAction performCharacterAction = new PerformCharacterAction(head, Action.DOWN);
        ws = performCharacterAction.transform(ws);

        assertEquals(expectedEndPos, head.getPosition());
        assertArrayEquals(new int[]{expectedEndPos}, ws.listPositionsWithContentOf(CharacterImpl.class));
    }

    @Ignore
    @Test(expected = WallCollision.class)
    public void testWallCollision() throws Exception {
        WorldState ws = new WorldState(10, 10);

        int startPos = 95;

        CharacterImpl head = new CharacterImpl("test", "id", startPos);
        ws = PaintbotTestUtil.replaceWorldObjectAt(ws, head, startPos);
        PerformCharacterAction performCharacterAction = new PerformCharacterAction(head, Action.DOWN);
        performCharacterAction.transform(ws);
    }

    @Ignore
    @Test(expected = ObstacleCollision.class)
    public void testObstacleCollision() throws Exception {
        WorldState ws = new WorldState(10, 10);

        int startPos = 55;
        int obstaclePos = 56;

        CharacterImpl head = new CharacterImpl("test", "id", startPos);
        ws = PaintbotTestUtil.replaceWorldObjectAt(ws, head, startPos);
        ws = PaintbotTestUtil.replaceWorldObjectAt(ws, new Obstacle(), obstaclePos);
        PerformCharacterAction performCharacterAction = new PerformCharacterAction(head, Action.RIGHT);
        performCharacterAction.transform(ws);
    }

    @Ignore
    @Test(expected = PaintbotCollision.class)
    public void testPaintbotCollision() throws Exception {
        WorldState ws = new WorldState(10, 10);

        int startPos = 55;
        int otherPaintbotPos = 56;

        CharacterImpl head = new CharacterImpl("test", "id", startPos);
        ws = PaintbotTestUtil.replaceWorldObjectAt(ws, head, startPos);
        ws = PaintbotTestUtil.replaceWorldObjectAt(ws, new CharacterImpl("test2", "id", otherPaintbotPos), otherPaintbotPos);
        PerformCharacterAction performCharacterAction = new PerformCharacterAction(head, Action.RIGHT);
        performCharacterAction.transform(ws);
    }

    @Test(expected = TransformationException.class)
    public void testMovePaintbotIsNull() throws TransformationException {
        WorldState ws = new WorldState(10, 10);

        int startPos = 15;

        CharacterImpl head = null;
        ws = PaintbotTestUtil.replaceWorldObjectAt(ws, head, startPos);
        PerformCharacterAction performCharacterAction = new PerformCharacterAction(head, Action.DOWN);
        ws = performCharacterAction.transform(ws);
    }

    @Test(expected = TransformationException.class)
    public void testMoveDirectionIsNull() throws TransformationException {
        WorldState ws = new WorldState(10, 10);

        int startPos = 15;

        CharacterImpl head = new CharacterImpl("test", "id", startPos);
        ws = PaintbotTestUtil.replaceWorldObjectAt(ws, head, startPos);
        PerformCharacterAction performCharacterAction = new PerformCharacterAction(head, null);
        ws = performCharacterAction.transform(ws);
    }

}