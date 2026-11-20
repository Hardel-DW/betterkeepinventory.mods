package fr.hardel.betterdeath.fabric;

import fr.hardel.betterdeath.StashedStack;
import fr.hardel.betterdeath.Witness;
import fr.hardel.keeper.capture.Capture;
import fr.hardel.keeper.capture.ItemCapture;
import fr.hardel.keeper.event.Carrier;
import fr.hardel.keeper.event.EventType;
import fr.hardel.keeper.event.EventTypes;
import fr.hardel.keeper.event.Form;
import fr.hardel.keeper.event.ItemMove;
import fr.hardel.keeper.event.Part;
import fr.hardel.keeper.event.Sentence;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.ToIntFunction;

public final class KeeperWitness extends Witness {
    private static final String MOD_ID = "betterdeath";
    private static final EventType<ItemMove> STASH = moves().build();
    private static final EventType<ItemMove> CLAIM = moves().build();
    private static final EventType<ItemMove> EXPIRE = moves().tone(EventType.Tone.WARN).build();

    public KeeperWitness() {
        EventTypes.register(Identifier.fromNamespaceAndPath(MOD_ID, "stash"), STASH);
        EventTypes.register(Identifier.fromNamespaceAndPath(MOD_ID, "claim"), CLAIM);
        EventTypes.register(Identifier.fromNamespaceAndPath(MOD_ID, "expire"), EXPIRE);
    }

    private static EventType.Builder<ItemMove> moves() {
        return EventType.of(ItemMove.STREAM_CODEC, ItemMove.CODEC, move -> Sentence.of(Part.item(move.form().stack(), move.count()).varying()))
            .family(EventTypes.ITEMS).targets(Registries.ITEM).grouped().merges(ItemMove::merge);
    }

    @Override
    public void stashes(ServerPlayer player, List<StashedStack> stash) {
        stash.forEach(kept -> Capture.record(player, STASH, new ItemMove(Form.of(kept.stack()), kept.stack().getCount(), Capture.actor(player), Carrier.Place.WORLD)));
    }

    @Override
    public void claims(ServerLevel level, Player receiver, List<StashedStack> stash, ToIntFunction<StashedStack> give) {
        Carrier actor = new Carrier.Player(receiver.getUUID());
        ItemCapture.counted(() -> stash.stream().map(kept -> new ItemMove(Form.of(kept.stack()), give.applyAsInt(kept), Carrier.Place.WORLD, actor)).filter(move -> move.count() > 0)
            .forEach(move -> Capture.record(level, actor, receiver.blockPosition(), CLAIM, move)));
    }

    @Override
    public void expires(ServerLevel level, BlockPos pos, UUID owner, List<StashedStack> stash) {
        stash.forEach(kept -> Capture.record(level, new Carrier.Player(owner), pos, EXPIRE, new ItemMove(Form.of(kept.stack()), kept.stack().getCount(), Carrier.Place.WORLD, Carrier.Place.VOID)));
    }
}
