package model

import storage.Storage
import user_interface.show
import user_interface.showHeader
import user_interface.showTargets

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
    open fun showPlayersTargetsConfig(){
        notStarted()
    }
    open fun refresh(): Clash {
        notStarted()
    }
    open fun show(): Clash {
        notStarted()
    }

    fun new(name: Name?, owner: Color): Clash =
        ClashRun(
            gs = gs,
            side = Player(owner),
            game = name?.let { NamedGame(it, owner).also {g -> gs.create(g.name, g)} } ?: Game(owner)
        )

    fun join(name: Name): Clash {
        val g = gs.read(name)
        checkNotNull(g){"Game $name does not exist"}
        return ClashRun(gs, Player(g.owner.otherColor), NamedGame(name, g.owner, g.board, g.state))
    }
}

class ClashRun(
    gs: GameStorage,
    val side: Player,
    val game: Game,
): Clash(gs) {
    override fun play(pos: Position): Clash =
        copy(game = game.play(pos)).also {
            if (it.game is NamedGame)
                check(side.color == (it.game.state as Run).turn) { "Not your turn" }
            updateGameFile(gs, it.game)
        }

    override fun pass(): Clash =
        this.copy(game = game.pass()).also {
            updateGameFile(gs, game)
        }

    override fun refresh() =
        if (game is NamedGame){
            val g = gs.read(game.name) ?: error("Game not found")
            copy(game = NamedGame(game.name, g.owner, g.board, g.state).also { check(it != game) { "No changes" } })
        }
        else
            this.also{ error("Command unavailable on a local game") }

    override fun targets(t: Boolean) =
        this.copy(side = side.copy(toggleTargets = t)).also{ it.show() }

    override fun showPlayersTargetsConfig() = showTargets(side)

    override fun show(): Clash =
        this.also {
            this.showHeader()
            game.show(side.toggleTargets)
        }

    private fun copy(
        gs: GameStorage = this.gs,
        side: Player = this.side,
        game: Game = this.game)
    = ClashRun(gs, side, game)
}

fun Clash.deleteIfOwner() {
    if (this is ClashRun && game is NamedGame && side.color == this.game.owner) gs.delete(game.name)
}

fun updateGameFile(gs: GameStorage, game: Game){
    if (game is NamedGame) gs.update(game.name, game)
}