package fr.hardel.betterdeath;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.function.BiConsumer;

public final class BetterDeathRules {
    public static final String MOD_ID = "betterdeath";
    public static final GameRuleCategory CATEGORY = new GameRuleCategory(id(MOD_ID));
    public static final GameRule<Boolean> BETTER_KEEP_INVENTORY = bool(true);
    public static final GameRule<Integer> EXPERIENCE_LOSS = percent(50);
    public static final GameRule<Integer> DURABILITY_LOSS = percent(15);
    public static final GameRule<Boolean> KEEP_DAMAGEABLE_ITEMS = bool(true);
    public static final GameRule<Integer> KEEP_STACK_CHANCE = percent(0);
    public static final GameRule<Integer> ALLAY_LIFETIME = integer(1, Integer.MAX_VALUE, 36000);

    private BetterDeathRules() {
    }

    public static void register(BiConsumer<Identifier, GameRule<?>> registry) {
        registry.accept(id("better_keep_inventory"), BETTER_KEEP_INVENTORY);
        registry.accept(id("experience_loss"), EXPERIENCE_LOSS);
        registry.accept(id("durability_loss"), DURABILITY_LOSS);
        registry.accept(id("keep_damageable_items"), KEEP_DAMAGEABLE_ITEMS);
        registry.accept(id("keep_stack_chance"), KEEP_STACK_CHANCE);
        registry.accept(id("allay_lifetime"), ALLAY_LIFETIME);
    }

    public static boolean applies(GameRules rules) {
        return !rules.get(GameRules.KEEP_INVENTORY) && rules.get(BETTER_KEEP_INVENTORY);
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    private static GameRule<Integer> percent(int defaultValue) {
        return integer(0, 100, defaultValue);
    }

    private static GameRule<Integer> integer(int min, int max, int defaultValue) {
        return new GameRule<>(CATEGORY, GameRuleType.INT, IntegerArgumentType.integer(min, max), GameRuleTypeVisitor::visitInteger, Codec.intRange(min, max),
            value -> value, defaultValue, FeatureFlagSet.of());
    }

    private static GameRule<Boolean> bool(boolean defaultValue) {
        return new GameRule<>(CATEGORY, GameRuleType.BOOL, BoolArgumentType.bool(), GameRuleTypeVisitor::visitBoolean, Codec.BOOL, value -> value ? 1 : 0,
            defaultValue, FeatureFlagSet.of());
    }
}
