package com.github.alexmodguy.alexscaves.config;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Function;

public class ACModConfigSpec {

    public static class Builder {
        public Builder push(String section) {
            return this;
        }

        public Builder pop() {
            return this;
        }

        public Builder comment(String... comment) {
            return this;
        }

        public Builder translation(String translationKey) {
            return this;
        }

        public boolean define(String name, boolean defaultValue) {
            return defaultValue;
        }

        public int defineInRange(String name, int defaultValue, int min, int max) {
            return defaultValue;
        }

        public double defineInRange(String name, double defaultValue, double min, double max) {
            return defaultValue;
        }

        public <T> T define(String name, T defaultValue) {
            return defaultValue;
        }

        public <T> List<? extends T> defineList(String name, List<? extends T> defaultValue, java.util.function.Predicate<Object> validator) {
            return defaultValue;
        }

        public <C> Pair<C, ACModConfigSpec> configure(Function<Builder, C> factory) {
            return Pair.of(factory.apply(this), new ACModConfigSpec());
        }
    }

    public static class ConfigValue<T> {
        private final T value;

        public ConfigValue(T value) {
            this.value = value;
        }

        public T get() {
            return value;
        }
    }

    public static class BooleanValue extends ConfigValue<Boolean> {
        public BooleanValue(Boolean value) {
            super(value);
        }
    }

    public static class IntValue extends ConfigValue<Integer> {
        public IntValue(Integer value) {
            super(value);
        }
    }

    public static class DoubleValue extends ConfigValue<Double> {
        public DoubleValue(Double value) {
            super(value);
        }
    }
}