package reversi.model
import reversi.model.PlayerColor.*

const val BOARD_SIZE = 8 // Sets the number of rows and columns of the board
const val BOARD_CELLS = BOARD_SIZE * BOARD_SIZE

typealias Board = Map<Position, PlayerColor>

data class Game (
    val owner : PlayerColor = BLACK,
    val board: Board = generateBoard(),
    val state: GameState = Run(owner),
)

sealed class GameState
/**
 * Represents an ongoing game state where a player is currently taking a turn.
 *
 * This class is declared as open so that other specialized run states
 * (such as [RunPassed]) can inherit from it.
 *
 * Equality considerations
 * By default, Kotlin classes compare by reference (memory address). Since we need
 * to compare [Run] objects by value (e.g., when serializing and deserializing a game),
 * this class overrides [equals] and [hashCode].
 */
open class Run(val turn: PlayerColor) : GameState() {

    /**
     * Compares this [Run] with another object for structural equality.
     *
     * Two [Run] instances are considered equal if:
     * - They are of the same runtime class (ensuring Run ≠ RunPassed), and
     * - They represent the same player's turn.
     */
    override fun equals(other: Any?) = when {
        this === other -> true
        other !is Run -> false
        else -> this::class == other::class && this.turn == other.turn
    }
    /**
     * Generates a hash code consistent with [equals].
     * Combines the class type and the player's color to ensure unique hashing
     * between different subclasses (e.g., Run vs. RunPassed).
     */
    override fun hashCode() = 31 * turn.hashCode() + this::class.hashCode()
}
    /**
     * Alternative implementation.
     *
     * This version treats [Run] and all its subclasses as equal if they share the same turn color
     * Example: Run(BLACK) == RunPassed(BLACK)
     *
     * It can simplify serialization tests but might introduce logical ambiguity
     * in state transitions, so it’s commented out by default.
     */
/*
open class Run(val turn: Color) : GameState() {
    override fun equals(other: Any?) =
        other is Run && other.turn == turn
    override fun hashCode() = turn.hashCode()
}
*/

class RunPassed(tr: PlayerColor): Run(tr)
data class Win(val winner: PlayerColor): GameState()
data object Draw: GameState()

fun Game.play(move: Position): Game {
    check(this.state is Run) {"Game has ended."}
    require(move in validMoves(state.turn)){"Invalid move."}

    val newBoard = board + (move to state.turn) + turnMoves(state.turn, move)
    return this.copy(
        board = newBoard,
        state = updateState(newBoard, state),
    )
}

fun Game.pass(): Game =
    with(this) {
        check(state is Run) {"Game has ended."}
        check(validMoves(state.turn).isEmpty()) { "There is possible moves for you to make." }

        if (state is RunPassed)
            copy(state = getEndState(board))
        else
            copy(state = RunPassed(state.turn.otherColor))
    }

/**
 * @param board The game's board
 * @param state The game's actual state
 * @return Updated state of the game
 */
private fun updateState(board: Board, state: Run): GameState =
    if (board.size != BOARD_CELLS)
        Run(state.turn.otherColor)
    else
        getEndState(board)

/**
 * @return Player with the most amount of pieces on the board
 */
private fun Board.mostCommonPieces(): PlayerColor? {
    val dif = this.count{ it.value == BLACK} - this.count{ it.value == WHITE}
    return when {
        dif > 0 -> BLACK
        dif < 0 -> WHITE
        else -> null
    }
}

/**
 * @param board The game's board
 * @return Gamestate Win or Draw, depending on the number of pieces on the board for each player
 */
private fun getEndState(board: Board): GameState =
    when (board.mostCommonPieces()) {
        WHITE -> Win(WHITE)
        BLACK -> Win(BLACK)
        else -> Draw
    }

/**
 * @return Starting board with its middle cells already occupied with the players pieces.
  */
fun generateBoard(): Board {
    val middleColumn = BOARD_SIZE / 2

   return emptyMap<Position, PlayerColor>() +
           (Position(toBoardIndex(middleColumn, COLUMNS[middleColumn])) to BLACK) +
           (Position(toBoardIndex(middleColumn + 1, COLUMNS[middleColumn - 1])) to BLACK) +
           (Position(toBoardIndex(middleColumn + 1, COLUMNS[middleColumn])) to WHITE) +
           (Position(toBoardIndex(middleColumn, COLUMNS[middleColumn - 1])) to WHITE)
}