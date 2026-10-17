package fr.hardel.betterdeath.mixin;

import fr.hardel.betterdeath.Keeper;
import fr.hardel.betterdeath.KeeperHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Allay.class)
public abstract class AllayMixin extends PathfinderMob implements KeeperHolder {
    @Unique
    private static final String KEEPER_KEY = "betterdeath:keeper";
    @Unique
    private @Nullable Keeper betterdeath$keeper;

    protected AllayMixin(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Inject(method = "customServerAiStep", at = @At("HEAD"), cancellable = true)
    private void betterdeath$keep(ServerLevel level, CallbackInfo callback) {
        if (betterdeath$keeper != null) {
            betterdeath$keeper.tick((Allay) (Object) this, level);
            callback.cancel();
        }
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void betterdeath$ignoreDamage(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> callback) {
        if (betterdeath$keeper != null && !source.is(DamageTypes.GENERIC_KILL)) {
            callback.setReturnValue(false);
        }
    }

    @Inject(method = "canPickUpLoot", at = @At("HEAD"), cancellable = true)
    private void betterdeath$pickUpNothing(CallbackInfoReturnable<Boolean> callback) {
        if (betterdeath$keeper != null) {
            callback.setReturnValue(false);
        }
    }

    @Inject(method = "dropEquipment", at = @At("HEAD"), cancellable = true)
    private void betterdeath$dropNothing(ServerLevel level, CallbackInfo callback) {
        if (betterdeath$keeper != null) {
            callback.cancel();
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void betterdeath$saveKeeper(ValueOutput output, CallbackInfo callback) {
        output.storeNullable(KEEPER_KEY, Keeper.CODEC, betterdeath$keeper);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void betterdeath$loadKeeper(ValueInput input, CallbackInfo callback) {
        betterdeath$keeper = input.read(KEEPER_KEY, Keeper.CODEC).orElse(null);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        return betterdeath$keeper != null ? InteractionResult.PASS : super.interact(player, hand, location);
    }

    @Override
    public boolean fireImmune() {
        return betterdeath$keeper != null || super.fireImmune();
    }

    @Override
    public boolean isPushable() {
        return betterdeath$keeper == null && super.isPushable();
    }

    @Override
    public boolean isPushedByFluid() {
        return betterdeath$keeper == null && super.isPushedByFluid();
    }

    @Override
    public boolean ignoreExplosion(Explosion explosion) {
        return betterdeath$keeper != null || super.ignoreExplosion(explosion);
    }

    @Override
    public @Nullable Keeper betterdeath$keeper() {
        return betterdeath$keeper;
    }

    @Override
    public void betterdeath$setKeeper(Keeper keeper) {
        betterdeath$keeper = keeper;
    }
}
