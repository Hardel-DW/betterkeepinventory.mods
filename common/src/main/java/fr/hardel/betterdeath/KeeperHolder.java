package fr.hardel.betterdeath;

import org.jspecify.annotations.Nullable;

public interface KeeperHolder {
    @Nullable Keeper betterdeath$keeper();

    void betterdeath$setKeeper(Keeper keeper);
}
