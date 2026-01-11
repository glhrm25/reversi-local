package reversi.ui

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import storage.*
import storage.mongo.*
import reversi.model.*

enum class EditMode(val text: String) {
    START("Start"), JOIN("Join")
}

class AppViewModel(val scope: CoroutineScope) {
    //private val storage = TextFileStorage<Name,_>("games", GameSerializer)
    private val driver = MongoDriver("ReversiDB")
    private val storage = MongoStorage<Name,_>("games",driver,GameSerializer)
    /**
     * Clash state (Clash or ClashRun)
     */
    var clash by mutableStateOf(Clash(storage))
        private set
    val isRun get() = clash is ClashRunMP || clash is ClashRunLocal
    val isMP get() = clash is ClashRunMP
    val isYourTurn get() = isRun && (game.state is Run) && ((game.state as Run).turn == you.playerColor)

    fun play(pos: Position) {
        if (game.state is Run && !isWaiting){
            animations = game.turningPieces((game.state as Run).turn, pos).map{ (p, _) -> p}.toSet()
            oper { play(pos) }
            waitForOther()
        }
    }
    fun pass() = oper { pass() }

    fun refresh() = oper { refresh() }
    fun changeAutoRefreshSetting(value: Boolean) {
        autoRefreshSetting = value
        waitForOther()
    }

    val targetsAssistanceSetting get() = if (isRun) you.toggleTargets else false
    fun targets(value: Boolean) = oper { targets(value) }

    /**
     * Starts a new game or joins an existing one, based on the users input.
     */
    fun doAction(name: String, side: PlayerColor, isMultiplayer: Boolean) {
        cancelWaiting()
        val nm = if(!isMultiplayer) null else Name(name)
        oper {
            if (editMode == EditMode.START) new(nm, side).also{ finish() }
            else nm?.let { join(nm) } ?: this
        }
        editMode = null
        waitForOther()
    }

    /**
     * Indicates if the edit dialog is being shown
     */
    var editMode by mutableStateOf<EditMode?>(null)
        private set
    fun new() { editMode = EditMode.START }
    fun join() { editMode = EditMode.JOIN }
    fun cancelEdit() { editMode = null }

    fun finish(){
        cancelWaiting()
        clash.finish()
    }

    /**
     * Properties to access ClashRun info
     */
    val game get() = (clash as ClashRun).game
    val you get() = (clash as ClashRun).side
    val name get() = (clash as ClashRunMP).name
    val newAvailable get() = (clash as? ClashRun)?.newAvailable() ?: false

    /**
    * Set that contains the flipping pieces after a move to display the animation
    */
    var animations by mutableStateOf<Set<Position>>(emptySet())

    val whitePiecesCounter get () = game.board.count { (_, col) -> col == PlayerColor.WHITE }
    val blackPiecesCounter get () = game.board.count { (_, col) -> col == PlayerColor.BLACK }

    /**
     * Performs an operation on the clash, catching exceptions to set the message property
     */
    private fun oper(op: Clash.()-> Clash) {
        try {
            clash = clash.op()
        } catch (ex: Exception) {
            println(ex)
            if (ex is IllegalStateException || ex is IllegalArgumentException) {
                message = ex.message
                if (ex is GameNotFoundException)
                    clash = Clash(storage)
            }
            else throw ex
        }
    }

    /**
     * Message
     */
    var message: String? by mutableStateOf(null)
        private set
    fun clearMessage() { message=null }

    /**
     * Auto-refresh job
     */
    var autoRefreshSetting by mutableStateOf(false)
        private set
    private var job by mutableStateOf<Job?>(null)
    val isWaiting get() = job != null

    private fun cancelWaiting() {
        job?.cancel()
        job = null
    }
    private fun waitForOther() {
        if (clash !is ClashRun || newAvailable || !autoRefreshSetting) return
        job = scope.launch {
            do {
                delay(3000)
                try {
                    clash = clash.autoRefresh()
                } catch (ex: Exception) {
                    println(ex)
                    if (ex is IllegalStateException) {
                        message = ex.message
                        if (ex is GameNotFoundException) {
                            clash = Clash(storage)
                            break
                        }
                    } else throw ex
                }
            } while (!newAvailable && autoRefreshSetting)
            job = null
        }
    }
}