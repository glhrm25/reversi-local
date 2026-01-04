package reversi.model

enum class Direction(val difRow: Int, val difCol: Int) {
    UP(-1,0), DOWN(1,0), LEFT(0,-1), RIGHT(0,1),
    UP_LEFT(-1,-1), UP_RIGHT(-1,1), DOWN_LEFT(1,-1), DOWN_RIGHT(1,1)
}

fun cellsInDirection(from: Position, dir: Direction) = buildList {
    var pos = from + dir
    while (pos != Position.INVALID) {
        add(pos)
        pos += dir
    }
}

/**
 * @return Pair<Boolean> = is Move Valid ; List<Position> The turning pieces for the specified direction.
*/
// Neste caso é útil retornar tambem a linha para evitar repetir codigo
private fun Board.isValidMove(move: Position, turn:PlayerColor, dir: Direction): Pair<Boolean, List<Position>> {
    val line = cellsInDirection(move, dir)
        .takeWhile { this[it] == turn.opponent && inLine2(move, it, dir) }

    return Pair(line.isNotEmpty() && this[line.last() + dir] == turn, line)
}

fun Game.validMoves(turn: PlayerColor): Set<Position> =
    Position.values.filter { p ->
        board[p] == null && Direction.entries.any { dir -> board.isValidMove(p, turn, dir).first }
    }.toSet()

fun Game.turnMoves(turn: PlayerColor, move: Position): List<Pair<Position, PlayerColor>> =
    buildSet {
        Direction.entries.forEach { dir ->
            val isValidMove = board.isValidMove(move, turn, dir)
            if (isValidMove.first)
                add(isValidMove.second)
        }
    }.flatten().map { it to turn }

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
