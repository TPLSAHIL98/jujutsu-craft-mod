package com.jujutsucraft.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import com.jujutsucraft.JujutsuCraftMod;

public class HollowPurpleC2SPacket implements FabricPacket {
    
    public static final Identifier ID = new Identifier(JujutsuCraftMod.MOD_ID, "hollow_purple");
    public static final PacketType<HollowPurpleC2SPacket> TYPE = 
        PacketType.create(ID, HollowPurpleC2SPacket::new);
    
    private int chargeTime;
    
    public HollowPurpleC2SPacket(int chargeTime) {
        this.chargeTime = chargeTime;
    }
    
    public HollowPurpleC2SPacket(PacketByteBuf buf) {
        this.chargeTime = buf.readInt();
    }
    
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(chargeTime);
    }
    
    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
    
    public int getChargeTime() {
        return chargeTime;
    }
}
