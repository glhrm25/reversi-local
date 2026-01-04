package reversi.model

enum class Direction(val difRow: Int, val difCol: Int) {
    UP(-1,0), DOWN(1,0), LEFT(0,-1), RIGHT(0,1),
    UP_LEFT(-1,-1), UP_RIGHT(-1,1), DOWN_LEFT(1,-1), DOWN_RIGHT(1,1)
}

/**
 * Computes all valid move positions for the given player.
 *
 * A position is considered a valid move if the board cell is empty and there is
 * at least one direction in which placing a piece would result in a valid move
 * according to the game rules.
 *
 * @param turn Current player's color
 * @return A set of positions where the player can legally place a piece
 */
fun Game.validMoves(turn: PlayerColor): Set<Position> =
    Position.values.filter { p ->
        board[p] == null && Direction.entries.any { dir -> board.isValidMove(p, turn, dir).first }
    }.toSet()

/**
 * Determines all opponent pieces that would be turned as a result of a move.
 *
 * For a given move position and player, this method evaluates all directions and
 * collects the opponent pieces that would be flipped in each valid direction.
 *
 * @param turn Current player's color
 * @param move Position where the piece is being placed
 * @return A list of pairs containing:
 *         - the position of each piece to be turned
 *         - the color it will become after the move
 */
fun Game.turningPieces(turn: PlayerColor, move: Position): List<Pair<Position, PlayerColor>> =
    buildSet {
        Direction.entries.forEach { dir ->
            val isValidMove = board.isValidMove(move, turn, dir)
            if (isValidMove.first)
                add(isValidMove.second)
        }
    }.flatten().map { it to turn }

/**
 * Checks whether two board positions are aligned according to the given direction.
 *
 * For horizontal directions (LEFT, RIGHT), both positions must be on the same row.
 * For vertical directions (UP, DOWN), both positions must be on the same column.
 * For diagonal directions, the row and column differences between the positions
 * must match the expected pattern for that diagonal.
 *
 * @param pos1 First position to compare
 * @param pos2 Second position to compare
 * @param direction Direction in which the alignment is being validated
 * @return True if the two positions are aligned in the given direction
 */
private fun inLine2(pos1: Position, pos2: Position, direction: Direction): Boolean {
    val row1 = pos1.row
    val col1 = pos1.column
    val row2 = pos2.row
    val col2 = pos2.column

    return when (direction) {
        Direction.LEFT, Direction.RIGHT -> row1 == row2
        Direction.UP, Direction.DOWN  -> col1 == col2
        Direction.DOWN_RIGHT -> (row2 - row1) == (col2 - col1)
        Direction.UP_LEFT -> (row1 - row2) == (col1 - col2)
        Direction.DOWN_LEFT -> (row2 - row1) == (col1 - col2)
        Direction.UP_RIGHT -> (row1 - row2) == (col2 - col1)
    }
}

/**
 * Validates a move in a specific direction and determines which opponent pieces would be flipped if the move is applied.
 *
 * Starting from the given move position, this method collects consecutive opponent pieces in the specified direction, stopping when the sequence is broken.
 * The move is considered valid in that direction if the sequence is non-empty and is immediately followed by a piece of the current player.
 *
 * @param move Position where the piece is being placed
 * @param turn Current player's color
 * @param dir Direction in which the move is being validated
 * @return A Pair where:
 *         - first: true if the move is valid in the given direction
 *         - second: list of opponent positions that would be flipped
 */
private fun Board.isValidMove(move: Position, turn:PlayerColor, dir: Direction): Pair<Boolean, List<Position>> {
    val piecesToFlip = cellsInDirection(move, dir).takeWhile { this[it] == turn.opponent }

    return Pair(piecesToFlip.isNotEmpty() && this[piecesToFlip.last() + dir] == turn, piecesToFlip)
}

/**
 * Collects all board positions starting from a given position and moving continuously in the specified direction.
 *
 * The traversal begins at the adjacent cell in the given direction and continues while the positions remain valid and aligned with that direction.
 *
 * @param from Starting position
 * @param dir Direction in which the positions are collected
 * @return A list of positions in the given direction, excluding the starting position
 */
private fun cellsInDirection(from: Position, dir: Direction) = buildList {
    var pos = from + dir
    while (pos != Position.INVALID && inLine2(from, pos, dir)) {
        add(pos)
        pos += dir
    }
}
