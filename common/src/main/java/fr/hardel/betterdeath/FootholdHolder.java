package fr.hardel.betterdeath;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public interface FootholdHolder {
    Optional<Vec3> betterdeath$foothold(ResourceKey<Level> dimension);
}
