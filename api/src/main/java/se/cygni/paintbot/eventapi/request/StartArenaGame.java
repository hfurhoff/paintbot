package se.cygni.paintbot.eventapi.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.paintbot.eventapi.ApiMessage;
import se.cygni.paintbot.eventapi.type.ApiMessageType;

@ApiMessageType
public class StartArenaGame extends ApiMessage {

    private final String arenaName;

    @JsonCreator
    public StartArenaGame(
            @JsonProperty("arenaName") String arenaName) {
        this.arenaName = arenaName;
    }

    public String getArenaName() {
        return arenaName;
    }
}
