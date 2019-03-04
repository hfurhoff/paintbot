package se.cygni.paintbot.api.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.paintbot.api.GameMessage;
import se.cygni.paintbot.api.type.GameMessageType;

@GameMessageType
public class GameLinkEvent extends GameMessage {

    private final String gameId;
    private final String url;

    @JsonCreator
    public GameLinkEvent(
            @JsonProperty("gameId") String gameId,
            @JsonProperty("url") String url) {

        this.gameId = gameId;
        this.url = url;
    }

    public String getGameId() {
        return gameId;
    }

    public String getUrl() {
        return url;
    }
}
