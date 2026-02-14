package org.tywrapstudios.khat.mixin;

import com.llamalad7.mixinextras.sugar.Local;

import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.tywrapstudios.khat.compat.SparkCompatKt;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import me.lucko.spark.common.platform.world.AsyncWorldInfoProvider;

@Mixin(value = AsyncWorldInfoProvider.class)
@Debug(export = true)
public abstract class AsyncWorldInfoProviderMixin {
    @Inject(
            method = "get",
            at = @At(value = "INVOKE",
                    target = "Lme/lucko/spark/common/SparkPlugin;log(Ljava/util/logging/Level;Ljava/lang/String;)V"),
            remap = false
    )
    private void handleSparkWorldTimeOut(CompletableFuture<?> future, CallbackInfoReturnable<?> cir, @Local TimeoutException var4) {
        SparkCompatKt.handleSparkWorldTimeOut(var4);
    }
}
