package fr.lewon.dofus.bot.handlers.movement

import fr.lewon.dofus.bot.core.d2p.maps.D2PMapsAdapter
import fr.lewon.dofus.bot.gui.vldb.overlay.impl.LOSOverlay
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.move.CurrentMapMessage
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object CurrentMapEventHandler : IEventHandler<CurrentMapMessage> {
    override fun onEventReceived(socketResult: CurrentMapMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        val completeCellDataByCellId = D2PMapsAdapter.getCompleteCellDataByCellId(socketResult.mapId)
        gameInfo.dofusBoard.updateCells(completeCellDataByCellId.values.map { it.cellData })
        gameInfo.completeCellDataByCellId = completeCellDataByCellId
        LOSOverlay.updateOverlay(gameInfo)
    }
}