package reversi.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import storage.Storage

typealias GameStorage = Storage<Name, Game>

open class Clash (val gs: GameStorage) {
    private fun notStarted(): Nothing = error("Clash not started")
    open fun play(pos: Position): Clash {
        notStarted()
    }
    open fun pass(): Clash {
        notStarted()
    }
    open fun targets(t: Boolean): Clash {
        notStarted()
    }
    open fun refresh(): Clash {
        notStarted()
    }
    open suspend fun autoRefresh(): Clash {
        notStarted()
    }

    fun new(name: Name?, owner: PlayerColor): Clash {
        val newGame = Game(owner = owner)
        val side = Player(owner)
        return name?.let { it -> ClashRunMP(gs, it, side, newGame.also{gs.create(name, it)}) } ?: ClashRunLocal(gs, newGame, side)
    }

    fun join(name: Name): Clash {
        val g = gs.read(name)
        checkNotNull(g){"Game $name does not exist"}
        return ClashRunMP(gs, name, Player(g.owner.otherColor), g)
    }
    open fun finish(){ }
}

open class ClashRun(gs: GameStorage, open val game: Game, open val side: Player): Clash(gs){
    override fun finish() { deleteIfOwner() }
}

class ClashRunMP(
    gs: GameStorage,
    val name: Name,
    override val side: Player,
    override val game: Game,
): ClashRun(gs, game, side) {
    override fun play(pos: Position): Clash =
        copy(game = game.play(pos)).also {
            check(side.playerColor == (game.state as Run).turn) { "Not your turn" }
            gs.update(name,it.game)
        }

    override fun pass(): Clash =
        this.copy(game = game.pass()).also {
            gs.update(name, it.game)
        }

    override fun refresh() =
        copy( game = gs.read(name)?.also { check( it != game) { "No changes" } }
            ?: throw GameNotFoundException()
        )

    override suspend fun autoRefresh() =
        copy( game = gs.slowRead(name)?.also { check( it != game) { "No changes" } }
            ?: throw GameNotFoundException()
        )

    override fun targets(t: Boolean) =
        this.copy(side = side.copy(toggleTargets = t))

    private fun copy(
        gs: GameStorage = this.gs,
        name: Name = this.name,
        side: Player = this.side,
        game: Game = this.game)
    = ClashRunMP(gs, name, side, game)
}

class ClashRunLocal(
    gs: GameStorage,
    override val game: Game,
    override val side: Player,
): ClashRun(gs, game, side) {
    override fun play(pos: Position): Clash =
        copy(game = game.play(pos))

    override fun pass(): Clash =
        this.copy(game = game.pass())

    override fun targets(t: Boolean) =
        this.copy(side = side.copy(toggleTargets = t))

    private fun copy(gs: GameStorage = this.gs,
                     game: Game = this.game,
                     side: Player = this.side)
            = ClashRunLocal(gs, game, side)
}

fun ClashRun.newAvailable() =
    side.playerColor == when(val state = game.state) {
        is Run -> state.turn
        else -> game.owner.otherColor
    }

fun Clash.deleteIfOwner() {
    if (this is ClashRunMP && side.playerColor == this.game.owner) gs.delete(name)
}

class GameNotFoundException: IllegalStateException("Game not found")

private suspend fun GameStorage.slowRead(name: Name): Game?{
    return withContext(Dispatchers.IO) { // Dispacthers.IO -> Arranja uma cortina noutra thread
        Thread.sleep(5000)
        read(name)
    }
}