package se.cygni.snake.game;

import com.google.common.eventbus.EventBus;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.game.Coordinate;
import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.enums.Action;
import se.cygni.game.exception.OutOfBoundsException;
import se.cygni.game.exception.TransformationException;
import se.cygni.game.random.XORShiftRandom;
import se.cygni.game.worldobject.Bomb;
import se.cygni.game.worldobject.Character;
import se.cygni.game.worldobject.CharacterImpl;
import se.cygni.game.worldobject.Empty;
import se.cygni.game.worldobject.Obstacle;
import se.cygni.game.worldobject.WorldObject;
import se.cygni.snake.api.event.CharacterStunnedEvent;
import se.cygni.snake.api.model.PointReason;
import se.cygni.snake.api.model.StunReason;
import se.cygni.snake.apiconversion.GameMessageConverter;
import se.cygni.snake.event.InternalGameEvent;
import se.cygni.snake.player.IPlayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class WorldUpdater {

    private static final Logger log = LoggerFactory.getLogger(WorldUpdater.class);
    private XORShiftRandom random = new XORShiftRandom();
    private final GameFeatures gameFeatures;
    private final PlayerManager playerManager;
    private final String gameId;
    private final EventBus globalEventBus;
    private ThreadLocal<WorldState> startingWorldState = new ThreadLocal<>();

    public WorldUpdater(GameFeatures gameFeatures, PlayerManager playerManager, String gameId, EventBus globalEventBus) {
        this.gameFeatures = gameFeatures;
        this.playerManager = playerManager;
        this.gameId = gameId;
        this.globalEventBus = globalEventBus;
    }

    public WorldState update(
            Map<String, Action> actions,
            GameFeatures gameFeatures,
            WorldState ws,
            long worldTick) throws TransformationException {

        startingWorldState.set(ws);

        WorldState nextWorld = new WorldState(ws);

        Map<String, Integer> originalPositions = actions.entrySet().stream().collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        e -> ws.getCharacterById(e.getKey()).getPosition()
                )
        );
        ConcurrentHashMap<Integer, List<String>> updatedPositions = new ConcurrentHashMap<>();
        var stunnedPlayers = new HashSet<String>();

        actions.entrySet().forEach(entry -> {
            String player = entry.getKey();
            Action action = entry.getValue();
            int nextPosition = originalPositions.get(player);
            if (action.isMovement() && canPerform(ws, player)) {
                nextPosition = getNextPosition(entry, ws);
                if (ws.isTileContentOfType(nextPosition, Obstacle.class) || isCollidingWithNeighbour(ws, entry, actions)) {
                    stunnedPlayers.add(player);
                    nextPosition = originalPositions.get(player);
                }
            }

            updatePosition(updatedPositions, player, nextPosition);
        });

        // Move all colliding player to original position
        while (updatedPositions.entrySet().stream().anyMatch(e -> e.getValue().size() > 1)) {
            var collidingPlayers = updatedPositions.entrySet()
                    .stream()
                    .filter(e -> e.getValue().size() > 1)
                    .flatMap(e -> e.getValue().stream().map(p -> Pair.of(e.getKey(), p)))
                    .collect(Collectors.toList());
            collidingPlayers.forEach(positionPlayerPair -> {
                String player = positionPlayerPair.getValue();
                Integer collidingPosition = positionPlayerPair.getKey();
                stunnedPlayers.add(player);
                List<String> playersAtCollidingPosition = updatedPositions.get(collidingPosition);
                List<String> withoutPlayer = playersAtCollidingPosition.stream().filter(p -> !p.equals(player))
                        .collect(toList());
                updatedPositions.put(collidingPosition, withoutPlayer);
                updatePosition(updatedPositions, player, originalPositions.get(player));
            });
        }

        updatedPositions.forEach((p, l) -> {
            if (l.isEmpty()) {
                updatedPositions.remove(p);
            }
        });

        Tile[] tiles = nextWorld.getTiles();

        for (int position = 0; position < tiles.length; position++) {
            if (updatedPositions.containsKey(position)) {
                String playerId = updatedPositions.get(position).get(0);
                var hasPickedUpBomb = ws.getTile(position).getContent() instanceof Bomb;
                updateCharacterState(tiles, position, nextWorld.getCharacterById(playerId), hasPickedUpBomb);
            } else if (originalPositions.containsValue(position)) {
                tiles[position] = new Tile(new Empty(), ws.getTile(position).getOwnerID());
            }
        }

        WorldState positionUpdatedWorld = new WorldState(ws.getWidth(), ws.getHeight(), tiles);

        Map<Integer, List<String>> positionsBombed = new HashMap<>();
        actions.entrySet().stream()
                .filter(e ->
                        e.getValue().equals(Action.EXPLODE) &&
                                ws.getCharacterById(e.getKey()).isCarryingBomb() &&
                                canPerform(ws, e.getKey())
                )
                .forEach(e -> {
                    String playerId = e.getKey();
                    var player = positionUpdatedWorld.getCharacterById(playerId);
                    player.setCarryingBomb(false);
                    int position = player.getPosition();
                    Coordinate myCoordinate = positionUpdatedWorld.translatePosition(position);

                    for (int dx = myCoordinate.getX() - gameFeatures.getExplosionRange();
                         dx <= myCoordinate.getX() + gameFeatures.getExplosionRange();
                         dx++
                    ) {
                        for (int dy = myCoordinate.getY() - gameFeatures.getExplosionRange();
                             dy <= myCoordinate.getY() + gameFeatures.getExplosionRange();
                             dy++) {

                            Coordinate coordinate = new Coordinate(dx, dy);
                            if (isWithinBounds(ws, coordinate) &&
                                    manhattanDistance(myCoordinate, coordinate) <= gameFeatures.getExplosionRange() &&
                                    !coordinate.equals(myCoordinate)
                            ) {
                                int currPos = positionUpdatedWorld.translateCoordinate(coordinate);
                                positionUpdatedWorld.getTile(currPos);

                                List<String> playersBombingPosition = positionsBombed.getOrDefault(currPos, new LinkedList<>());
                                playersBombingPosition.add(playerId);
                                positionsBombed.put(currPos, playersBombingPosition);
                            }
                        }

                    }
                });

        nextWorld.setBombings(positionsBombed);

        positionsBombed.forEach((position, players) -> {
            WorldObject content = positionUpdatedWorld.getTile(position).getContent();
            // Randomly select one player to successfully bomb
            String playerId = players.get(random.nextInt(players.size()));
            if (content instanceof Empty || content instanceof Bomb) {
                tiles[position] = new Tile(content, playerId);
            } else if (content instanceof Character) {
                stunnedPlayers.add(positionUpdatedWorld.getCharacterAtPosition(position).getPlayerId());
            }
        });

        // Set colliding players to stunned
        stunnedPlayers.forEach(p -> nextWorld.getCharacterById(p).setIsStunnedForTicks(gameFeatures.getNoOfTicksStunned()));

        playerManager.getLivePlayers().forEach(p -> {
            int ownedTiles = positionUpdatedWorld.listPositionWithOwner(p.getPlayerId()).length;
            p.addPoints(PointReason.OWNED_TILES, ownedTiles);
            positionUpdatedWorld.getCharacterById(p.getPlayerId()).setPoints(p.getTotalPoints());
        });

        return new WorldState(ws.getWidth(), ws.getHeight(), tiles, nextWorld.getCollisions(), nextWorld.getBombings());
    }

    private void updatePosition(ConcurrentHashMap<Integer, List<String>> updatedPositions, String player, Integer newPosition) {
        List<String> playersAtPosition = updatedPositions.getOrDefault(newPosition, new LinkedList<>());
        playersAtPosition.add(player);
        updatedPositions.put(newPosition, playersAtPosition);
    }

    private boolean isCollidingWithNeighbour(WorldState ws, Map.Entry<String, Action> entry, Map<String, Action> actions) {
        int nextPosition = getNextPosition(entry, ws);
        if (ws.isTileContentOfType(nextPosition, CharacterImpl.class)) {
            String otherPlayer = ws.getCharacterAtPosition(nextPosition).getPlayerId();
            return actions.get(otherPlayer).isOppositeMovement(entry.getValue());
        }

        return false;
    }

    private int manhattanDistance(Coordinate myCoordinate, Coordinate coordinate) {
        return Math.abs(myCoordinate.getX() - coordinate.getX()) + Math.abs(myCoordinate.getY() - coordinate.getY());
    }

    private boolean isWithinBounds(WorldState worldState, Coordinate coordinate) {
        return coordinate.getX() >= 0 && coordinate.getX() < worldState.getWidth()
                && coordinate.getY() >= 0 && coordinate.getY() < worldState.getHeight();
    }

    private boolean canPerform(WorldState ws, String playerId) {
        return ws.getCharacterById(playerId).getIsStunnedForTicks() == 0;
    }


    private void updateCharacterState(Tile[] tiles, int targetPosition, Character character, boolean hasPickedUpBomb) {
        character.setPosition(targetPosition);
        if (hasPickedUpBomb) {
            character.setCarryingBomb(true);
        }
        tiles[targetPosition] = new Tile(character, character.getPlayerId());
    }

    private int getNextPosition(Map.Entry<String, Action> updateEntry, WorldState ws) {
        var currentPos = ws.getCharacterById(updateEntry.getKey()).getPosition();
        try {
            return ws.getPositionForAdjacent(currentPos, updateEntry.getValue());
        } catch (OutOfBoundsException e) {
            //Don't run out of the map, you'll get nowhere.
            return currentPos;
        }
    }


    private void syncPoints(WorldState ws) {
        playerManager.toSet().forEach(player -> {
            if (player.isAlive())
                ws.getCharacterById(player.getPlayerId()).setPoints(player.getTotalPoints());
        });
    }

    private void notifyCharacterStunned(
            CharacterImpl head,
            StunReason stunReason,
            Coordinate coordinate,
            long worldTick) {

        IPlayer stunnedCharacter = playerManager.getPlayer(head.getPlayerId());
        log.info("Stun occurred by: {}. GameId: {}, Player: {}, with id: {}, stunned at: {}",
                stunReason,
                gameId,
                stunnedCharacter.getName(),
                stunnedCharacter.getPlayerId(),
                coordinate);

        stunnedCharacter.stunned(worldTick);

        CharacterStunnedEvent characterStunnedEvent = GameMessageConverter.onPlayerStunned(
                stunReason,
                gameFeatures.getNoOfTicksStunned(),
                head.getPlayerId(),
                coordinate.getX(), coordinate.getY(),
                gameId, worldTick);

        playerManager.toSet().stream().forEach(player -> {
            player.onCharacterStunned(characterStunnedEvent);
        });

        InternalGameEvent gevent = new InternalGameEvent(
                System.currentTimeMillis(),
                characterStunnedEvent);
        globalEventBus.post(gevent);
    }
}
