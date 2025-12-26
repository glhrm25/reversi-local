package user_interface
import model.validMoves
import model.*
import model.Color.*

const val BLACK_SYMBOL = '#'
const val WHITE_SYMBOL = '@'
const val EMPTY_SYMBOL = '.'
const val TARGETS_SYMBOL = '*'

fun Game.show(targets: Boolean) {
    println("  " + COLUMNS.joinToString(" "))
    val validMoves = if (state is Run) validMoves((this.state as Run).turn).toSet() else emptySet()
    Position.values
        .map{
            if (targets && it in validMoves) TARGETS_SYMBOL
            else board[it].symbol()
        }
        .chunked(BOARD_SIZE)
        .forEachIndexed { idx, row ->
            println("${idx+1} ${row.joinToString(" ")}")
        }

    println("$BLACK_SYMBOL =  ${board.count{ (_, player) -> player == BLACK}} | $WHITE_SYMBOL =  ${board.count { (_, player) -> player == WHITE}}")
    when (state) {
        is Run -> println("Turn: ${(state as Run).turn.symbol()}")
        is Win -> println("Winner: ${(state as Win).winner.symbol()}")
        Draw -> println("Draw")
    }
}

fun Color?.symbol(): Char =
    when (this) {
        BLACK -> BLACK_SYMBOL
        WHITE -> WHITE_SYMBOL
        else -> EMPTY_SYMBOL
    }

fun ClashRun.showHeader() {
    if (game is NamedGame)
        println("You are player ${side.color.symbol()} in game ${game.name}.")
    else if (game.state is Run)
        println("You are player ${(game.state as Run).turn.symbol()} in local game.")
}

fun showTargets(side: Player) =
    println("Targets = ${side.toggleTargets.toOnOrOff()}")

fun Boolean.toOnOrOff() = if (this) "On" else "Off"