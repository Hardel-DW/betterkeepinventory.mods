package fr.hardel.betterdeath.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
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
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.List;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @ModifyExpressionValue(method = "dropEquipment", at = @At(value = "INVOKE", target = "Ljava/lang/Boolean;booleanValue()Z", ordinal = 0),
        slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/world/level/gamerules/GameRules;KEEP_INVENTORY:Lnet/minecraft/world/level/gamerules/GameRule;")))
    private boolean betterdeath$keepEverything(boolean original, ServerLevel level) {
        return original || BetterDeathRules.keepsEverything(level.getGameRules(), (Player) (Object) this);
    }

    @WrapOperation(method = "dropEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;dropAll()V"))
    private void betterdeath$stashInsteadOfDropping(Inventory inventory, Operation<Void> original, ServerLevel level) {
        if (!level.getGameRules().get(BetterDeathRules.BETTER_KEEP_INVENTORY)) {
            original.call(inventory);
            return;
        }

        ServerPlayer player = (ServerPlayer) (Object) this;
        List<StashedStack> stash = new DeathToll(level.getGameRules(), player.getRandom()).take(player);
        if (!stash.isEmpty()) {
            Keeper.summon(player, stash);
        }
    }

    @ModifyExpressionValue(method = "getBaseExperienceReward", at = @At(value = "INVOKE", target = "Ljava/lang/Boolean;booleanValue()Z", ordinal = 0),
        slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/world/level/gamerules/GameRules;KEEP_INVENTORY:Lnet/minecraft/world/level/gamerules/GameRule;")))
    private boolean betterdeath$dropNoExperience(boolean original, ServerLevel level) {
        return original || BetterDeathRules.keepsInventory(level.getGameRules(), (Player) (Object) this);
    }
}
