package se.cygni.game.transformation;

import org.junit.Test;
import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.worldobject.CharacterImpl;

import static org.junit.Assert.assertEquals;

/**
 * @author Alan Tibbetts
 * @since 12/04/16
 */
public class DecrementStunTest {

    @Test
    public void testTransform() {
        CharacterImpl paintbotA = new CharacterImpl("a", "a", 2);
        paintbotA.setIsStunnedForTicks(3);

        Tile[] tiles = new WorldState(3, 3).getTiles();
        tiles[2] = new Tile(paintbotA);
        WorldState worldState = new WorldState(3, 3, tiles);

        DecrementStun decrementStun = new DecrementStun();

        WorldState updatedWorldState = decrementStun.transform(worldState);
        CharacterImpl updatedCharacter = updatedWorldState.getCharacterById("a");
        assertEquals(2, updatedCharacter.getIsStunnedForTicks());
    }

    @Test
    public void testTransformWithZeroCount() {
        CharacterImpl paintbotA = new CharacterImpl("a", "a", 2);

        Tile[] tiles = new WorldState(3, 3).getTiles();
        tiles[2] = new Tile(paintbotA);
        WorldState worldState = new WorldState(3, 3, tiles);

        DecrementStun decrementStun = new DecrementStun();

        WorldState updatedWorldState = decrementStun.transform(worldState);
        CharacterImpl updatedCharacter = updatedWorldState.getCharacterById("a");
        assertEquals(0, updatedCharacter.getIsStunnedForTicks());
    }
}
