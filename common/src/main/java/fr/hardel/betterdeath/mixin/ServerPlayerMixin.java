package fr.hardel.betterdeath.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import fr.hardel.betterdeath.BetterDeathRules;
import fr.hardel.betterdeath.FootholdHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements FootholdHolder {
    @Unique
    private @Nullable Vec3 betterdeath$foothold;
    @Unique
    private @Nullable ResourceKey<Level> betterdeath$footholdDimension;

    @Inject(method = "doTick", at = @At("TAIL"))
    private void betterdeath$trackFoothold(CallbackInfo callback) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        if (self.mainSupportingBlockPos.isPresent()) {
            betterdeath$foothold = self.position();
            betterdeath$footholdDimension = self.level().dimension();
        }
    }

    @ModifyExpressionValue(method = "restoreFrom", at = @At(value = "INVOKE", target = "Ljava/lang/Boolean;booleanValue()Z", ordinal = 0),
        slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/world/level/gamerules/GameRules;KEEP_INVENTORY:Lnet/minecraft/world/level/gamerules/GameRule;")))
    private boolean betterdeath$keepInventory(boolean original, ServerPlayer oldPlayer) {
        return original || BetterDeathRules.keepsInventory(oldPlayer.level().getGameRules(), oldPlayer);
    }

    @Override
    public Optional<Vec3> betterdeath$foothold(ResourceKey<Level> dimension) {
        return dimension.equals(betterdeath$footholdDimension) ? Optional.ofNullable(betterdeath$foothold) : Optional.empty();
    }
}
