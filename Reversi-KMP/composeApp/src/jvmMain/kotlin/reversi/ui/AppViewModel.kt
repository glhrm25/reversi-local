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
            oper { play(pos) }
            waitForOther()
        }
    }
    fun pass(){ oper { pass() } }

    var autoRefreshOption by mutableStateOf(false)
        private set
    fun refresh() {
        //oper { refresh() }
        try {
            clash = clash.refresh()
        } catch (ex: Exception) {
            if (ex is IllegalStateException) {
                message = ex.message
                if (ex is GameNotFoundException) {
                    clash = Clash(storage)
                }
            } else throw ex
        }
    }
    fun setAutoRefreshSetting(value: Boolean) { autoRefreshOption = value }

    val currentTargetsAssistanceOption get() = if (isRun) you.toggleTargets else false
    fun setTargetsAssistanceSetting(value: Boolean) = oper { targets(value) }

    fun doAction(name: String, side: PlayerColor, isMultiplayer: Boolean) {
        cancelWaiting()
        val nm = if(!isMultiplayer) null else Name(name)
        oper {
            if (editMode == EditMode.START) new(nm, side)
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
    val validMoves get() = if (game.state is Run) game.validMoves((game.state as Run).turn) else emptyList()
    val whitePiecesCounter get () = game.board.count { (_, col) -> col == PlayerColor.WHITE }
    val blackPiecesCounter get () = game.board.count { (_, col) -> col == PlayerColor.WHITE }

    /**
     * Performs an operation on the clash, catching exceptions to set the message property
     */
    private fun oper(op: Clash.()-> Clash) {
        try {
            clash = clash.op()
        } catch (ex: Exception) {
            if (ex is IllegalStateException || ex is IllegalArgumentException)
                message = ex.message
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
    private var job by mutableStateOf<Job?>(null)
    val isWaiting get() = job != null

    private fun cancelWaiting() {
        job?.cancel()
        job = null
    }
    private fun waitForOther() {
        if (clash !is ClashRun || newAvailable || !autoRefreshOption) return
        job = scope.launch {
            do {
                delay(3000)
                try {
                    clash = clash.autoRefresh()
                } catch (ex: Exception) {
                    if (ex is IllegalStateException) {
                        message = ex.message
                        if (ex is GameNotFoundException) {
                            clash = Clash(storage)
                            break
                        }
                    } else throw ex
                }
            } while (!newAvailable && autoRefreshOption)
            job = null
        }
    }
}