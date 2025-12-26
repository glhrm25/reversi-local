import model.*
import kotlin.test.*

class GameSerializerTests {
    // Helper function to play a list of moves
    private fun playMoves(vararg moves: Int): Game {
        var game = Game(owner = Color.BLACK)
        moves.forEach { pos -> game = game.play(Position(pos)) }
        return game
    }

    @Test
    fun serializeRunGame() {
        val game = playMoves(toBoardIndex(4, 'C'), toBoardIndex(3, 'C'))
        val s = GameSerializer.serialize(game)
        println(s)
        assertEquals(game, GameSerializer.deserialize(s))
    }

    @Test
    fun serializeWinGame() {
        val game = Game(state = Win(Color.BLACK))
        val s = GameSerializer.serialize(game)
        assertEquals(game, GameSerializer.deserialize(s))
    }

    @Test
    fun serializeDrawGame() {
        val game = Game(state = Draw)
        val s = GameSerializer.serialize(game)
        assertEquals(game, GameSerializer.deserialize(s))
    }
}
