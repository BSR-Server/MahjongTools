package org.bsrserver.mahjongtools;

import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class Utils {
    public static ItemFrameEntity getItemFrameEntity(ServerWorld serverWorld, int x, int y, int z) {
        List<ItemFrameEntity> itemFrameEntityList = serverWorld.getEntitiesByType(
                EntityType.ITEM_FRAME,
                new Box(new BlockPos(x, y, z)),
                itemFrameEntity -> true
        );
        if (!itemFrameEntityList.isEmpty()) {
            return itemFrameEntityList.get(0);
        } else {
            return null;
        }
    }

    public static String getItemFrameNameByPos(ServerWorld serverWorld, int x, int y, int z) {
        ItemFrameEntity itemFrameEntity = Utils.getItemFrameEntity(serverWorld, x, y, z);
        if (itemFrameEntity != null) {
            return itemFrameEntity.getHeldItemStack().getName().asString();
        } else {
            return null;
        }
    }
}
