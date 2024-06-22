package net.walksanator.spectralsync

import de.dafuqs.spectrum.SpectrumCommon
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import kotlin.jvm.optionals.getOrNull

class SyncBook(properties: Properties) : Item(properties) {
    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand
    ): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(interactionHand)
        if (level.isClientSide()) {
            return super.use(level,player,interactionHand)
        }
        level as ServerLevel
        player as ServerPlayer

        if (stack.tag == null) {
            stack.tag = CompoundTag()
        }

        val adv = player.advancements
        val srvAdv  = level.server.advancements
        val tag = stack.tag?.getList("advancements", StringTag.TAG_STRING.toInt())
        if ( (tag?.size?: 0) == 0 || player.isShiftKeyDown) {
            val advStrs = srvAdv.allAdvancements.filter {
                val prog = adv.getOrStartProgress(it)
                prog.isDone && it.id.namespace == SpectrumCommon.MOD_ID
            }.map {it.id.toString()}
            val list = ListTag()
            advStrs.forEach {
                list.add(StringTag.valueOf(it))
            }
            stack.tag?.put("advancements",list)
        } else {
            tag!!.map { (it as StringTag).asString }
                .mapNotNull { ResourceLocation.read(it).result().getOrNull() }
                .mapNotNull {srvAdv.getAdvancement(it)}
                .forEach {
                    val prog = adv.getOrStartProgress(it)
                    prog.remainingCriteria.forEach { crit ->
                        adv.award(it,crit)
                    }
                }
        }

        return super.use(level, player, interactionHand)
    }

    override fun isFoil(itemStack: ItemStack): Boolean {
        val tag = itemStack.tag?.getList("advancements", StringTag.TAG_STRING.toInt())?.size?: 0
        return tag > 0
    }
}