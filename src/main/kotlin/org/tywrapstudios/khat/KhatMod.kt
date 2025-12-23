package org.tywrapstudios.khat

import net.fabricmc.api.DedicatedServerModInitializer
import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object KhatMod : DedicatedServerModInitializer {
    // This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	val LOGGER: Logger = LoggerFactory.getLogger("Khat")
	const val VERSION: String =  /*$ mod_version*/"1.0.0"
	const val MINECRAFT: String =  /*$ minecraft*/"1.21.1"

    override fun onInitializeServer() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Hello Fabric world!")

        //? if !release
        //LOGGER.warn("I'm still a template!")

        //? if fapi: <0.100
        /*LOGGER.info("Fabric API is old on this version");*/
    }

    /**
     * Adapts to the ResourceLocation/Identifier changes introduced in 1.21.
     */
    fun id(namespace: String, path: String): ResourceLocation {
		//? if <1.21 {
		/*return new ResourceLocation(namespace, path);
        */
		//?} else
		return ResourceLocation.fromNamespaceAndPath(namespace, path)
	}
}