package fr.hardel.betterdeath;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record StashedStack(int slot, ItemStack stack) {
    public static final Codec<StashedStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("slot").forGetter(StashedStack::slot),
        ItemStack.CODEC.fieldOf("stack").forGetter(StashedStack::stack)
    ).apply(instance, StashedStack::new));

    public void returnTo(Player receiver, boolean wearer, BlockPos ground) {
        Inventory inventory = receiver.getInventory();
        if (wearer && Inventory.EQUIPMENT_SLOT_MAPPING.containsKey(slot) && inventory.getItem(slot).isEmpty()) {
            inventory.setItem(slot, stack);
            return;
        }

        inventory.add(stack);
        if (stack.isEmpty()) {
            return;
        }

        ItemEntity item = new ItemEntity(receiver.level(), ground.getX() + 0.5, receiver.getY(), ground.getZ() + 0.5, stack, 0, 0, 0);
        item.setTarget(receiver.getUUID());
        receiver.level().addFreshEntity(item);
    }
}
