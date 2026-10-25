package com.github.alexmodguy.alexscaves.client.model.baked;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BakedModelShadeLayerFullbright extends ForwardingBakedModel {

    public BakedModelShadeLayerFullbright(BakedModel originalModel) {
        this.wrapped = originalModel;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        if (state == null) {
            return super.getQuads(state, side, rand);
        }
        return transformUnshadedQuad(super.getQuads(state, side, rand));
    }

    private static List<BakedQuad> transformUnshadedQuad(List<BakedQuad> oldQuads) {
        List<BakedQuad> quads = new ArrayList<>(oldQuads);
        if (!quads.isEmpty()) {
            quads.replaceAll(quad -> quad.isShade() ? quad : setFullbright(quad));
        }
        return quads;
    }

    private static BakedQuad setFullbright(BakedQuad quad) {
        int[] vertexData = quad.getVertices().clone();
        int step = vertexData.length / 4;

        vertexData[6] = 0x00F000F0;
        vertexData[6 + step] = 0x00F000F0;
        vertexData[6 + (2 * step)] = 0x00F000F0;
        vertexData[6 + (3 * step)] = 0x00F000F0;
        return new BakedQuad(vertexData, quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade());
    }
}