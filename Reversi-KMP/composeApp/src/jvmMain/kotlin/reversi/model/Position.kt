package reversi.model

val COLUMNS = ('A' until 'A' + BOARD_SIZE).toList()
val ROWS = (1 .. BOARD_SIZE).toList()


class Position private constructor(val index: Int) {

    val row get() = ROWS[index / BOARD_SIZE]
    val column get() = COLUMNS[index % BOARD_SIZE]

    companion object {
        val values = List(BOARD_CELLS) { Position(it) }
        val INVALID = Position(-1)
        // Invoke -> Position()
        private fun isValidPosition(index: Int) : Position =
            values.firstOrNull { index == it.index } ?: INVALID

        operator fun invoke(idx: Int): Position = isValidPosition(idx)
    }

    override fun toString() = "$row:$column"
}

fun toBoardIndex(row: Int, column: Char): Int = (row-1) * BOARD_SIZE + column.toIntColumn()

private fun Char.toIntColumn(): Int = this - 'A'

operator fun Position.plus(dir: Direction) = Position((row-1+dir.difRow)*BOARD_SIZE + column.toIntColumn()+dir.difCol)