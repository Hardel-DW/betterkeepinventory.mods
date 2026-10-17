package fr.hardel.betterdeath;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public record Keeper(UUID owner, List<StashedStack> stash, long expiresAt) {
    public static final Codec<Keeper> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        UUIDUtil.CODEC.fieldOf("owner").forGetter(Keeper::owner),
        StashedStack.CODEC.listOf().fieldOf("stash").forGetter(Keeper::stash),
        Codec.LONG.fieldOf("expires_at").forGetter(Keeper::expiresAt)
    ).apply(instance, Keeper::new));
    
    private static final double SIGHT_RANGE = 32;
    private static final double GIVE_RANGE = 2;
    private static final double FOLLOW_SPEED = 2.25;

    public static void summon(ServerPlayer player, List<StashedStack> stash) {
        ServerLevel level = player.level();
        Allay allay = EntityTypes.ALLAY.create(level, EntitySpawnReason.TRIGGERED);
        place(allay, player);
        allay.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.CHEST));
        allay.setNoGravity(true);
        allay.setPersistenceRequired();
        allay.setCustomName(Component.translatableWithFallback("entity.betterdeath.keeper", "%s's belongings", player.getName()));
        allay.setCustomNameVisible(true);
        long expiresAt = level.getGameTime() + level.getGameRules().get(BetterDeathRules.ALLAY_LIFETIME);
        ((KeeperHolder) allay).betterdeath$setKeeper(new Keeper(player.getUUID(), stash, expiresAt));
        level.addFreshEntity(allay);
    }

    private static void place(Allay allay, ServerPlayer player) {
        ServerLevel level = player.level();
        if (player.getY() >= level.getMinY()) {
            allay.snapTo(player.position());
            return;
        }

        Vec3 foothold = ((FootholdHolder) player).betterdeath$foothold(level.dimension()).orElse(new Vec3(player.getX(), level.getMinY(), player.getZ()));
        allay.snapTo(player.getX(), foothold.y, player.getZ());
        if (!level.noCollision(allay)) {
            allay.snapTo(foothold);
        }
    }

    public void tick(Allay allay, ServerLevel level) {
        if (level.getGameTime() >= expiresAt) {
            allay.discard();
            return;
        }

        Player player = level.getPlayerByUUID(owner);
        boolean seen = player != null && player.isAlive() && !player.isSpectator() && allay.closerThan(player, SIGHT_RANGE) && allay.getSensing().hasLineOfSight(player);
        if (!seen) {
            return;
        }

        if (allay.closerThan(player, GIVE_RANGE) && player.mainSupportingBlockPos.isPresent() && !player.isInLiquid()) {
            stash.forEach(stack -> stack.returnTo(player, player.mainSupportingBlockPos.get()));
            level.playSound(null, allay.getX(), allay.getY(), allay.getZ(), SoundEvents.ALLAY_ITEM_TAKEN, SoundSource.NEUTRAL);
            allay.discard();
            return;
        }

        allay.getNavigation().moveTo(player, FOLLOW_SPEED);
    }
}
