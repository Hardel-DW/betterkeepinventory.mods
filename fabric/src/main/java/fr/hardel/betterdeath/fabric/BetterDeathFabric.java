package fr.hardel.betterdeath.fabric;

import fr.hardel.betterdeath.BetterDeathRules;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.gamerules.GameRuleCategory;

public final class BetterDeathFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        GameRuleCategory.register(BetterDeathRules.CATEGORY.id());
        BetterDeathRules.register((id, rule) -> Registry.register(BuiltInRegistries.GAME_RULE, id, rule));
    }
}
