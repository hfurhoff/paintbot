package se.cygni.paintbot.api.util;

import se.cygni.paintbot.api.model.GameSettings;

public class GameSettingsUtils {

    public static GameSettings trainingWorld() {
        GameSettings settings = new GameSettings();
        settings.setMaxNoofPlayers(5);
        settings.setBombsEnabled(true);
        settings.setObstaclesEnabled(true);
        return settings;
    }
}
