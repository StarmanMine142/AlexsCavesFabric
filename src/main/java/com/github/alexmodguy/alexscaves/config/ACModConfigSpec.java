package com.github.alexmodguy.alexscaves.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public class ACModConfigSpec {

    public static class Builder {
        private final String name;
        private final JsonObject values = new JsonObject();
        private final JsonObject defaults = new JsonObject();
        private String section = "";

        public Builder(String name) {
            this.name = name;
        }

        public Builder push(String section) {
            this.section = section;
            return this;
        }

        public Builder pop() {
            this.section = "";
            return this;
        }

        public Builder comment(String... comment) {
            return this;
        }

        public Builder translation(String translationKey) {
            return this;
        }

        private String key(String name) {
            return section.isEmpty() ? name : section + "." + name;
        }

        public boolean define(String name, boolean defaultValue) {
            String k = key(name);
            defaults.addProperty(k, defaultValue);
            return values.has(k) ? values.get(k).getAsBoolean() : defaultValue;
        }

        public int defineInRange(String name, int defaultValue, int min, int max) {
            String k = key(name);
            defaults.addProperty(k, defaultValue);
            if (!values.has(k)) return defaultValue;
            int v = values.get(k).getAsInt();
            return Math.max(min, Math.min(max, v));
        }

        public double defineInRange(String name, double defaultValue, double min, double max) {
            String k = key(name);
            defaults.addProperty(k, defaultValue);
            if (!values.has(k)) return defaultValue;
            double v = values.get(k).getAsDouble();
            return Math.max(min, Math.min(max, v));
        }

        @SuppressWarnings("unchecked")
        public <T> T define(String name, T defaultValue) {
            if (defaultValue instanceof Boolean b) return (T) Boolean.valueOf(define(name, b.booleanValue()));
            if (defaultValue instanceof Integer i) return (T) Integer.valueOf(defineInRange(name, i, Integer.MIN_VALUE, Integer.MAX_VALUE));
            if (defaultValue instanceof Double d) return (T) Double.valueOf(defineInRange(name, d, Double.MIN_VALUE, Double.MAX_VALUE));
            return defaultValue;
        }

        public <T> List<? extends T> defineList(String name, List<? extends T> defaultValue, java.util.function.Predicate<Object> validator) {
            return defaultValue;
        }

        public <C> Pair<C, ACModConfigSpec> configure(Function<Builder, C> factory) {
            Path configFile = FabricLoader.getInstance().getConfigDir().resolve(name + ".json");
            if (Files.exists(configFile)) {
                try {
                    String content = Files.readString(configFile);
                    JsonElement parsed = JsonParser.parseString(content);
                    if (parsed.isJsonObject()) {
                        flatten(parsed.getAsJsonObject(), values, "");
                    }
                } catch (IOException ignored) {
                }
            }

            C config = factory.apply(this);

            JsonObject toWrite = values.deepCopy();
            for (String key : defaults.keySet()) {
                if (!toWrite.has(key)) {
                    toWrite.add(key, defaults.get(key));
                }
            }
            JsonObject nested = new JsonObject();
            for (String key : toWrite.keySet()) {
                setNested(nested, key, toWrite.get(key));
            }

            try {
                Files.createDirectories(configFile.getParent());
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Files.writeString(configFile, gson.toJson(nested));
            } catch (IOException ignored) {
            }

            return Pair.of(config, new ACModConfigSpec());
        }

        private static void flatten(JsonObject source, JsonObject target, String prefix) {
            for (String key : source.keySet()) {
                JsonElement value = source.get(key);
                String fullKey = prefix.isEmpty() ? key : prefix + "." + key;
                if (value.isJsonObject()) {
                    flatten(value.getAsJsonObject(), target, fullKey);
                } else {
                    target.add(fullKey, value);
                }
            }
        }

        private static void setNested(JsonObject root, String key, JsonElement value) {
            String[] parts = key.split("\\.");
            JsonObject current = root;
            for (int i = 0; i < parts.length - 1; i++) {
                if (!current.has(parts[i]) || !current.get(parts[i]).isJsonObject()) {
                    current.add(parts[i], new JsonObject());
                }
                current = current.getAsJsonObject(parts[i]);
            }
            current.add(parts[parts.length - 1], value);
        }
    }

    public static class ConfigValue<T> {
        private final T value;
        public ConfigValue(T value) { this.value = value; }
        public T get() { return value; }
    }

    public static class BooleanValue extends ConfigValue<Boolean> {
        public BooleanValue(Boolean value) { super(value); }
    }

    public static class IntValue extends ConfigValue<Integer> {
        public IntValue(Integer value) { super(value); }
    }

    public static class DoubleValue extends ConfigValue<Double> {
        public DoubleValue(Double value) { super(value); }
    }
}