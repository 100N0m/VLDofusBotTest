package fr.lewon.dofus.bot.game.fight

import fr.lewon.dofus.bot.gui.characters.form.ai.RangeSpellCombination
import fr.lewon.dofus.bot.util.ui.ConsoleLogger


class FightAI(
    private val playerMovePoints: Int,
    private val enemyMovePoints: Int,
    private val fightBoard: FightBoard,
    private val losSpellCombination: RangeSpellCombination,
    private val nonLosSpellCombination: RangeSpellCombination,
    private val contactSpellCombination: RangeSpellCombination,
    private val initialDepth: Int
) {

    fun selectBestDest(cells: List<FightCell>): FightCell {
        val playerFighter = fightBoard.getPlayerFighter() ?: error("Player not found")
        val playerPosition = playerFighter.fightCell
        var chosenCell = playerPosition
        var best = evaluateMove(playerPosition, playerPosition)

        for (cell in cells) {
            evaluateMove(playerPosition, cell).takeIf { it > best }?.let {
                chosenCell = cell
                best = it
            }
        }
        ConsoleLogger.debug("Best dest score : $best")
        return chosenCell
    }

    private fun evaluateMove(initialPlayerPosition: FightCell, newPlayerPosition: FightCell): Int {
        val state = FightState(0, fightBoard.clone(), newPlayerPosition)
        playPlayerMove(state, initialPlayerPosition, newPlayerPosition)
        return minValue(state, initialDepth)
    }

    private fun playPlayerMove(state: FightState, initialPlayerPosition: FightCell, newPlayerPosition: FightCell) {
        state.fb.move(initialPlayerPosition.cellId, newPlayerPosition.cellId, false)
        state.playerPosition = newPlayerPosition
        val dist = state.fb.getDist(newPlayerPosition, state.fb.closestEnemyPosition) ?: error("Invalid board")
        val los = state.fb.lineOfSight(newPlayerPosition, state.fb.closestEnemyPosition)
        if (losSpellCombination.keys.isNotEmpty() && los && dist in losSpellCombination.minRange..losSpellCombination.maxRange) {
            state.attacksDone += losSpellCombination.aiValue
        } else if (nonLosSpellCombination.keys.isNotEmpty() && dist in nonLosSpellCombination.minRange..nonLosSpellCombination.maxRange) {
            state.attacksDone += nonLosSpellCombination.aiValue
        } else if (contactSpellCombination.keys.isNotEmpty() && dist <= 1) {
            state.attacksDone += contactSpellCombination.aiValue
        }
    }

    private fun playEnemyMove(state: FightState, targetCell: FightCell) {
        state.fb.closestEnemyPosition = targetCell
    }

    private fun maxValue(state: FightState, depth: Int): Int {
        if (depth <= 0) {
            return evaluateState(state)
        }

        val accessibleCells = state.fb.getMoveCells(playerMovePoints, state.playerPosition)

        var v = Int.MIN_VALUE
        for (cell in accessibleCells) {
            val newState = state.clone()
            playPlayerMove(newState, state.playerPosition, cell)
            v = maxOf(v, minValue(newState, depth - 1))
        }
        return v
    }

    private fun minValue(state: FightState, depth: Int): Int {
        if (depth <= 0) {
            return evaluateState(state)
        }

        val accessibleCells = state.fb.getMoveCells(enemyMovePoints, state.fb.closestEnemyPosition)

        var v = Int.MAX_VALUE
        for (cell in accessibleCells) {
            val newState = state.clone()
            playEnemyMove(newState, cell)
            v = minOf(v, maxValue(newState, depth - 1))
        }
        return v
    }

    private fun evaluateState(state: FightState): Int {
        val dist = state.fb.getPathLength(state.playerPosition, state.fb.closestEnemyPosition) ?: 100000
        return state.attacksDone * 2000 - 5 * dist
    }

    private class FightState(var attacksDone: Int, val fb: FightBoard, var playerPosition: FightCell) {
        fun clone(): FightState {
            return FightState(attacksDone, fb.clone(), playerPosition)
        }
    }

}