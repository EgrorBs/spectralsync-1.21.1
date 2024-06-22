package net.walksanator.spectralsync

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Rarity
import org.slf4j.LoggerFactory


object SpectralSync : ModInitializer {
    private val logger = LoggerFactory.getLogger("spectral-sync")
	val SYNC_BOOK = SyncBook(FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON).stacksTo(1))
	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Hello Fabric world!")
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation("spectral-sync", "syncbook"), SYNC_BOOK);

	}
}