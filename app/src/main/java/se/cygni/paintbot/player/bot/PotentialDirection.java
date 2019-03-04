package se.cygni.paintbot.player.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.paintbot.api.model.*;

/**
 * @author Alan Tibbetts
 * @since 13/04/16
 */
public class PotentialDirection implements Comparable<PotentialDirection> {

    public static CharacterAction[] POSSIBLE_DIRECTIONS = {CharacterAction.UP, CharacterAction.DOWN, CharacterAction.LEFT, CharacterAction.RIGHT};

    private static final Logger log = LoggerFactory.getLogger(PotentialDirection.class);

    static final int DEFAULT_KEEP_GOING_SCORE = 10;
    static final int DEFAULT_EMPTY_SCORE = 100;
    static final int DEFAULT_FOOD_SCORE = 400;
    static final int DEFAULT_AVOID_SCORE = -100;
    static final int DEFAULT_SNAKE_TAIL_SCORE = 0;
    static final int DEFAULT_OUT_OF_BOUNDS_SCORE = -100;

    private final CharacterAction direction;

    private int keepGoingScore = DEFAULT_KEEP_GOING_SCORE;
    private int emptyScore = DEFAULT_EMPTY_SCORE;
    private int foodScore = DEFAULT_FOOD_SCORE;
    private int avoidScore = DEFAULT_AVOID_SCORE;
    private int paintbotTailScore = DEFAULT_SNAKE_TAIL_SCORE;
    private int outOfBoundsScore = DEFAULT_OUT_OF_BOUNDS_SCORE;

    private Integer score = 0;

    public PotentialDirection(final CharacterAction direction) {
        this.direction = direction;
    }

    public CharacterAction getDirection() {
        return direction;
    }

    public int getScore() {
        return score;
    }

    public void addTile(final Map gameMap, final TileContent tileContent, final int distanceFromPaintbot, final String playerId) {
        int scoreToAdd = 0;
        switch (tileContent.getContent()) {
            case "empty":
                scoreToAdd = getEmptyScore() / distanceFromPaintbot;
                break;
            case "food":
                scoreToAdd = getFoodScore() / distanceFromPaintbot;
                break;
            case "paintbotbody":
                if (getPaintbotTailScore() > 0 && tileContent instanceof MapPaintbotBody) {
                    MapPaintbotBody mapPaintbotBody = (MapPaintbotBody) tileContent;
                    if (mapPaintbotBody.isTail() && !mapPaintbotBody.getPlayerId().equals(playerId)) {
                        CharacterInfo paintbot = findPaintbot(gameMap, mapPaintbotBody.getPlayerId());
                        /*if (paintbot != null && paintbot.getTailProtectedForGameTicks() > 0) {
                            scoreToAdd = -scoreToAdd;  // Don't eat protected paintbots!
                        } else {*/
                        scoreToAdd = getPaintbotTailScore();  // Don't care how far away, we want to eat!
                        //}
                    } else {  // Not the tail
                        scoreToAdd = getAvoidScore() / distanceFromPaintbot;
                    }
                } else { // CharacterImpl
                    scoreToAdd = (2 * getAvoidScore()) / distanceFromPaintbot;  // Really want to avoid heads!
                }
                break;
            case "obstacle":
                scoreToAdd = getAvoidScore() / distanceFromPaintbot;
                break;
            default:
                break;
        }
        this.score += scoreToAdd;
    }

    private CharacterInfo findPaintbot(final Map gameMap, final String playerId) {
        for (CharacterInfo characterInfo : gameMap.getCharacterInfos()) {
            if (characterInfo.getId().equals(playerId)) {
                return characterInfo;
            }
        }
        return null;
    }

    @Override
    public int compareTo(PotentialDirection o) {
        return this.score.compareTo(o.getScore());
    }

    public void goingThisWayAnyway() {
        this.score = this.score + getKeepGoingScore();
    }

    public int getKeepGoingScore() {
        return keepGoingScore;
    }

    public void setKeepGoingScore(int keepGoingScore) {
        this.keepGoingScore = keepGoingScore;
    }

    public int getEmptyScore() {
        return emptyScore;
    }

    public void setEmptyScore(int emptyScore) {
        this.emptyScore = emptyScore;
    }

    public int getFoodScore() {
        return foodScore;
    }

    public void setFoodScore(int foodScore) {
        this.foodScore = foodScore;
    }

    public int getAvoidScore() {
        return avoidScore;
    }

    public void setAvoidScore(int avoidScore) {
        this.avoidScore = avoidScore;
    }

    public int getPaintbotTailScore() {
        return paintbotTailScore;
    }

    public void setPaintbotTailScore(int paintbotTailScore) {
        this.paintbotTailScore = paintbotTailScore;
    }

    public int getOutOfBoundsScore() {
        return outOfBoundsScore;
    }

    public void setOutOfBoundsScore(int outOfBoundsScore) {
        this.outOfBoundsScore = outOfBoundsScore;
    }

    public void applyOutOfBoundsScore() {
        this.score = this.score + getOutOfBoundsScore();
    }

    public static boolean isOppositeDirection(final CharacterAction currentDirection, final CharacterAction potentialDirection) {
        boolean oppositeDirection = false;
        if (currentDirection != null) {
            switch (currentDirection) {
                case UP:
                    oppositeDirection = potentialDirection == CharacterAction.DOWN;
                    break;
                case DOWN:
                    oppositeDirection = potentialDirection == CharacterAction.UP;
                    break;

                case LEFT:
                    oppositeDirection = potentialDirection == CharacterAction.RIGHT;
                    break;

                case RIGHT:
                    oppositeDirection = potentialDirection == CharacterAction.LEFT;
                    break;

                default:
                    throw new RuntimeException("Unknown Action: " + potentialDirection.toString());
            }
        }
        return oppositeDirection;
    }

    @Override
    public String toString() {
        return "PotentialDirection{" +
                "direction=" + direction +
                ", score=" + score +
                '}';
    }
}
