package se.cygni.paintbot.arena;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.game.Player;
import se.cygni.paintbot.api.event.ArenaUpdateEvent;
import se.cygni.paintbot.api.exception.InvalidPlayerName;
import se.cygni.paintbot.api.model.GameMode;
import se.cygni.paintbot.api.model.GameSettings;
import se.cygni.paintbot.api.request.RegisterMove;
import se.cygni.paintbot.api.request.RegisterPlayer;
import se.cygni.paintbot.api.response.PlayerRegistered;
import se.cygni.paintbot.api.util.MessageUtils;
import se.cygni.paintbot.apiconversion.GameSettingsConverter;
import se.cygni.paintbot.event.InternalGameEvent;
import se.cygni.paintbot.game.Game;
import se.cygni.paintbot.game.GameFeatures;
import se.cygni.paintbot.game.GameManager;
import se.cygni.paintbot.player.RemotePlayer;
import se.cygni.paintbot.tournament.util.TournamentUtil;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ArenaManager {
    private static final Logger log = LoggerFactory.getLogger(ArenaManager.class);
    private static final int ARENA_PLAYER_COUNT = 8;

    private final EventBus outgoingEventBus;
    private final EventBus incomingEventBus;
    private final EventBus globalEventBus;

    private String arenaName;
    private boolean ranked;

    private GameManager gameManager;
    private Set<Player> connectedPlayers = new HashSet<>();
    private long secondsUntilNextAutostartedGame = 0;
    private Game currentGame = null;
    private double currentGameStartTime;

    ArenaRater rater = new ArenaRater();

    public ArenaManager(GameManager gameManager, EventBus globalEventBus) {
        this.gameManager = gameManager;

        this.outgoingEventBus = new EventBus("arena-outgoing");
        this.incomingEventBus = new EventBus("arena-incoming");
        this.globalEventBus = globalEventBus;

        incomingEventBus.register(this);
        globalEventBus.register(this);
    }

    @Subscribe
    public void registerPlayer(RegisterPlayer registerPlayer) {
        String playerId = registerPlayer.getReceivingPlayerId();

        Player player = new Player(registerPlayer.getPlayerName());
        player.setPlayerId(playerId);

        // TODO remove duplicated code and add password or similar anti-fakenicking
        if (connectedPlayers.stream().anyMatch(otherPlayer -> otherPlayer.getName().equals(player.getName()))) {
            int removeDupWarning = 0;
            InvalidPlayerName playerNameTaken = new InvalidPlayerName(InvalidPlayerName.PlayerNameInvalidReason.Taken);
            MessageUtils.copyCommonAttributes(registerPlayer, playerNameTaken);
            playerNameTaken.setReceivingPlayerId(playerId);
            outgoingEventBus.post(playerNameTaken);
            return;
        }

        connectedPlayers.add(player);

        GameSettings gameSettings = GameSettingsConverter.toGameSettings(new GameFeatures());
        PlayerRegistered playerRegistered = new PlayerRegistered("not_yet_known", player.getName(), gameSettings, GameMode.ARENA);
        MessageUtils.copyCommonAttributes(registerPlayer, playerRegistered);

        outgoingEventBus.post(playerRegistered);

        log.debug("Player: {} registered in the arena {}", player.getName(), arenaName);

        broadcastState();
    }

    @Subscribe
    public void registerMove(RegisterMove registerMove) {
        if (currentGame != null) {
            currentGame.registerMove(registerMove);
        }
    }

    public void playerLostConnection(String playerId) {
        Player player = new Player("name_unknown");
        player.setPlayerId(playerId);
        connectedPlayers.remove(player);

        if (currentGame != null) {
            currentGame.playerLostConnection(playerId);
        }

        broadcastState();
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public void runGameScheduler() {
        processCurrentGame();

        if (ranked) {
            secondsUntilNextAutostartedGame--;
            planNextGame();
        }
    }

    public void broadcastState() {
        String gameId = currentGame != null ? currentGame.getGameId() : null;
        List<String> onlinePlayers = connectedPlayers.stream().map(Player::getName).collect(Collectors.toList());
        List<ArenaUpdateEvent.ArenaHistory> history = rater
                .getGameResults()
                .stream()
                .limit(50)
                .map(gr -> new ArenaUpdateEvent.ArenaHistory(gr.gameId, gr.positions))
                .collect(Collectors.toList());
        globalEventBus.post(new InternalGameEvent(
                System.currentTimeMillis(), new ArenaUpdateEvent(arenaName, gameId, ranked, rater.getRating(), onlinePlayers, history)));
    }

    private void processCurrentGame() {
        if (currentGame == null) {
            return;
        }

        if (currentGame.isEnded() && viewersHaveFinished(currentGame)) {
            processEndedGame();
            if (ranked) {
                currentGame = null;
                secondsUntilNextAutostartedGame = 0;
                broadcastState();
            }
            // else: game is left for players to see in the arena
        }
    }

    // Because the game engine can run faster than the viewers, we have to calculate if they have finished the game.
    private boolean viewersHaveFinished(Game currentGame) {
        long ticks = currentGame.getGameEngine().getCurrentWorldTick();
        double elapsedSeconds = System.nanoTime() / 1e9 - currentGameStartTime;
        return elapsedSeconds > 5 + currentGame.getGameEngine().getCurrentWorldTick() * 0.25 + 5;
    }

    private void planNextGame() {
        if (currentGame != null && !currentGame.isEnded() && secondsUntilNextAutostartedGame < 10) {
            log.warn("Arena game {} has exceeded maximum game time, aborting and starting a new game", currentGame.getGameId());
            currentGame.abort();
            currentGame = null;
        }

        if (connectedPlayers.size() < 2) {
            log.trace("Not enough players to start arena game");
            return;
        }

        if (secondsUntilNextAutostartedGame <= 0) {
            // TODO This arbitrary formula (5 min between games) can use some tweaking
            secondsUntilNextAutostartedGame = 60 * 5;
            startGame();
        } else {
            log.trace(String.format("Waiting %d seconds until next game", secondsUntilNextAutostartedGame));
        }
    }

    public void requestGameStart() {
        if (!ranked) {
            if (currentGame != null && currentGame.isEnded()) {
                // Sanity check if a game is restarted before viewersHaveFinished is set to true
                processEndedGame();
            }
            startGame();
        }
    }

    private void startGame() {
        // TODO add a taboo list and prefer players that have not played recently
        Set<Player> players = TournamentUtil.getRandomPlayers(connectedPlayers, ARENA_PLAYER_COUNT);

        if (currentGame != null && !currentGame.isEnded()) {
            currentGame.abort();
        }

        currentGame = gameManager.createArenaGame();
        currentGame.setOutgoingEventBus(outgoingEventBus);
        players.forEach(player -> {
            // This object is mutable, we need a new one each game
            RemotePlayer remotePlayer = new RemotePlayer(player, outgoingEventBus);
            currentGame.addPlayer(remotePlayer);
        });
        currentGame.startGame();
        currentGameStartTime = System.nanoTime() / 1e9;
        log.info("Started game in arena {} with id {}", arenaName, currentGame.getGameId());

        broadcastState();
    }

    private void processEndedGame() {
        if (!rater.hasGame(currentGame.getGameId())) {
            rater.addGameToResult(currentGame, ranked);
            broadcastState();
        }
    }

    public EventBus getOutgoingEventBus() {
        return outgoingEventBus;
    }

    public EventBus getIncomingEventBus() {
        return incomingEventBus;
    }

    public void setArenaName(String arenaName) {
        this.arenaName = arenaName;
    }

    public String getArenaName() {
        return arenaName;
    }

    public void setRanked(boolean ranked) {
        this.ranked = ranked;
    }
}