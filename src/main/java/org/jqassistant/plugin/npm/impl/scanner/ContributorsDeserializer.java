package org.jqassistant.plugin.npm.impl.scanner;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContributorsDeserializer extends JsonDeserializer<List<Map<String, Object>>> {
    @Override
    public List<Map<String, Object>> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        List<Map<String, Object>> result = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode element : node) {
                result.add(parseContributor(element));
            }
        } else {
            result.add(parseContributor(node));
        }
        return result;
    }

    private Map<String, Object> parseContributor(JsonNode node) {
        Map<String, Object> contributor = new HashMap<>();
        if (node.isTextual()) {
            contributor.put("", node.asText());
        } else if (node.isObject()) {
            node.fields().forEachRemaining(entry -> contributor.put(entry.getKey(), entry.getValue().asText()));
        }
        return contributor;
    }
}
