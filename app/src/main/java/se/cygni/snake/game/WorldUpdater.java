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
import se.cygni.game.worldobject.WorldObject;
import se.cygni.snake.api.event.CharacterStunnedEvent;
import se.cygni.snake.api.model.StunReason;
import se.cygni.snake.apiconversion.GameMessageConverter;
import se.cygni.snake.event.InternalGameEvent;
import se.cygni.snake.player.IPlayer;

import java.util.Arrays;
import java.util.HashMap;
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

        ConcurrentHashMap<Integer, List<String>> updatedPositions = new ConcurrentHashMap<>(actions.entrySet().stream()
                .filter(e -> e.getValue().isMovement())
                .map(e -> getNextPosition(e, ws))
                .collect(Collectors.groupingBy(Pair::getKey, Collectors.mapping(Pair::getValue, Collectors.toList()))));

        var stunnedPlayers = new LinkedList<String>();

        // Move all colliding player to original position
        while(updatedPositions.entrySet().stream().anyMatch(e -> e.getValue().size() > 1)){
            var collidingEntries = updatedPositions.entrySet()
                    .stream()
                    .filter(e -> e.getValue().size() > 1)
                    .collect(toList());
            for (var entry : collidingEntries) {
                Map<Integer, List<String>> collisions = nextWorld.getCollisions();
                int position = entry.getKey();
                List<String> collisionsToReport = collisions.getOrDefault(position, new LinkedList<>());
                var colliders = updatedPositions.get(position);
                collisionsToReport.addAll(colliders);
                collisions.put(position, collisionsToReport);

                for(var player : entry.getValue()) {
                    stunnedPlayers.add(player);
                    Integer originalPosition = originalPositions.get(player);
                    List<String> playersAtPos = updatedPositions.getOrDefault(originalPosition, new LinkedList<>());
                    playersAtPos.add(player);
                    colliders.remove(player);
                    updatedPositions.put(originalPosition, playersAtPos);
                }
            }
        }

        // Set colliding players to stunned
        stunnedPlayers.forEach(p -> nextWorld.getCharacterById(p).setIsStunnedForTicks(gameFeatures.getNoOfTicksStunned()));

        Tile[] tiles = nextWorld.getTiles();

        for(var e : updatedPositions.entrySet()) {
            for(var p : e.getValue()) {
                int targetPosition = e.getKey();
                var hasPickedUpBomb = ws.getTile(targetPosition).getContent() instanceof Bomb;
                updateCharacterState(tiles, targetPosition, nextWorld.getCharacterById(p), hasPickedUpBomb);
            }
        }

        Map<Integer, List<String>> positionsBombed = new HashMap<>();
        actions.entrySet().stream()
                .filter(e -> e.getValue().equals(Action.EXPLODE) && ws.getCharacterById(e.getKey()).isCarryingBomb())
                .forEach(e -> {
                    String playerId = e.getKey();
                    var player = nextWorld.getCharacterById(playerId);
                    player.setCarryingBomb(false);
                    int position = player.getPosition();
                    Arrays.stream(nextWorld.listAdjacentTiles(position))
                            .forEach(adjacentPosition -> {
                                List<String> playersBombingPosition = positionsBombed.getOrDefault(adjacentPosition, new LinkedList<>());
                                playersBombingPosition.add(playerId);
                                positionsBombed.put(adjacentPosition, playersBombingPosition);
                            });
                });

        nextWorld.setBombings(positionsBombed);

        positionsBombed.forEach((position, players) -> {
            WorldObject content = nextWorld.getTile(position).getContent();
            if (content instanceof Empty) {
                // Randomly select one player to successfully bomb
                String playerId = players.get(random.nextInt(players.size()));
                tiles[position] = new Tile(new Empty(), playerId);
            } else if (content instanceof Character) {
                nextWorld.getCharacterAtPosition(position).setIsStunnedForTicks(gameFeatures.getNoOfTicksStunned());
            }

        });


        return new WorldState(ws.getWidth(), ws.getHeight(), tiles, nextWorld.getCollisions(), nextWorld.getBombings());

    }


    private void updateCharacterState(Tile[] tiles, int targetPosition, Character character, boolean hasPickedUpBomb) {
        Tile currentTile = tiles[character.getPosition()];
        tiles[character.getPosition()] = new Tile(new Empty(), currentTile.getOwnerID());
        tiles[targetPosition] = new Tile(character, character.getPlayerId());
        character.setPosition(targetPosition);
        character.setCarryingBomb(hasPickedUpBomb);
    }

    private Pair<Integer, String> getNextPosition(Map.Entry<String, Action> updateEntry, WorldState ws) {
        var currentPos = ws.getCharacterById(updateEntry.getKey()).getPosition();
        try {
            return Pair.of(ws.getPositionForAdjacent(currentPos, updateEntry.getValue()), updateEntry.getKey());
        } catch (OutOfBoundsException e) {
            //Don't run out of the map, you'll get nowhere.
            return Pair.of(currentPos, updateEntry.getKey());
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
