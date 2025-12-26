import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import model.*

class CommandsTests {

    private fun fullBoard(): Map<Position, Color> {
        return (0 until BOARD_CELLS).associate { i ->
            Position(i) to if (i % 2 == 0) Color.BLACK else Color.WHITE
        }
    }

    @Test
    fun `PASS command test with full board`() {
        var game = Game(
            owner = Color.BLACK,
            board = fullBoard().filterKeys { it != Position(BOARD_CELLS - 1) },
            state = Run(Color.BLACK)
        )
        assertEquals(Run(turn = Color.BLACK), game.state)

        game = game.pass()
        assertEquals(RunPassed(Color.WHITE), game.state)

        game = game.play(Position(BOARD_CELLS - 1))
        assertEquals(Win(Color.WHITE), game.state)

        assertFailsWith<IllegalStateException> {
            game.pass()
        }
    }

    @Test
    fun `PASS command test with initial board`() {
        var game = Game(owner = Color.BLACK)
        assertEquals(Run(Color.BLACK), game.state)
        assertFailsWith<IllegalStateException> {game.pass()}
        assertEquals(Run(Color.BLACK), game.state)

        game = game.play(Position(toBoardIndex(4, 'C')))
        assertEquals(Run(Color.WHITE), game.state)
        assertFailsWith<IllegalStateException> {game.pass()}
        assertEquals(Run(Color.WHITE), game.state)
    }
}
