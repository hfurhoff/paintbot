package se.cygni.paintbot.event;

import se.cygni.paintbot.api.GameMessage;
import se.cygni.paintbot.apiconversion.GameMessageConverter;

public class InternalGameEvent {
    private final long tstamp;
    private GameMessage gameMessage;

    public InternalGameEvent(long tstamp) {
        this.tstamp = tstamp;
    }

    public InternalGameEvent(long tstamp, GameMessage gameMessage) {
        this.tstamp = tstamp;
        this.gameMessage = gameMessage;
    }

    public long getTstamp() {
        return tstamp;
    }

    public GameMessage getGameMessage() {
        return gameMessage;
    }

    public void onGameAborted(String gameId) {
        this.gameMessage = GameMessageConverter.onGameAborted(gameId);
    }

    public void onGameChanged(String gameId) {
        this.gameMessage = GameMessageConverter.onGameChanged(gameId);
    }
}
