package fr.lewon.dofus.bot.sniffer.model.types.game.data.items

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.effects.ObjectEffect
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ObjectItemToSellInNpcShop : ObjectItemMinimalInformation() {
	var objectPrice: Double = 0.0
	var buyCriterion: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		objectPrice = stream.readVarLong().toDouble()
		buyCriterion = stream.readUTF()
	}
}