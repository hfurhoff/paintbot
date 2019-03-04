package se.cygni.paintbot.api.event;

import org.junit.Test;
import se.cygni.paintbot.api.GameMessageParser;

import static org.junit.Assert.assertEquals;

public class GameEndedEventTest {

    @Test
    public void testSerializationGameEndedEvent() throws Exception {
        GameEndedEvent gee = new GameEndedEvent("playerWinnerId", "playerWinnerName", "666", 4, null);
        TestUtil.populateBaseData(gee, "rPlayerId");

        String serialized = GameMessageParser.encodeMessage(gee);
        GameEndedEvent parsedgee = (GameEndedEvent)GameMessageParser.decodeMessage(serialized);

        assertEquals("playerWinnerId", parsedgee.getPlayerWinnerId());
        assertEquals("playerWinnerName", parsedgee.getPlayerWinnerName());
        assertEquals("666", parsedgee.getGameId());
        assertEquals(4, parsedgee.getGameTick());
        assertEquals("rPlayerId", parsedgee.getReceivingPlayerId());
    }

}