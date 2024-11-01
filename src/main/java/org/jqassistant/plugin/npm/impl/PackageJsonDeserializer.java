package org.jqassistant.plugin.npm.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.jqassistant.plugin.npm.impl.model.Bugs;
import org.jqassistant.plugin.npm.impl.model.Funding;
import org.jqassistant.plugin.npm.impl.model.Package;
import org.jqassistant.plugin.npm.impl.model.Person;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manually implements the deserialization of the package.json to allow for arbitrary anomalies
 */
@Slf4j
public class PackageJsonDeserializer extends JsonDeserializer<Package> {

    Pattern personPattern = Pattern.compile("([^<>()]+[^ <>()])( <.+>)?( \\(.+\\))?");

    @Override
    public Package deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        Package result = new Package();

        if(node.isObject()) {
            node.fields().forEachRemaining(packageJsonProperty -> {
                JsonNode valueNode = packageJsonProperty.getValue();
                switch (packageJsonProperty.getKey()) {
                    case "name": result.setName(deserializeStringProperty("name", valueNode)); break;
                    case "version": result.setVersion(deserializeStringProperty("version", valueNode)); break;
                    case "description": result.setDescription(deserializeStringProperty("description", valueNode)); break;
                    case "keywords": result.setKeywords(deserializeStringArrayProperty("keywords", valueNode)); break;
                    case "homepage": result.setHomepage(deserializeStringProperty("homepage", valueNode)); break;
                    case "bugs": result.setBugs(deserializeBugsProperty(valueNode)); break;
                    case "license": result.setLicense(deserializeStringProperty("license", valueNode)); break;
                    case "author": result.setAuthor(deserializePersonProperty("author", valueNode)); break;
                    case "contributors": result.setContributors(deserializeContributorsProperty(valueNode)); break;
                    case "funding": result.setFunding(deserializeFundingProperty(valueNode)); break;
                    case "files": result.setFiles(deserializeStringArrayProperty("files", valueNode)); break;
                    case "main": result.setMain(deserializeStringProperty("main", valueNode)); break;
                    case "browser": result.setBrowser(deserializeStringProperty("browser", valueNode)); break;
                    case "scripts": result.setScripts(deserializeStringMap("scripts", valueNode)); break;
                    case "dependencies": result.setDependencies(deserializeStringMap("dependencies", valueNode)); break;
                    case "devDependencies": result.setDevDependencies(deserializeStringMap("devDependencies", valueNode)); break;
                    case "peerDependencies": result.setPeerDependencies(deserializeStringMap("peerDependencies", valueNode)); break;
                    case "engines": result.setEngines(deserializeStringMap("engines", valueNode)); break;
                    default: log.error("Encountered unknown top-level property in package.json ({})", packageJsonProperty.getKey());
                }
            });
        } else {
            log.error("package.json does not contain a top-level object");
        }

        return result;
    }

    private String deserializeStringProperty(String propertyName, JsonNode node) {
        if(node.isTextual()) {
            return node.asText();
        } else {
            log.error("property {} is not a string", propertyName);
            return null;
        }
    }

    private String[] deserializeStringArrayProperty(String propertyName, JsonNode node) {
        if(node.isArray()) {
            List<String> result = new ArrayList<>();
            node.elements().forEachRemaining(element -> {
                if(element.isTextual()) {
                    result.add(element.asText());
                } else {
                    log.error("property {} contains non-string element (skipping)", propertyName);
                }
            });
            return result.toArray(new String[0]);
        } else {
            log.error("property {} is not a string array", propertyName);
            return new String[0];
        }
    }

    private Map<String, String> deserializeStringMap(String propertyName, JsonNode node) {
        Map<String, String> result = new HashMap<>();
        if(node.isObject()) {
            node.fields().forEachRemaining(field -> {
                JsonNode value = field.getValue();
                if(value.isTextual()) {
                    result.put(field.getKey(), value.textValue());
                } else {
                    log.error("Property {} of {} is not a string", field.getKey(), propertyName);
                }
            });
        } else {
            log.error("property {} is not an object", propertyName);
        }
        return result;
    }

    private Person deserializePersonProperty(String propertyName, JsonNode node) {
        if(node.isTextual()) {
            // single string representation, e.g. "John Doe <contact@example.com> (https://homepage.com)"
            String text = node.asText();
            Matcher matcher = personPattern.matcher(text);
            if(matcher.matches()) {
                String email = matcher.group(2);
                String url = matcher.group(3);
                Person result = new Person();
                result.setName(matcher.group(1));
                if(email != null) {
                    result.setEmail(email.substring(2, email.length() - 1));
                }
                if(url != null) {
                    result.setUrl(url.substring(2, url.length() - 1));
                }
                return result;
            } else {
                log.error("string content of {} does not match pattern for this property", propertyName);
            }
        } else if(node.isObject()) {
            // object representation
            Person result = new Person();
            node.fields().forEachRemaining(entry -> {
                switch (entry.getKey()) {
                    case "name": result.setName(deserializeStringProperty(propertyName + ".name", entry.getValue())); break;
                    case "email": result.setEmail(deserializeStringProperty(propertyName + ".email", entry.getValue())); break;
                    case "url": result.setUrl(deserializeStringProperty(propertyName + ".url", entry.getValue())); break;
                    default: log.error("object content of {} does contain unknown property ({})", propertyName, entry.getKey());
                }
            });
            return result;
        } else {
            log.error("property {} is neither represented through a string nor an object", propertyName);
        }
        return null;
    }

    private List<Person> deserializeContributorsProperty(JsonNode node) {
        List<Person> result = new ArrayList<>();
        if(node.isArray()) {

            int index = 0;
            for (var it = node.elements(); it.hasNext(); index++) {
                JsonNode elem = it.next();
                Person p = deserializePersonProperty("contributors[" + index + "]", elem);
                if(p != null) {
                    result.add(p);
                }
            }
        } else {
            log.error("property contributors is not an array");
        }
        return result;
    }

    private Bugs deserializeBugsProperty(JsonNode node) {
        if(node.isObject()) {
            Bugs result = new Bugs();
            node.fields().forEachRemaining(entry -> {
                switch (entry.getKey()) {
                    case "email": result.setEmail(deserializeStringProperty("bugs.email", entry.getValue())); break;
                    case "url": result.setUrl(deserializeStringProperty("bugs.url", entry.getValue())); break;
                    default: log.error("object content of bugs does contain unknown property ({})", entry.getKey());
                }
            });
            return result;
        } else {
            log.error("property bugs is not an object");
        }
        return null;
    }

    private List<Funding> deserializeFundingProperty(JsonNode node) {
        List<Funding> result = new ArrayList<>();
        if(node.isArray()) {
            int index = 0;
            for (var it = node.elements(); it.hasNext(); index++) {
                JsonNode elem = it.next();
                Funding f = deserializeFundingObject("funding[" + index + "]", elem);
                if(f != null) {
                    result.add(f);
                }
            }
        } else if(node.isObject() || node.isTextual()) {
            Funding f = deserializeFundingObject("funding", node);
            if(f != null) {
                result.add(f);
            }
        }else {
            log.error("property funding is not an array");
        }
        return result;
    }

    private Funding deserializeFundingObject(String propertyName, JsonNode node) {
        if(node.isObject()) {
            Funding result = new Funding();
            node.fields().forEachRemaining(entry -> {
                switch (entry.getKey()) {
                    case "type": result.setType(deserializeStringProperty("funding.type", entry.getValue())); break;
                    case "url": result.setUrl(deserializeStringProperty("funding.url", entry.getValue())); break;
                    default: log.error("object content of {} does contain unknown property ({})", propertyName, entry.getKey());
                }
            });
            return result;
        } else if(node.isTextual()) {
            Funding result = new Funding();
            result.setType("url");
            result.setUrl(node.asText());
            return result;
        } else {
            log.error("property {} is neither represented through a string nor an object", propertyName);
        }
        return null;
    }

}
