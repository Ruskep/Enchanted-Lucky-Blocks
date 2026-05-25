package com.luckychallenges.mixin;

import com.luckychallenges.challenge.ChallengeManager;
import com.luckychallenges.coin.CoinManager;
import com.luckychallenges.data.PlayerDataPersistence;
import com.luckychallenges.upgrade.UpgradeManager;
import com.luckychallenges.upgrade.UpgradeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerDataPersistenceMixin {

    @Inject(method = "writeCustomData", at = @At("RETURN"))
    private void onWriteData(WriteView view, CallbackInfo ci) {
        ServerPlayerEntity self = (ServerPlayerEntity)(Object)this;
        WriteView lc = view.get("luckychallenges");
        lc.putInt("coins", CoinManager.getCoins(self.getUuid()));
        lc.putInt("healthPenalty", ChallengeManager.getHealthPenalty(self.getUuid()));
        lc.putInt("blocksBroken", PlayerDataPersistence.getBlocksBroken(self.getUuid()));
        lc.putInt("challengesDone", PlayerDataPersistence.getChallengesCompleted(self.getUuid()));
        WriteView upgrades = lc.get("upgrades");
        for (UpgradeType type : UpgradeType.values()) {
            int level = UpgradeManager.getLevel(self.getUuid(), type);
            if (level > 0) {
                upgrades.putInt(type.name(), level);
            }
        }
    }

    @Inject(method = "readCustomData", at = @At("RETURN"))
    private void onReadData(ReadView view, CallbackInfo ci) {
        var lcOpt = view.getOptionalReadView("luckychallenges");
        if (lcOpt.isEmpty()) return;

        ServerPlayerEntity self = (ServerPlayerEntity)(Object)this;
        ReadView lc = lcOpt.get();

        CoinManager.setCoins(self.getUuid(), lc.getInt("coins", 0));
        ChallengeManager.setHealthPenalty(self.getUuid(), lc.getInt("healthPenalty", 0));
        PlayerDataPersistence.getBlocksBrokenMap().put(self.getUuid(), lc.getInt("blocksBroken", 0));
        PlayerDataPersistence.getChallengesCompletedMap().put(self.getUuid(), lc.getInt("challengesDone", 0));

        var upgradesOpt = lc.getOptionalReadView("upgrades");
        if (upgradesOpt.isPresent()) {
            ReadView upgrades = upgradesOpt.get();
            for (UpgradeType type : UpgradeType.values()) {
                int level = upgrades.getInt(type.name(), 0);
                if (level > 0) {
                    UpgradeManager.setLevel(self.getUuid(), type, level);
                }
            }
        }
    }
}
