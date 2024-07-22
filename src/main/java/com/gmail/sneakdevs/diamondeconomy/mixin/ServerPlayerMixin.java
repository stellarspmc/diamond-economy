package com.gmail.sneakdevs.diamondeconomy.mixin;

import com.gmail.sneakdevs.diamondeconomy.DiamondUtils;
import com.gmail.sneakdevs.diamondeconomy.config.DiamondEconomyConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void tickMixin(CallbackInfo ci) {
        int playTime = ((ServerPlayer)(Object)this).getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));
        if (playTime > 0 && playTime % (900 * 20) == 0) DiamondUtils.getDatabaseManager().changeBalance(((ServerPlayer)(Object)this).getStringUUID(), 1);
    }
}