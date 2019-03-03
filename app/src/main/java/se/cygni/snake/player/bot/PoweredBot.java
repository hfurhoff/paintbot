package se.cygni.snake.player.bot;

import com.google.common.eventbus.EventBus;
import se.cygni.game.random.XORShiftRandom;
import se.cygni.snake.api.event.GameStartingEvent;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.model.CharacterAction;
import se.cygni.snake.api.model.CharacterInfo;
import se.cygni.snake.client.MapCoordinate;
import se.cygni.snake.client.MapUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static se.cygni.snake.api.model.CharacterAction.EXPLODE;
import static se.cygni.snake.api.model.CharacterAction.STAY;

public class PoweredBot extends BotPlayer {
    private XORShiftRandom random = new XORShiftRandom();
    int explosionRange;

    public PoweredBot(String playerId, EventBus incomingEventbus) {
        super(playerId, incomingEventbus);
    }

    @Override
    public void onGameStart(GameStartingEvent gameStartingEvent) {
        explosionRange = gameStartingEvent.getGameSettings().getExplosionRange();
    }

    @Override
    public void onWorldUpdate(MapUpdateEvent mapUpdateEvent) {
        MapUtil mapUtil = new MapUtil(mapUpdateEvent.getMap(), getPlayerId());
        boolean shouldExplode = shouldExplode(mapUtil, mapUpdateEvent);
        boolean isCarryingBomb = isCarryingBomb(playerId, mapUpdateEvent);
        if (isCarryingBomb && shouldExplode) {
            registerMove(mapUpdateEvent, EXPLODE);
            return;
        }

        if (isCarryingBomb && !shouldExplode) {
            MapCoordinate closestEnemy = findClosestEnemy(mapUtil, mapUpdateEvent);
            CharacterAction chosenDirection = getDirection(mapUtil, closestEnemy);
            registerMove(mapUpdateEvent, chosenDirection);
        }

        if (!isCarryingBomb) {
            MapCoordinate closestPowerUp = findClosestPowerUp(mapUtil);
            if (closestPowerUp == null) {
                registerMove(mapUpdateEvent, STAY);
                return;
            }
            CharacterAction chosenDirection = getDirection(mapUtil, closestPowerUp);
            registerMove(mapUpdateEvent, chosenDirection);
        }

        registerMove(mapUpdateEvent, STAY);
    }

    private MapCoordinate findClosestEnemy(MapUtil mapUtil, MapUpdateEvent mapUpdateEvent) {
        MapCoordinate closest = null;
        int minDistance = Integer.MAX_VALUE;
        for (CharacterInfo player : mapUpdateEvent.getMap().getCharacterInfos()) {
            if (!player.getId().equals(playerId)) {
                int distance = mapUtil.getMyPosition().getManhattanDistanceTo(mapUtil.translatePosition(player.getPosition()));
                if (distance < minDistance) {
                    minDistance = distance;
                    closest = mapUtil.translatePosition(player.getPosition());
                }
            }
        }

        return closest;
    }

    private boolean isCarryingBomb(String playerId, MapUpdateEvent mapUpdateEvent) {
        for (CharacterInfo player : mapUpdateEvent.getMap().getCharacterInfos()) {
            if (player.getId().equals(playerId)) {
                return player.isCarryingBomb();
            }
        }
        throw new IllegalStateException("Current player does not exist");
    }

    private boolean shouldExplode(MapUtil mapUtil, MapUpdateEvent mapUpdateEvent) {
        MapCoordinate myPosition = mapUtil.getMyPosition();
        CharacterInfo[] players = mapUpdateEvent.getMap().getCharacterInfos();

        int minDist = Integer.MAX_VALUE;
        for (CharacterInfo player : players) {
            if (!player.getId().equals(playerId)) {
                int dist = myPosition.getManhattanDistanceTo(mapUtil.translatePosition(player.getPosition()));
                minDist = Math.min(dist, minDist);
            }
        }

        return minDist <= explosionRange;
    }

    private CharacterAction getDirection(MapUtil mapUtil, MapCoordinate closestPowerUp) {
        MapCoordinate myPosition = mapUtil.getMyPosition();
        List<CharacterAction> possibleActions = new ArrayList<>();
        if (closestPowerUp.x < myPosition.x) {
            possibleActions.add(CharacterAction.LEFT);
        } else if (closestPowerUp.x > myPosition.x) {
            possibleActions.add(CharacterAction.RIGHT);
        }

        if (closestPowerUp.y < myPosition.y) {
            possibleActions.add(CharacterAction.UP);
        } else if (closestPowerUp.y > myPosition.y) {
            possibleActions.add(CharacterAction.DOWN);
        }

        CharacterAction chosenDirection = STAY;
        List<CharacterAction> validActions = possibleActions.stream().filter(mapUtil::canIMoveInDirection)
            .collect(Collectors.toList());

        if (!validActions.isEmpty()) {
            chosenDirection = validActions.get(random.nextInt(validActions.size()));
        }
        return chosenDirection;
    }

    private MapCoordinate findClosestPowerUp(MapUtil mapUtil) {
        MapCoordinate closestPowerUp = null;

        int closestPowerUpDistance = Integer.MAX_VALUE;
        MapCoordinate myPosition = mapUtil.getMyPosition();

        for (MapCoordinate mc : mapUtil.listCoordinatesContainingBombs()) {
            if (closestPowerUp == null) {
                closestPowerUp = mc;
                closestPowerUpDistance = closestPowerUp.getManhattanDistanceTo(myPosition);
            } else {
                int distance = myPosition.getManhattanDistanceTo(mc);
                if (distance < closestPowerUpDistance) {
                    closestPowerUp = mc;
                    closestPowerUpDistance = distance;
                }
            }

        }

        return closestPowerUp;
    }

}
