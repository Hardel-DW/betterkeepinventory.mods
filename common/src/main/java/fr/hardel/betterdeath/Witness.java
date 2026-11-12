package fr.hardel.betterdeath;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.ToIntFunction;

public class Witness {
    private static Witness present = new Witness();

    public static Witness present() {
        return present;
    }

    public static void appoint(Witness witness) {
        present = witness;
    }

    public void stashes(ServerPlayer player, List<StashedStack> stash) {}

    public void claims(ServerLevel level, Player receiver, List<StashedStack> stash, ToIntFunction<StashedStack> give) {
        stash.forEach(give::applyAsInt);
    }

    public void expires(ServerLevel level, BlockPos pos, UUID owner, List<StashedStack> stash) {}
}
