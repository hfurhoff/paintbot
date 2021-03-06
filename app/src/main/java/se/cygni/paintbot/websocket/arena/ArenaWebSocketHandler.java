package se.cygni.paintbot.websocket.arena;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketSession;
import se.cygni.paintbot.api.exception.InvalidMessage;
import se.cygni.paintbot.arena.ArenaManager;
import se.cygni.paintbot.arena.ArenaSelectionManager;
import se.cygni.paintbot.websocket.BaseGameSocketHandler;

import java.io.IOException;

public class ArenaWebSocketHandler extends BaseGameSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(ArenaWebSocketHandler.class);

    private static final String ARENA_NAME_WHITELIST = "[a-zA-Z0-9-_]+";

    private final ArenaSelectionManager arenaSelectionManager;
    private ArenaManager arenaManager;

    @Autowired
    public ArenaWebSocketHandler(ArenaSelectionManager arenaSelectionManager) {
        this.arenaSelectionManager = arenaSelectionManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        super.afterConnectionEstablished(session);

        String uri = session.getUri().getPath();

        String arenaName = uri.replaceAll(".*/", "");
        if (arenaName.length() > 0) {
            if (!arenaName.matches(ARENA_NAME_WHITELIST)) {
                handleInvalidName(uri, arenaName);
                return;
            }
        }

        this.arenaManager = arenaSelectionManager.getArena(arenaName);
        setOutgoingEventBus(arenaManager.getOutgoingEventBus());
        setIncomingEventBus(arenaManager.getIncomingEventBus());
        log.info("Started arena web socket handler");
    }

    private void handleInvalidName(String uri, String arenaFromUri) {
        try {
            sendPaintbotMessage(new InvalidMessage(
                    String.format("Arena name %s does not match allowed pattern %s", arenaFromUri, ARENA_NAME_WHITELIST),
                    uri));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void playerLostConnection() {
        log.info("{} lost connection", getPlayerId());
        if (arenaManager != null) {
            arenaManager.playerLostConnection(getPlayerId());
        }
    }
}
