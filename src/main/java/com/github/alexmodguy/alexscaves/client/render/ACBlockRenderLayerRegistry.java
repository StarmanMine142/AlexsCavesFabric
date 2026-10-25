package com.github.alexmodguy.alexscaves.client.render;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ACBlockRenderLayerRegistry {
    private static final Map<ResourceLocation, String> MODEL_RENDER_TYPE_CACHE = new HashMap<>();
    private static final Map<ResourceLocation, String> VANILLA_PARENT_RENDER_TYPES = Map.of(
        ResourceLocation.withDefaultNamespace("block/leaves"), "cutout_mipped",
        ResourceLocation.withDefaultNamespace("block/cross"), "cutout",
        ResourceLocation.withDefaultNamespace("block/tinted_cross"), "cutout",
        ResourceLocation.withDefaultNamespace("block/flower_pot_cross"), "cutout",
        ResourceLocation.withDefaultNamespace("block/crop"), "cutout"
    );

    private ACBlockRenderLayerRegistry() {
    }

    public static void register() {
        int cutoutBlocks = 0;
        int cutoutMippedBlocks = 0;
        int translucentBlocks = 0;
        ClassLoader classLoader = ACBlockRenderLayerRegistry.class.getClassLoader();

        for (ResourceLocation blockId : BuiltInRegistries.BLOCK.keySet()) {
            if (!AlexsCaves.MODID.equals(blockId.getNamespace())) {
                continue;
            }

            String renderTypeName = inferBlockRenderType(classLoader, blockId);
            RenderType renderType = toRenderType(renderTypeName);
            if (renderType == null) {
                continue;
            }

            Block block = BuiltInRegistries.BLOCK.get(blockId);
            BlockRenderLayerMap.INSTANCE.putBlock(block, renderType);
            switch (renderTypeName) {
                case "cutout" -> cutoutBlocks++;
                case "cutout_mipped" -> cutoutMippedBlocks++;
                case "translucent" -> translucentBlocks++;
                default -> {
                }
            }
        }

        AlexsCaves.LOGGER.info(
            "Registered {} cutout, {} cutout_mipped, and {} translucent Alex's Caves blocks from asset render metadata",
            cutoutBlocks,
            cutoutMippedBlocks,
            translucentBlocks
        );
    }

    @Nullable
    private static String inferBlockRenderType(ClassLoader classLoader, ResourceLocation blockId) {
        JsonObject blockstateJson = readJsonObject(
            classLoader,
            "assets/" + blockId.getNamespace() + "/blockstates/" + blockId.getPath() + ".json"
        );
        if (blockstateJson == null) {
            return null;
        }

        String renderType = null;
        JsonObject variants = getObject(blockstateJson, "variants");
        if (variants != null) {
            for (JsonElement variantElement : variants.asMap().values()) {
                renderType = mergeRenderType(renderType, resolveEntryRenderType(classLoader, variantElement));
            }
        }

        JsonElement multipartElement = blockstateJson.get("multipart");
        if (multipartElement != null && multipartElement.isJsonArray()) {
            for (JsonElement partElement : multipartElement.getAsJsonArray()) {
                JsonObject partObject = partElement.getAsJsonObject();
                renderType = mergeRenderType(renderType, resolveEntryRenderType(classLoader, partObject.get("apply")));
            }
        }
        return renderType;
    }

    @Nullable
    private static String resolveEntryRenderType(ClassLoader classLoader, @Nullable JsonElement entryElement) {
        if (entryElement == null) {
            return null;
        }
        if (entryElement.isJsonArray()) {
            String renderType = null;
            for (JsonElement childElement : entryElement.getAsJsonArray()) {
                renderType = mergeRenderType(renderType, resolveEntryRenderType(classLoader, childElement));
            }
            return renderType;
        }
        if (!entryElement.isJsonObject()) {
            return null;
        }
        JsonObject entryObject = entryElement.getAsJsonObject();
        JsonElement modelElement = entryObject.get("model");
        if (modelElement == null || !modelElement.isJsonPrimitive()) {
            return null;
        }
        ResourceLocation modelId = normalizeModelId(modelElement.getAsString());
        if (modelId == null) {
            return null;
        }
        return resolveModelRenderType(classLoader, modelId, new HashSet<>());
    }

    @Nullable
    private static String resolveModelRenderType(
        ClassLoader classLoader,
        ResourceLocation modelId,
        Set<ResourceLocation> visited
    ) {
        String cached = MODEL_RENDER_TYPE_CACHE.get(modelId);
        if (cached != null) {
            return cached;
        }
        String vanillaParentRenderType = VANILLA_PARENT_RENDER_TYPES.get(modelId);
        if (vanillaParentRenderType != null) {
            MODEL_RENDER_TYPE_CACHE.put(modelId, vanillaParentRenderType);
            return vanillaParentRenderType;
        }
        if (!visited.add(modelId)) {
            return null;
        }

        JsonObject modelJson = readJsonObject(
            classLoader,
            "assets/" + modelId.getNamespace() + "/models/" + modelId.getPath() + ".json"
        );
        if (modelJson == null) {
            return null;
        }

        JsonElement renderTypeElement = modelJson.get("render_type");
        if (renderTypeElement != null && renderTypeElement.isJsonPrimitive()) {
            String renderTypeName = renderTypeElement.getAsString();
            MODEL_RENDER_TYPE_CACHE.put(modelId, renderTypeName);
            return renderTypeName;
        }

        JsonElement parentElement = modelJson.get("parent");
        if (parentElement != null && parentElement.isJsonPrimitive()) {
            ResourceLocation parentId = normalizeModelId(parentElement.getAsString());
            if (parentId == null) {
                return null;
            }
            String inheritedRenderType = resolveModelRenderType(classLoader, parentId, visited);
            if (inheritedRenderType != null) {
                MODEL_RENDER_TYPE_CACHE.put(modelId, inheritedRenderType);
            }
            return inheritedRenderType;
        }
        return null;
    }

    @Nullable
    private static JsonObject readJsonObject(ClassLoader classLoader, String resourcePath) {
        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                return null;
            }
            String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).trim();
            if (json.endsWith(",")) {
                json = json.substring(0, json.length() - 1);
            }
            try (Reader reader = new StringReader(json)) {
                com.google.gson.stream.JsonReader jsonReader = new com.google.gson.stream.JsonReader(reader);
                jsonReader.setLenient(true);
                JsonElement jsonElement = JsonParser.parseReader(jsonReader);
                return jsonElement != null && jsonElement.isJsonObject() ? jsonElement.getAsJsonObject() : null;
            }
        } catch (Exception exception) {
            return null;
        }
    }

    @Nullable
    private static JsonObject getObject(JsonObject parent, String key) {
        JsonElement element = parent.get(key);
        return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
    }

    @Nullable
    private static ResourceLocation normalizeModelId(String modelId) {
        try {
            ResourceLocation resourceLocation = modelId.contains(":")
                ? ResourceLocation.parse(modelId)
                : ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, modelId);
            String path = resourceLocation.getPath();
            if (!path.startsWith("block/") && !path.startsWith("item/")) {
                path = "block/" + path;
            }
            return ResourceLocation.fromNamespaceAndPath(resourceLocation.getNamespace(), path);
        } catch (Exception exception) {
            return null;
        }
    }

    @Nullable
    private static String mergeRenderType(@Nullable String current, @Nullable String candidate) {
        if (candidate == null) {
            return current;
        }
        if (current == null || renderTypePriority(candidate) > renderTypePriority(current)) {
            return candidate;
        }
        return current;
    }

    private static int renderTypePriority(String renderTypeName) {
        return switch (renderTypeName) {
            case "translucent" -> 3;
            case "cutout_mipped" -> 2;
            case "cutout" -> 1;
            default -> 0;
        };
    }

    @Nullable
    private static RenderType toRenderType(@Nullable String renderTypeName) {
        if (renderTypeName == null) {
            return null;
        }
        return switch (renderTypeName) {
            case "cutout" -> RenderType.cutout();
            case "cutout_mipped" -> RenderType.cutoutMipped();
            case "translucent" -> RenderType.translucent();
            default -> null;
        };
    }
}
