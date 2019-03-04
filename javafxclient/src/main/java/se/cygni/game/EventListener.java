package se.cygni.game;

import se.cygni.paintbot.api.event.GameEndedEvent;
import se.cygni.paintbot.api.event.GameStartingEvent;
import se.cygni.paintbot.api.event.MapUpdateEvent;
import se.cygni.paintbot.api.event.PaintbotDeadEvent;
import se.cygni.paintbot.api.exception.InvalidPlayerName;
import se.cygni.paintbot.api.response.PlayerRegistered;
import se.cygni.paintbot.eventapi.response.ActiveGamesList;

public interface EventListener {

    public void onMessage(String message);

    public void onActiveGamesList(ActiveGamesList activeGamesList);

    public void onMapUpdate(MapUpdateEvent mapUpdateEvent);

    public void onPaintbotDead(PaintbotDeadEvent paintbotDeadEvent);

    public void onGameEnded(GameEndedEvent gameEndedEvent);

    public void onGameStarting(GameStartingEvent gameStartingEvent);

    public void onPlayerRegistered(PlayerRegistered playerRegistered);

    public void onInvalidPlayerName(InvalidPlayerName invalidPlayerName);

}
