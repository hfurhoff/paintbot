package se.cygni.game;

import se.cygni.game.worldobject.Character;
import se.cygni.game.worldobject.CharacterImpl;
import se.cygni.game.worldobject.Empty;
import se.cygni.game.worldobject.PowerUp;
import se.cygni.game.worldobject.WorldObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TileMultipleContent {

    private final List<WorldObject> contents;

    public TileMultipleContent() {
        contents = new ArrayList<>();
    }

    public TileMultipleContent(WorldObject content) {
        this();
        if (!(content instanceof Empty))
            contents.add(content);
    }

    public void addContent(WorldObject content) {
        // Empty tiles are not stored
        if (content instanceof Empty)
            return;

        // Do not store already added contents
        if (contents.contains(content)) {
            return;
        }

        contents.add(content);
    }

    public WorldObject getContent() {
        if (!isValidCombinationOfContents()) {
            throw new IllegalStateException("Invalid combination of contents in this Tile");
        }

        if (contents.isEmpty()) {
            return new Empty();
        }

        if (contents.size() == 2 &&
                containsType(PowerUp.class) && containsType(CharacterImpl.class)) {
            return getFirstContentOfType(CharacterImpl.class);
        }

        if (contents.size() == 1) {
            return contents.get(0);
        }

        // ToDo: Add more cases here.
        throw new IllegalStateException("Found no way of calculating resulting content");
    }

    public List<WorldObject> getContents() {
        List<WorldObject> contentsCopy = new ArrayList<>();
        contentsCopy.addAll(contents);
        return contentsCopy;
    }

    public int size() {
        return contents.size();
    }

    public boolean hasContent() {
        return contents.size() > 0;
    }

    public boolean hasSingleContent() {
        return contents.size() == 1;
    }

    public boolean isValidCombinationOfContents() {
        if (!hasContent()) {
            return true;
        }

        if (hasSingleContent()) {
            return true;
        }

        if (contents.size() == 2 &&
                containsType(PowerUp.class) && containsType(CharacterImpl.class)) {
            return true;
        }

        if (contents.size() >= 2) {
            return false;
        }

        return true;
    }

    public <T extends WorldObject> boolean containsExactlyOneOfEachType(Class<T>... types) {
        if (types.length != contents.size())
            return false;

        for (Class<T> clazz : types) {
            if (countInstancesOf(clazz) != 1)
                return false;
        }
        return true;
    }

    public <T extends WorldObject> void removeType(Class<T> type) {
        contents.remove(getFirstContentOfType(type));
    }

    public List<CharacterImpl> listOffendingPaintbotHeads() {
        return listContentsOfType(CharacterImpl.class);
    }

    public List<String> listOffendingPaintbotHeadIds() {
        return listContentsOfType(CharacterImpl.class)
                .stream()
                .map(paintbotHead -> paintbotHead.getPlayerId())
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public <T extends WorldObject> List<T> listContentsOfType(Class<T> type) {
        if (!hasContent()) {
            return new ArrayList<>();
        }

        return contents.stream()
                .filter(worldObject -> worldObject.getClass().equals(type)).map(typedObject -> {
                    return (T) typedObject;
                })
                .collect(Collectors.toList());
    }

    public <T extends WorldObject> int countInstancesOf(Class<T> type) {
        return listContentsOfType(type).size();
    }

    public <T extends WorldObject> boolean containsType(Class<T> type) {
        if (!hasContent()) {
            return false;
        }

        for (WorldObject wo : contents) {
            if (wo.getClass().equals(type)) {
                return true;
            }
        }

        return false;
    }

    public List<String> listPaintbotIdsPresent() {
        List<String> paintbotIds = new ArrayList<>();

        for (WorldObject wo : contents) {
            if (wo instanceof Character) {
                paintbotIds.add(((Character) wo).getPlayerId());
            }
        }
        return paintbotIds;
    }

    //Check if a tile contains two character at the same time (useful to know if collisons are to be handled)
    public boolean containsCharacterCollision() {
        if (size() != 2)
            return false;

        WorldObject wo1 = contents.get(0);
        WorldObject wo2 = contents.get(1);

        CharacterImpl character = null;

        return wo1 instanceof CharacterImpl && wo2 instanceof CharacterImpl;
    }

    private <T extends WorldObject> T getFirstContentOfType(Class<T> type) {
        for (WorldObject wo : contents) {
            if (wo.getClass().equals(type)) {
                return (T) wo;
            }
        }

        throw new IllegalStateException("Could not find content of type: " + type);
    }
}
