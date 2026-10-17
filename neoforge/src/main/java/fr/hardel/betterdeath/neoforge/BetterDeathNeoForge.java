package fr.hardel.betterdeath.neoforge;

import fr.hardel.betterdeath.BetterDeathRules;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterGameRuleCategoryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(BetterDeathRules.MOD_ID)
public final class BetterDeathNeoForge {
    public BetterDeathNeoForge(IEventBus modBus) {
        modBus.addListener((RegisterGameRuleCategoryEvent event) -> event.register(BetterDeathRules.CATEGORY));
        modBus.addListener((RegisterEvent event) -> BetterDeathRules.register((id, rule) -> event.register(Registries.GAME_RULE, id, () -> rule)));
    }
}
