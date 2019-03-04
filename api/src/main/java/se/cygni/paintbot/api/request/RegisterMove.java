package se.cygni.paintbot.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.paintbot.api.GameMessage;
import se.cygni.paintbot.api.model.CharacterAction;
import se.cygni.paintbot.api.type.GameMessageType;

@GameMessageType
public class RegisterMove extends GameMessage {

    private final long gameTick;
    private final String gameId;
    private final CharacterAction direction;

    @JsonCreator
    public RegisterMove(
            @JsonProperty("gameId") String gameId,
            @JsonProperty("gameTick") long gameTick,
            @JsonProperty("direction") CharacterAction direction) {

        this.gameId = gameId;
        this.gameTick = gameTick;
        this.direction = direction;
    }

    public String getGameId() {
        return gameId;
    }

    public long getGameTick() {
        return gameTick;
    }

    public CharacterAction getDirection() {
        return direction;
    }
}
