package com.jujutsucraft.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.particle.ParticleTypes;

public class HollowPurpleProjectileEntity extends ProjectileEntity {
    
    public static final EntityType<HollowPurpleProjectileEntity> TYPE = 
        EntityType.Builder.<HollowPurpleProjectileEntity>create(
            HollowPurpleProjectileEntity::new,
            net.minecraft.entity.EntityType.CATEGORY_MISC
        )
        .dimensions(0.5f, 0.5f)
        .maxTrackingRange(256)
        .trackingTickInterval(1)
        .build();
    
    private float damage;
    private float radius;
    private int lifetime = 0;
    private static final int MAX_LIFETIME = 200;
    private float chargeMultiplier;
    
    public HollowPurpleProjectileEntity(World world) {
        super(TYPE, world);
        this.damage = 20;
        this.radius = 10;
        this.chargeMultiplier = 1.0f;
    }
    
    public HollowPurpleProjectileEntity(World world, LivingEntity owner, float damage, float radius, float chargeMultiplier) {
        super(TYPE, world);
        this.setOwner(owner);
        this.damage = damage;
        this.radius = radius;
        this.chargeMultiplier = chargeMultiplier;
        this.noClip = false;
    }
    
    @Override
    protected void initDataTracker() {
    }
    
    @Override
    public void tick() {
        super.tick();
        
        this.lifetime++;
        
        if (lifetime > MAX_LIFETIME) {
            this.discard();
            return;
        }
        
        Vec3d pos = this.getPos();
        
        if (!this.getWorld().isClient) {
            BlockPos blockPos = BlockPos.ofFloored(pos);
            
            if (this.getWorld().getBlockState(blockPos).getMaterial().isSolid()) {
                this.onImpact(blockPos, null);
                return;
            }
            
            for (LivingEntity entity : this.getWorld().getNonSpectatingEntities(
                LivingEntity.class,
                this.getBoundingBox().expand(1.0)
            )) {
                if (entity != this.getOwner() && !entity.isSpectator()) {
                    this.onImpact(null, entity);
                    return;
                }
            }
        }
        
        if (this.getWorld().isClient) {
            spawnTrailParticles(pos);
        }
    }
    
    private void onImpact(BlockPos blockPos, LivingEntity entity) {
        if (this.getWorld().isClient) return;
        
        World world = this.getWorld();
        Vec3d pos = this.getPos();
        
        for (LivingEntity e : world.getNonSpectatingEntities(
            LivingEntity.class,
            this.getBoundingBox().expand(radius + 2)
        )) {
            if (e != this.getOwner()) {
                double distance = e.getPos().distanceTo(pos);
                if (distance <= radius + 2) {
                    e.damage(world.getDamageSources().magic(), damage);
                    
                    double knockback = 3.0 * chargeMultiplier * (1.0 - distance / (radius + 2));
                    e.takeKnockback(knockback, pos.x - e.getX(), pos.z - e.getZ());
                }
            }
        }
        
        if (blockPos != null) {
            destroyBlocksInRadius(world, blockPos, radius);
        }
        
        world.playSound(
            null,
            pos.x, pos.y, pos.z,
            SoundEvents.ENTITY_GENERIC_EXPLODE,
            SoundCategory.BLOCKS,
            2.0f + (chargeMultiplier - 1.0f) * 0.5f,
            0.7f
        );
        
        spawnExplosionParticles(world, pos);
        
        this.discard();
    }
    
    private void destroyBlocksInRadius(World world, BlockPos center, float radius) {
        int r = (int) (radius + 1);
        for (int x = center.getX() - r; x <= center.getX() + r; x++) {
            for (int y = center.getY() - r; y <= center.getY() + r; y++) {
                for (int z = center.getZ() - r; z <= center.getZ() + r; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    double dist = Math.sqrt(center.getSquaredDistance(pos));
                    
                    if (dist < radius) {
                        var state = world.getBlockState(pos);
                        if (state.getDestroySpeed(world, pos) >= 0 && 
                            state != net.minecraft.block.Blocks.BEDROCK.getDefaultState() &&
                            state != net.minecraft.block.Blocks.END_PORTAL_FRAME.getDefaultState()) {
                            
                            world.breakBlock(pos, true);
                        }
                    }
                }
            }
        }
    }
    
    private void spawnTrailParticles(Vec3d pos) {
        for (int i = 0; i < 3; i++) {
            double angle = System.currentTimeMillis() * 0.005 + i * (Math.PI * 2 / 3);
            double offsetX = Math.cos(angle) * 0.2;
            double offsetZ = Math.sin(angle) * 0.2;
            
            this.getWorld().addParticle(
                ParticleTypes.EFFECT,
                pos.x + offsetX,
                pos.y,
                pos.z + offsetZ,
                this.getVelocity().x * 0.05,
                this.getVelocity().y * 0.05,
                this.getVelocity().z * 0.05
            );
        }
    }
    
    private void spawnExplosionParticles(World world, Vec3d pos) {
        for (int i = 0; i < 25; i++) {
            double angle = Math.random() * Math.PI * 2;
            double elevation = (Math.random() - 0.5) * Math.PI;
            double distance = radius * (0.5 + Math.random() * 0.5);
            
            double x = Math.cos(angle) * Math.cos(elevation) * distance;
            double y = Math.sin(elevation) * distance;
            double z = Math.sin(angle) * Math.cos(elevation) * distance;
            
            world.addParticle(
                ParticleTypes.EFFECT,
                pos.x + x * 0.1,
                pos.y + y * 0.1,
                pos.z + z * 0.1,
                x * 0.15,
                y * 0.15,
                z * 0.15
            );
        }
    }
    
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putFloat("damage", damage);
        nbt.putFloat("radius", radius);
        nbt.putFloat("chargeMultiplier", chargeMultiplier);
        nbt.putInt("lifetime", lifetime);
    }
    
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.damage = nbt.getFloat("damage");
        this.radius = nbt.getFloat("radius");
        this.chargeMultiplier = nbt.getFloat("chargeMultiplier");
        this.lifetime = nbt.getInt("lifetime");
    }
}
