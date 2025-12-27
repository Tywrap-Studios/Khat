package org.tywrapstudios.khat.mixin;

import net.minecraft.CrashReport;
//? if >=1.21
import net.minecraft.ReportType;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.tywrapstudios.khat.logic.HandleMinecraft;

import java.io.File;
import java.nio.file.Path;import java.util.List;

@Mixin(CrashReport.class)
@Debug(export = true)
public abstract class CrashReportMixin {
    @Shadow
    public abstract Throwable getException();

    @Shadow
    @Final
    private static Logger LOGGER;

    @Inject(//? if >=1.21 {
            method = "saveToFile(Ljava/nio/file/Path;Lnet/minecraft/ReportType;Ljava/util/List;)Z",
            //?} else {
            /*method = "saveToFile",
            *///?}
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/io/Writer;write(Ljava/lang/String;)V",
                    shift = At.Shift.AFTER
            )
    )
    //? if >= 1.21 {
    private void sendWebhooksOnCrash(Path path, ReportType reportType, List<String> list, CallbackInfoReturnable<Boolean> cir) {
    //?} else {
    /*private void sendWebhooksOnCrash(File file, CallbackInfoReturnable<Boolean> cir) {
    *///?}
        try {
            //? if < 1.21
            //Path path = file.toPath();
            HandleMinecraft.INSTANCE.handleCrash(getException(), path);
        } catch (Exception e) {
            LOGGER.error("An error occurred while trying to send the crash report to Discord.", e);
        }
    }
}
