package model

private val directions = listOf(
    1, -1,  // Horizontal
    BOARD_SIZE, -BOARD_SIZE, // Vertical
    BOARD_SIZE + 1, -BOARD_SIZE - 1, // backslash
    BOARD_SIZE - 1, -BOARD_SIZE + 1, // slash
)

fun Game.validMoves(turn: Color): List<Position> {
    val opponent = turn.otherColor

    val l = board.entries.map { (c, p) ->
        if (p == opponent){
            directions.map{
                val pd = c.index + it
                if (pd in 0 until BOARD_CELLS &&
                    board[Position(pd)] == null &&
                    this.turnMoves(turn, Position(pd)).isNotEmpty()
                    ) Position(pd)
                else null
            }
        }
        else emptyList()
    }
    return l.flatten().filterNotNull()
}

fun Game.turnMoves(turn: Color, move: Position): List<Pair<Position, Color>> =
    directions.map{
        this.turnMovesByDirection(turn, move, it)
    }.flatten().distinct()

private fun Game.turnMovesByDirection(turn: Color, move: Position, direction: Int = 0): List<Pair<Position, Color>> {
    val opponent = turn.otherColor

    val idx = move.index + direction
    val range = if (direction < 0) (idx downTo 0 step -direction)
                 else (idx until BOARD_CELLS step direction)

    buildList {
        for (i in range) {
            if (!inLine(idx, Position(i).index, direction)) break

            when (board[Position(i)]) {
                opponent -> add(i)
                turn -> {
                    return this.map { Position(it) to turn }
                }
                else -> break
            }
        }
    }
    return emptyList() // Returns emptyList if it couldn't find a player's piece on the same line/column/diagonal has the move.
}

private fun inLine(idx1: Int, idx2: Int, direction: Int): Boolean {
    val row1 = Position(idx1).row
    val col1 = Position(idx1).column
    val row2 = Position(idx2).row
    val col2 = Position(idx2).column

    return when (direction) {
        1, -1 -> row1 == row2 // horizontal
        BOARD_SIZE, -BOARD_SIZE -> col1 == col2 // vertical
        BOARD_SIZE + 1 -> (row2 - row1) == (col2 - col1) // diagonal ↘
        -BOARD_SIZE - 1 -> (row1 - row2) == (col1 - col2) // diagonal ↖
        BOARD_SIZE - 1 -> (row2 - row1) == (col1 - col2) // diagonal ↙
        -BOARD_SIZE + 1 -> (row1 - row2) == (col2 - col1) // diagonal ↗
        else -> false
    }
}