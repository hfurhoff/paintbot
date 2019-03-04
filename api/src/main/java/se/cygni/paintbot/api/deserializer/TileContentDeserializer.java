package se.cygni.paintbot.api.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import se.cygni.paintbot.api.model.MapCharacter;
import se.cygni.paintbot.api.model.MapEmpty;
import se.cygni.paintbot.api.model.MapObstacle;
import se.cygni.paintbot.api.model.MapPaintbotBody;
import se.cygni.paintbot.api.model.MapPowerUp;
import se.cygni.paintbot.api.model.TileContent;

import java.io.IOException;

public class TileContentDeserializer extends JsonDeserializer<TileContent> {
    @Override
    public TileContent deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException
    {
        JsonNode node = jp.getCodec().readTree(jp);

        String content = node.get("content").asText();

        String name = "";
        if (node.has("name"))
            name = node.get("name").asText();

        String playerId = "";
        if (node.has("playerId"))
            playerId = node.get("playerId").asText();

        int order = -1;
        if (node.has("order"))
            order = (Integer) ((IntNode) node.get("order")).numberValue();

        boolean tail = false;
        if (node.has("tail"))
            tail = ((BooleanNode) node.get("tail")).booleanValue();

        switch (content) {
            case MapObstacle.CONTENT  : return new MapObstacle();
            case MapPowerUp.CONTENT:
                return new MapPowerUp();
            case MapCharacter.CONTENT : return new MapCharacter(name, playerId);
            case MapPaintbotBody.CONTENT : return new MapPaintbotBody(tail, playerId, order);
            default: return new MapEmpty();
        }
    }
}