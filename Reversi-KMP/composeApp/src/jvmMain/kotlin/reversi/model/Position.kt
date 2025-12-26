package reversi.model

val COLUMNS = ('A' until 'A' + BOARD_SIZE).toList()
val ROWS = (1 .. BOARD_SIZE).toList()


class Position private constructor(val index: Int) {

    val row get() = ROWS[index / BOARD_SIZE]
    val column get() = COLUMNS[index % BOARD_SIZE]

    companion object {
        val values = List(BOARD_CELLS) { Position(it) }

        // Invoke -> Position()
        operator fun invoke(idx: Int): Position = values[idx]
    }
}

fun toBoardIndex(row: Int, column: Char): Int = (row-1) * BOARD_SIZE + column.toIntColumn()

private fun Char.toIntColumn(): Int = this - 'A'