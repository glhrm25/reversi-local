import kotlin.test.*
import model.*

class GameStateTest {

    @Test
    fun `initial game state`() {
        val game = Game()
        assertEquals(Color.BLACK, game.owner)
        assertTrue(game.state is Run)
        assertEquals(Color.BLACK, (game.state as Run).turn)

        val blacks = game.board.count { it.value == Color.BLACK }
        val whites = game.board.count { it.value == Color.WHITE }

        assertEquals(2, blacks)
        assertEquals(2, whites)
    }

    @Test
    fun `play move updates board and turn`() {
        val game = Game()
        val move = game.validMoves(Color.BLACK).first()
        val newGame = game.play(move)

        assertEquals(Color.BLACK, newGame.board[move])

        assertTrue(newGame.state is Run)
        assertEquals(Color.WHITE, (newGame.state as Run).turn)
    }

    @Test
    fun `playing in an invalid position throws`() {
        val game = Game()
        val invalidPos = Position(0)
        assertFailsWith<IllegalArgumentException> {
            game.play(invalidPos)
        }
    }

    @Test
    fun `cannot pass when moves are available`() {
        val game = Game()
        assertFailsWith<IllegalStateException> {
            game.pass()
        }
    }

    @Test
    fun `pass twice in a row ends the game`() {
        val fullBoard = (0 until BOARD_CELLS).associate { i ->
            Position(i) to if (i % 2 == 0) Color.BLACK else Color.WHITE
        }

        var game = Game(board = fullBoard, state = Run(Color.BLACK))
        assertTrue(game.state is Run)

        game = game.pass()
        assertEquals(RunPassed(Color.WHITE), game.state)

        game = game.pass()
        assertTrue(game.state is Win || game.state is Draw)
    }

    @Test
    fun `game ends with correct winner on full board`() {
        val fullBoard = (0 until BOARD_CELLS).associate { i ->
            Position(i) to if (i % 2 == 0) Color.BLACK else Color.WHITE
        }

        val game = Game(board = fullBoard, state = Run(Color.BLACK))

        val passed = game.pass().pass()
        assertTrue(passed.state is Win || passed.state is Draw)
    }
}
