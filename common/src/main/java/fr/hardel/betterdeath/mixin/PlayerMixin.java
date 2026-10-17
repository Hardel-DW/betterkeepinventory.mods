package fr.hardel.betterdeath.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fr.hardel.betterdeath.BetterDeathRules;
import fr.hardel.betterdeath.DeathToll;
import fr.hardel.betterdeath.Keeper;
import fr.hardel.betterdeath.StashedStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @WrapOperation(method = "dropEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;dropAll()V"))
    private void betterdeath$stashInsteadOfDropping(Inventory inventory, Operation<Void> original, ServerLevel level) {
        if (!BetterDeathRules.applies(level.getGameRules())) {
            original.call(inventory);
            return;
        }

        ServerPlayer player = (ServerPlayer) (Object) this;
        List<StashedStack> stash = new DeathToll(level.getGameRules(), player.getRandom()).take(player);
        if (!stash.isEmpty()) {
            Keeper.summon(player, stash);
        }
    }

    @ModifyReturnValue(method = "getBaseExperienceReward", at = @At("RETURN"))
    private int betterdeath$dropNoExperience(int original, ServerLevel level) {
        return BetterDeathRules.applies(level.getGameRules()) ? 0 : original;
    }
}
