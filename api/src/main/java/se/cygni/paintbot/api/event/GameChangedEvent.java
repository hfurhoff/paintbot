package se.cygni.paintbot.api.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.paintbot.api.GameMessage;
import se.cygni.paintbot.api.type.GameMessageType;

@GameMessageType
public class GameChangedEvent extends GameMessage {

    private final String gameId;

    @JsonCreator
    public GameChangedEvent(
            @JsonProperty("gameId") String gameId) {

        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }

}
