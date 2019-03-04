package se.cygni.paintbot.api.event;

import org.junit.Test;
import se.cygni.paintbot.api.GameMessageParser;
import se.cygni.paintbot.api.model.StunReason;

import static org.junit.Assert.*;

public class CharacterStunnedEventTest {

    @Test
    public void testSerializationPaintbotDeadEvent() throws Exception {
        CharacterStunnedEvent sde = new CharacterStunnedEvent(StunReason.CollisionWithWall, 7,"playerId", 5, 10, "6666", 99);
        TestUtil.populateBaseData(sde, "rPlayerId");

        String serialized = GameMessageParser.encodeMessage(sde);

        CharacterStunnedEvent parsedSde = (CharacterStunnedEvent)GameMessageParser.decodeMessage(serialized);

        assertEquals(StunReason.CollisionWithWall, parsedSde.getStunReason());
        assertEquals(7, parsedSde.getDurationInTicks());
        assertEquals("playerId", parsedSde.getPlayerId());
        assertEquals(5, parsedSde.getX());
        assertEquals(10, parsedSde.getY());
        assertEquals("6666", parsedSde.getGameId());
        assertEquals(99, parsedSde.getGameTick());
        assertEquals("rPlayerId", parsedSde.getReceivingPlayerId());
    }
}