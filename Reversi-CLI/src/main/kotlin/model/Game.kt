package model
import model.Color.*

const val BOARD_SIZE = 8 // Sets the number of rows and columns of the board
const val BOARD_CELLS = BOARD_SIZE * BOARD_SIZE

typealias Board = Map<Position, Color>

open class Game (
    open val owner : Color = BLACK,
    open val board: Board = generateBoard(),
    open val state: GameState = Run(owner),
){
    open fun copy(owner: Color = this.owner, board: Board = this.board, state: GameState = this.state) = Game(owner, board, state)
}

data class NamedGame(
    val name: Name,
    override val owner: Color = BLACK,
    override val board: Board = generateBoard(),
    override val state: GameState = Run(owner),
): Game(owner, board, state)

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
open class Run(val turn: Color) : GameState() {

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

class RunPassed(tr: Color): Run(tr)
data class Win(val winner: Color): GameState()
data object Draw: GameState()

fun Game.play(move: Position): Game {
    check(this.state is Run) {"Game has ended."}
    require(move in validMoves((state as Run).turn)){"Invalid move $move."}

    val newBoard = board + (move to (state as Run).turn) + turnMoves((state as Run).turn, move)
    return this.copy(
        board = newBoard,
        state = updateState(newBoard, state as Run),
    )
}

fun Game.pass(): Game =
    with(this) {
        check(state is Run) {"Game has ended."}
        check(validMoves((state as Run).turn).isEmpty()) { "There is possible moves for you to make." }

        if (state is RunPassed)
            copy(state = getEndState(board))

        else
            copy(state = RunPassed((state as Run).turn.otherColor))
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
private fun Board.mostCommonPieces(): Color? {
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
private fun generateBoard(): Board {
    val middleColumn = BOARD_SIZE / 2

   return emptyMap<Position, Color>() +
           (Position(toBoardIndex(middleColumn, COLUMNS[middleColumn])) to BLACK) +
           (Position(toBoardIndex(middleColumn + 1, COLUMNS[middleColumn - 1])) to BLACK) +
           (Position(toBoardIndex(middleColumn + 1, COLUMNS[middleColumn])) to WHITE) +
           (Position(toBoardIndex(middleColumn, COLUMNS[middleColumn - 1])) to WHITE)
}