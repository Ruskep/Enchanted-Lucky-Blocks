package com.luckychallenges.upgrade;

import com.luckychallenges.challenge.ChallengeManager;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class UpgradeEffects {

    private static final Identifier SPEED_MOD_ID = Identifier.of("luckychallenges", "speed_boost");

    public static void apply(ServerPlayerEntity player, UpgradeType type, int level) {
        switch (type) {
            case HEALTH -> {
                EntityAttributeInstance attr = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
                if (attr != null) {
                    int penalty = ChallengeManager.getHealthPenalty(player.getUuid());
                    double base = Math.max(1.0, 20.0 + level * 2.0 - penalty);
                    attr.setBaseValue(base);
                    if (player.getHealth() > player.getMaxHealth())
                        player.setHealth(player.getMaxHealth());
                }
            }
            case ARMOR -> {
                EntityAttributeInstance attr = player.getAttributeInstance(EntityAttributes.ARMOR);
                if (attr != null) attr.setBaseValue(level * 1.0);
            }
            case SPEED_BOOST -> {
                EntityAttributeInstance attr = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
                if (attr != null) {
                    attr.removeModifier(SPEED_MOD_ID);
                    if (level > 0) {
                        attr.addPersistentModifier(new EntityAttributeModifier(
                            SPEED_MOD_ID, 0.2 * level,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                    }
                }
            }
            case NIGHT_VISION -> {
                if (level > 0) {
                    player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, true));
                } else {
                    player.removeStatusEffect(StatusEffects.NIGHT_VISION);
                }
            }
            case FIRE_IMMUNE -> {
                // Обрабатывается в PerkEvents.tickFireImmune
            }
            case NO_FALL -> {
                // Обрабатывается в PerkEvents.tickNoFall
            }
            case REGEN -> {
                // Обрабатывается в PerkEvents.tickRegen
            }
            case STEP_UP -> {
                EntityAttributeInstance attr = player.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
                if (attr != null) attr.setBaseValue(level > 0 ? 2.0025 : 0.6);
            }
            default -> {}
        }
    }
}
