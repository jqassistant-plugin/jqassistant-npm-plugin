package org.jqassistant.plugin.npm.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.jqassistant.plugin.npm.impl.model.Package;
import org.jqassistant.plugin.npm.impl.model.*;

import java.io.IOException;
import java.util.*;
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

        if (node.isObject()) {
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
                    case "exports": result.setExports(deserializeStringPropertyOrMap("exports", valueNode)); break;
                    case "main": result.setMain(deserializeStringProperty("main", valueNode)); break;
                    case "browser": result.setBrowser(deserializeStringProperty("browser", valueNode)); break;
                    case "bin": result.setBin(deserializeBinProperty(valueNode)); break;
                    case "repository": result.setRepository(deserializeRepositoryProperty(valueNode)); break;
                    case "scripts": result.setScripts(deserializeStringMap("scripts", valueNode)); break;
                    case "config": result.setConfig(deserializeStringMap("config", valueNode)); break;
                    case "dependencies": result.setDependencies(deserializeStringMap("dependencies", valueNode)); break;
                    case "devDependencies": result.setDevDependencies(deserializeStringMap("devDependencies", valueNode)); break;
                    case "peerDependencies": result.setPeerDependencies(deserializeStringMap("peerDependencies", valueNode)); break;
                    case "peerDependenciesMeta": result.setPeerDependenciesMeta(deserializePeerDependenciesMetaProperty(valueNode)); break;
                    case "bundleDependencies": // both bundleDependencies and bundledDependencies are allowed
                    case "bundledDependencies": result.setBundleDependencies(deserializeBundleDependenciesProperty(valueNode)); break;
                    case "optionalDependencies": result.setOptionalDependencies(deserializeStringMap("optionalDependencies", valueNode)); break;
                    case "overrides": result.setOverrides(deserializeOverridesProperty(valueNode)); break;
                    case "engines": result.setEngines(deserializeStringMap("engines", valueNode)); break;
                    case "os": result.setOs(deserializeStringArrayProperty("os", valueNode)); break;
                    case "cpu": result.setCpu(deserializeStringArrayProperty("cpu", valueNode)); break;
                    case "devEngines": result.setDevEngines(deserializeDevEnginesProperty(valueNode)); break;
                    case "private": result.setPrivat(deserializeBooleanProperty("private", valueNode)); break;
                    case "man": result.setMan(deserializeStringOrArrayProperty("man", valueNode)); break;
                    case "publishConfig": result.setPublishConfig(deserializeStringMap("publishConfig", valueNode)); break;
                    case "workspaces": result.setWorkspaces(deserializeStringArrayProperty("workspaces", valueNode)); break;
                    default: log.error("Encountered unknown top-level property in package.json ({})", packageJsonProperty.getKey());
                }
            });
        } else {
            log.error("package.json does not contain a top-level object");
        }

        return result;
    }

    private String deserializeStringProperty(String propertyName, JsonNode node) {
        if (node.isTextual()) {
            return node.asText();
        } else {
            log.error("property {} is not a string", propertyName);
            return null;
        }
    }

    private String[] deserializeStringArrayProperty(String propertyName, JsonNode node) {
        if (node.isArray()) {
            List<String> result = new ArrayList<>();
            node.elements().forEachRemaining(element -> {
                if (element.isTextual()) {
                    result.add(element.asText());
                } else {
                    log.error("property {} contains non-string element (skipping)", propertyName);
                }
            });
            return result.toArray(new String[0]);
        }

        else {
            log.error("property {} is not a string array", propertyName);
            return new String[0];
        }
    }

    private String[] deserializeStringOrArrayProperty(String propertyName, JsonNode node) {
        if (node.isArray()) {
            List<String> result = new ArrayList<>();
            node.elements().forEachRemaining(element -> {
                if (element.isTextual()) {
                    result.add(element.asText());
                } else {
                    log.error("property {} contains non-string element (skipping)", propertyName);
                }
            });
            return result.toArray(new String[0]);
        }
        else if(node.isTextual()){
            return new String[] { node.asText() };
        }
        else {
            log.error("property {} is not a string nor a string array", propertyName);
            return new String[0];
        }
    }

    private Map<String, String> deserializeStringMap(String propertyName, JsonNode node) {
        Map<String, String> result = new HashMap<>();
        if (node.isObject()) {
            node.fields().forEachRemaining(field -> {
                JsonNode value = field.getValue();
                if (value.isTextual()) {
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

    private String deserializeBooleanProperty(String propertyName, JsonNode node) {
        if (node.isBoolean()) {
            return node.asText();
        } else {
            log.error("property {} is not boolean", propertyName);
            return null;
        }
    }


        private Map<String, String> deserializeStringPropertyOrMap(String propertyName, JsonNode node) {
        Map<String, String> result = new HashMap<>();
        if (node.isTextual()) {
            result.put(".", node.textValue());
        }
        else if (node.isObject()) {
            node.fields().forEachRemaining(field -> {
                JsonNode value = field.getValue();
                if (value.isTextual()) {
                    result.put(field.getKey(), value.textValue());
                } else {
                    log.error("Property {} of {} is not a string", field.getKey(), propertyName);
                }
            });
        } else {
            log.error("property {} is neither represented through a string nor an object", propertyName);
        }
        return result;
    }



    private Person deserializePersonProperty(String propertyName, JsonNode node) {
        if (node.isTextual()) {
            // single string representation, e.g. "John Doe <contact@example.com> (https://homepage.com)"
            String text = node.asText();
            Matcher matcher = personPattern.matcher(text);
            if (matcher.matches()) {
                String email = matcher.group(2);
                String url = matcher.group(3);
                Person result = new Person();
                result.setName(matcher.group(1));
                if (email != null) {
                    result.setEmail(email.substring(2, email.length() - 1));
                }
                if (url != null) {
                    result.setUrl(url.substring(2, url.length() - 1));
                }
                return result;
            } else {
                log.error("string content of {} does not match pattern for this property", propertyName);
            }
        } else if (node.isObject()) {
            // object representation
            Person result = new Person();
            node.fields().forEachRemaining(entry -> {
                switch (entry.getKey()) {
                    case "name":
                        result.setName(deserializeStringProperty(propertyName + ".name", entry.getValue()));
                        break;
                    case "email":
                        result.setEmail(deserializeStringProperty(propertyName + ".email", entry.getValue()));
                        break;
                    case "url":
                        result.setUrl(deserializeStringProperty(propertyName + ".url", entry.getValue()));
                        break;
                    default:
                        log.error("object content of {} does contain unknown property ({})", propertyName, entry.getKey());
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
        if (node.isArray()) {

            int index = 0;
            for (var it = node.elements(); it.hasNext(); index++) {
                JsonNode elem = it.next();
                Person p = deserializePersonProperty("contributors[" + index + "]", elem);
                if (p != null) {
                    result.add(p);
                }
            }
        } else {
            log.error("property contributors is not an array");
        }
        return result;
    }

    private Bugs deserializeBugsProperty(JsonNode node) {
        if (node.isObject()) {
            Bugs result = new Bugs();
            node.fields().forEachRemaining(entry -> {
                switch (entry.getKey()) {
                    case "email":
                        result.setEmail(deserializeStringProperty("bugs.email", entry.getValue()));
                        break;
                    case "url":
                        result.setUrl(deserializeStringProperty("bugs.url", entry.getValue()));
                        break;
                    default:
                        log.error("object content of bugs does contain unknown property ({})", entry.getKey());
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
        if (node.isArray()) {
            int index = 0;
            for (var it = node.elements(); it.hasNext(); index++) {
                JsonNode elem = it.next();
                Funding f = deserializeFundingObject("funding[" + index + "]", elem);
                if (f != null) {
                    result.add(f);
                }
            }
        } else if (node.isObject() || node.isTextual()) {
            Funding f = deserializeFundingObject("funding", node);
            if (f != null) {
                result.add(f);
            }
        } else {
            log.error("property funding is neither an array, an object, nor a string");
        }
        return result;
    }

    private Funding deserializeFundingObject(String propertyName, JsonNode node) {
        if (node.isObject()) {
            Funding result = new Funding();
            node.fields().forEachRemaining(entry -> {
                switch (entry.getKey()) {
                    case "type":
                        result.setType(deserializeStringProperty("funding.type", entry.getValue()));
                        break;
                    case "url":
                        result.setUrl(deserializeStringProperty("funding.url", entry.getValue()));
                        break;
                    default:
                        log.error("object content of {} does contain unknown property ({})", propertyName, entry.getKey());
                }
            });
            return result;
        } else if (node.isTextual()) {
            Funding result = new Funding();
            result.setType("url");
            result.setUrl(node.asText());
            return result;
        } else {
            log.error("property {} is neither represented through a string nor an object", propertyName);
        }
        return null;
    }

    private List<Binary> deserializeBinProperty(JsonNode node) {
        List<Binary> result = new ArrayList<>();
        if (node.isObject()) {
            node.fields().forEachRemaining(entry -> {
                JsonNode value = entry.getValue();
                if (value.isTextual()) {
                    Binary b = new Binary();
                    b.setName(entry.getKey());
                    b.setPath(value.asText());
                    result.add(b);
                } else {
                    log.error("content of bin.{} is not a string", entry.getKey());
                }
            });
        } else if (node.isTextual()) {
            Binary b = new Binary();
            b.setName(null); // resolve this to package name later
            b.setPath(node.asText());
            result.add(b);
        } else {
            log.error("property bin is neither an object nor a string");
        }
        return result;
    }

    private Repository deserializeRepositoryProperty(JsonNode node) {
        Repository r = new Repository();
        if (node.isObject()) {
            r.setUrl(node.get("url").textValue());
            r.setType(node.get("type").textValue());
            r.setDirectory(node.get("directory").textValue());
            return r;
        } else if (node.isTextual()) {
            r.setUrl(node.textValue());
            return r;
        } else {
            log.error("property bin is neither an object nor a string");
        }
        return null;
    }

    private Map<String, Boolean> deserializePeerDependenciesMetaProperty(JsonNode node) {
        Map<String, Boolean> result = new HashMap<>();
        if (node.isObject()) {
            node.fields().forEachRemaining(dependencyField -> {
                JsonNode dependencyFieldValue = dependencyField.getValue();
                if (dependencyFieldValue.isObject()) {
                    dependencyFieldValue.fields().forEachRemaining(dependencyMetaEntry -> {
                        JsonNode dependencyMetaEntryValue = dependencyMetaEntry.getValue();
                        if (dependencyMetaEntry.getKey().equals("optional")) {
                            if (dependencyMetaEntryValue.isBoolean()) {
                                result.put(dependencyField.getKey(), dependencyMetaEntryValue.booleanValue());
                            } else {
                                log.error("Property peerDependenciesMeta.{}.optional is not a boolean", dependencyField.getKey());
                            }
                        } else {
                            log.error("Property peerDependenciesMeta.{} does contain unknown property ({})", dependencyField.getKey(), dependencyMetaEntry.getKey());
                        }
                    });
                } else {
                    log.error("Property peerDependenciesMeta.{} is not an object", dependencyField.getKey());
                }
            });
        } else {
            log.error("property peerDependenciesMeta is not an object");
        }
        return result;
    }

    private BundleDependencies deserializeBundleDependenciesProperty(JsonNode node) {
        BundleDependencies result = new BundleDependencies();
        if (node.isBoolean()) {
            result.setAllBundled(node.asBoolean());
        } else if (node.isArray()) {
            int index = 0;
            for (var it = node.elements(); it.hasNext(); index++) {
                JsonNode elem = it.next();
                if (elem.isTextual()) {
                    result.getDependencies().add(elem.textValue());
                } else {
                    log.error("property bundleDependencies[{}] is not a string", index);
                }
            }
        } else {
            log.error("property bundleDependencies is neither an array nor a boolean");
        }
        return result;
    }


    private Map<String, String> deserializeOverridesProperty(JsonNode node) {
        Map<String, String> result = new HashMap<>();
        if (node.isObject()) {
            node.fields().forEachRemaining(field -> {
                JsonNode value = field.getValue();
                StringBuilder name = new StringBuilder(field.getKey());

                Map<String, String> newResult1 = this.deserializeOverridesField(result, name, value);
                result.putAll(newResult1);

            });
        } else {
            log.error("property overrides is not an object");
        }
        return result;
    }

    private Map<String, String> deserializeOverridesField(Map<String, String> result, StringBuilder name, JsonNode node) {
        if (node.isTextual()) {
            result.put(name.toString(), node.textValue());
        }
        node.fields().forEachRemaining(field -> {
            JsonNode value = field.getValue();
            if (value.isTextual()) {
                if (field.getKey().equals(".")) {
                    result.put(name.toString(), field.getValue().textValue());
                    Map<String, String> newResult = this.deserializeOverridesField(result, name, value);
                    result.putAll(newResult);
                } else {
                    String name1 = name.append("/").append(field.getKey()).toString();
                    result.put(name1, field.getValue().textValue());
                }
            }
            if (value.isObject()) {
                name.append("/").append(field.getKey());
                Map<String, String> newResult1 = this.deserializeOverridesField(result, name, value);
                result.putAll(newResult1);
            }
        });
        return result;
    }

    private List<DevEngine> deserializeDevEnginesProperty(JsonNode node) {
        List<DevEngine> result = new ArrayList<>();
        if (node.isObject()) {
            node.fields().forEachRemaining(field -> {
                    if (field.getValue().isObject()) {
                        DevEngine devEngine = new DevEngine();
                        devEngine.setType(field.getKey());
                        result.add(deserializeDevEnginesObjectField(field.getValue(), devEngine));
                    }
                    if (field.getValue().isArray()) {
                        Iterator<JsonNode> elementsIterator = field.getValue().elements();
                        while (elementsIterator.hasNext()) {
                            JsonNode arrayField = elementsIterator.next();
                            if (arrayField.isObject()) {
                                DevEngine devEngineForArrayField = new DevEngine();
                                devEngineForArrayField.setType(field.getKey());
                                result.add(deserializeDevEnginesObjectField(arrayField, devEngineForArrayField));
                            } else {
                                log.error("Field {} of property devEngines is not an object", field.getKey());
                            }
                        }
                    }
                });
        } else {
            log.error("property devEngines is not an object");
        }
        result.stream()
            .filter(descriptor -> descriptor.getOnFail() == null)
            .forEach(descriptor -> descriptor.setOnFail("error")); // if undefined, onFail is of the same value as error
        result.stream()
            .filter(descriptor -> descriptor.getName() == null) // objects must contain name
            .forEach(descriptor -> log.error("field {} of property devEngines contains no name", descriptor.getType()));
        return result;
    }

    private DevEngine deserializeDevEnginesObjectField(JsonNode field, DevEngine devEngine) {
        field.fields()
            .forEachRemaining(objectField -> {
                switch (objectField.getKey()) {
                case "name":
                    devEngine.setName(objectField.getValue().asText());
                    break;
                case "version":
                    devEngine.setVersion(objectField.getValue().asText());
                    break;
                case "onFail":
                    if(objectField.getValue().asText().equals("warn") || objectField.getValue().asText().equals("error") || objectField.getValue().asText().equals("ignore")) {
                        devEngine.setOnFail(objectField.getValue()
                            .asText());
                    }else{
                        log.error("onFail field of {} property devEngines is not one of [warn, error, ignore]", devEngine.getType());
                    }
                    break;
                }
            });
        return devEngine;
    }
}
