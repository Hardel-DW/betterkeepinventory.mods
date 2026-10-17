package fr.hardel.betterdeath;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.ArrayList;
import java.util.List;

public final class DeathToll {
    private final int experienceLoss;
    private final int durabilityLoss;
    private final boolean keepDamageableItems;
    private final int keepStackChance;
    private final RandomSource random;

    public DeathToll(GameRules rules, RandomSource random) {
        this.experienceLoss = rules.get(BetterDeathRules.EXPERIENCE_LOSS);
        this.durabilityLoss = rules.get(BetterDeathRules.DURABILITY_LOSS);
        this.keepDamageableItems = rules.get(BetterDeathRules.KEEP_DAMAGEABLE_ITEMS);
        this.keepStackChance = rules.get(BetterDeathRules.KEEP_STACK_CHANCE);
        this.random = random;
    }

    public List<StashedStack> take(ServerPlayer player) {
        loseExperience(player);
        Inventory inventory = player.getInventory();
        List<StashedStack> stash = new ArrayList<>();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack lost = settle(inventory, slot);
            if (!lost.isEmpty()) {
                stash.add(new StashedStack(slot, lost));
            }
        }

        return stash;
    }

    private void loseExperience(ServerPlayer player) {
        long points = pointsToReach(player.experienceLevel) + Math.round(player.experienceProgress * player.getXpNeededForNextLevel());
        player.giveExperiencePoints((int) -Math.min(points * experienceLoss / 100, Integer.MAX_VALUE));
    }

    private static long pointsToReach(long level) {
        if (level <= 16) {
            return level * level + 6 * level;
        }

        return level <= 31 ? (5 * level * level - 81 * level + 720) / 2 : (9 * level * level - 325 * level + 4440) / 2;
    }

    private ItemStack settle(Inventory inventory, int slot) {
        ItemStack stack = inventory.getItem(slot);
        if (stack.isDamageableItem()) {
            wear(stack);
        }

        if (stack.isEmpty() || keepDamageableItems && stack.has(DataComponents.MAX_DAMAGE)) {
            return ItemStack.EMPTY;
        }

        int count = stack.getCount();
        int kept = random.nextInt(100) < keepStackChance ? random.nextIntBetweenInclusive(1, count) : 0;
        return inventory.removeItem(slot, count - kept);
    }

    private void wear(ItemStack stack) {
        int damage = stack.getDamageValue() + Mth.ceil(stack.getMaxDamage() * durabilityLoss / 100F);
        if (damage >= stack.getMaxDamage()) {
            stack.shrink(1);
            return;
        }

        stack.setDamageValue(damage);
    }
}
