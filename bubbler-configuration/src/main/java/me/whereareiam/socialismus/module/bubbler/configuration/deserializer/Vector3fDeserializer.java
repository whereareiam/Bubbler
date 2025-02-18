package me.whereareiam.socialismus.module.bubbler.configuration.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.retrooper.packetevents.util.Vector3f;

import java.io.IOException;

public class Vector3fDeserializer extends JsonDeserializer<Vector3f> {
    @Override
    public Vector3f deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = parser.getCodec();
        JsonNode root = codec.readTree(parser);
        
        float x = (float) root.get("x").asDouble();
        float y = (float) root.get("y").asDouble();
        float z = (float) root.get("z").asDouble();

        return new Vector3f(x, y, z);
    }
}
