package se.cygni.paintbot.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import se.cygni.paintbot.api.deserializer.TileContentDeserializer;

@JsonDeserialize(using = TileContentDeserializer.class)
public interface TileContent {
    String getContent();
    String toDisplay();
}
