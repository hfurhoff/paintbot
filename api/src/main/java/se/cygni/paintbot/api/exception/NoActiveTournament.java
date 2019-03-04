package se.cygni.paintbot.api.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import se.cygni.paintbot.api.GameMessage;
import se.cygni.paintbot.api.type.GameMessageType;

@GameMessageType
public class NoActiveTournament extends GameMessage {

    @JsonCreator
    public NoActiveTournament() {
    }

}
