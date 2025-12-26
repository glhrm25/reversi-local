import model.*
import storage.TextFileStorage
import kotlin.test.*

class StorageTests {

    private fun newStorage(): TextFileStorage<Name, Game> =
        TextFileStorage("StorageTests", GameSerializer)

    @Test
    fun `create and read game`() {
        val storage = newStorage()
        val name = Name("TestGame${System.currentTimeMillis()}")
        val game = Game()

        storage.create(name, game)
        val readGame = storage.read(name)
        assertNotNull(readGame)
        assertEquals(game, readGame)
        storage.delete(name)
    }


    @Test
    fun `create existing game should fail`() {
        val storage = newStorage()
        val name = Name("TestGame${System.currentTimeMillis()}")
        val game = Game()

        storage.create(name, game)
        assertFailsWith<IllegalStateException> {
            storage.create(name, Game())
        }
        storage.delete(name)
    }

    @Test
    fun `update existing game`() {
        val storage = newStorage()
        val name = Name("TestGame${System.currentTimeMillis()}")
        val game = Game()

        storage.create(name, game)
        val move = game.validMoves(Color.BLACK).first()
        val updatedGame = game.play(move)

        storage.update(name, updatedGame)
        assertEquals(updatedGame, storage.read(name))

        storage.delete(name)
    }

    @Test
    fun `update non existing game should fail`() {
        val storage = newStorage()
        val name = Name("TestGame${System.currentTimeMillis()}")
        val game = Game()

        assertFailsWith<IllegalStateException> {
            storage.update(name, game)
        }
    }

    @Test
    fun `delete existing game`() {
        val storage = newStorage()
        val name = Name("TestGame${System.currentTimeMillis()}")
        val game = Game()

        storage.create(name, game)
        storage.delete(name)
        assertNull(storage.read(name))
    }

    @Test
    fun `delete non existing game should fail`() {
        val storage = newStorage()
        val name = Name("TestGame${System.currentTimeMillis()}")
        assertFailsWith<IllegalStateException> {
            storage.delete(name)
        }
    }
}
