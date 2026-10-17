package fr.hardel.betterdeath.mixin;

import fr.hardel.betterdeath.BetterDeathRules;
import fr.hardel.betterdeath.FootholdHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements FootholdHolder {
    @Unique
    private @Nullable Vec3 betterdeath$foothold;
    @Unique
    private @Nullable ResourceKey<Level> betterdeath$footholdDimension;

    @Shadow
    private void transferInventoryXpAndScore(Player oldPlayer) {
    }

    @Inject(method = "doTick", at = @At("TAIL"))
    private void betterdeath$trackFoothold(CallbackInfo callback) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        if (self.mainSupportingBlockPos.isPresent()) {
            betterdeath$foothold = self.position();
            betterdeath$footholdDimension = self.level().dimension();
        }
    }

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void betterdeath$keepInventory(ServerPlayer oldPlayer, boolean restoreAll, CallbackInfo callback) {
        if (!restoreAll && BetterDeathRules.applies(((ServerPlayer) (Object) this).level().getGameRules())) {
            transferInventoryXpAndScore(oldPlayer);
        }
    }

    @Override
    public Optional<Vec3> betterdeath$foothold(ResourceKey<Level> dimension) {
        return dimension.equals(betterdeath$footholdDimension) ? Optional.ofNullable(betterdeath$foothold) : Optional.empty();
    }
}
