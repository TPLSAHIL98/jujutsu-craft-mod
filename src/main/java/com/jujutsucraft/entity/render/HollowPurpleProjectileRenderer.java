package com.jujutsucraft.entity.render;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import com.jujutsucraft.entity.HollowPurpleProjectileEntity;

public class HollowPurpleProjectileRenderer extends EntityRenderer<HollowPurpleProjectileEntity> {
    
    public HollowPurpleProjectileRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
    }
    
    @Override
    public void render(HollowPurpleProjectileEntity entity, float yaw, float tickDelta,
                      MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        
        matrices.push();
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
    
    @Override
    public Identifier getTexture(HollowPurpleProjectileEntity entity) {
        return null;
    }
}
