package org.tywrapstudios.khat.platform

import net.fabricmc.loader.api.FabricLoader
import org.objectweb.asm.tree.ClassNode
import org.slf4j.LoggerFactory
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class KhatMixinPlugin : IMixinConfigPlugin {
    private val logger = LoggerFactory.getLogger(KhatMixinPlugin::class.java)

    private val conditions = mutableMapOf<String, () -> Boolean>()

    override fun shouldApplyMixin(targetClassName: String?, mixinClassName: String?): Boolean {
        logger.debug("Checking whether $mixinClassName should be applied to $targetClassName.")
        return conditions[mixinClassName]?.invoke() ?: true
    }

    private fun addCondition(mixinClassName: String, conditionSupplier: () -> Boolean) {
        conditions[mixinClassName] = conditionSupplier
    }

    override fun onLoad(mixinPackage: String?) {
        addCondition("$mixinPackage.AsyncWorldInfoProviderMixin") {
            FabricLoader.getInstance().isModLoaded("spark")
        }
    }

    override fun getRefMapperConfig(): String? = null

    override fun acceptTargets(
        myTargets: Set<String?>?,
        otherTargets: Set<String?>?
    ) {
    }

    override fun getMixins(): List<String>? = null

    override fun preApply(
        targetClassName: String?,
        targetClass: ClassNode?,
        mixinClassName: String?,
        mixinInfo: IMixinInfo?
    ) {
    }

    override fun postApply(
        targetClassName: String?,
        targetClass: ClassNode?,
        mixinClassName: String?,
        mixinInfo: IMixinInfo?
    ) {
    }
}