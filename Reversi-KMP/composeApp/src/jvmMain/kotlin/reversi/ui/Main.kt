package reversi.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.*
import reversi_kmp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

val TOTAL_WIDTH = STATUS_WIDTH
val TOTAL_HEIGHT = (WIDTH + LINE_THICKNESS + STATUS_HEIGHT)*2 + GRID_SIDE

/**
 * TODO:
 * BUGS:
 * - Animações do outro player
 */

@Composable
fun FrameWindowScope.App(onExit: MutableState<()->Unit>) {
    val scope = rememberCoroutineScope()
    val vm = remember { AppViewModel(scope).also{
        val oldOnExit = onExit.value
        onExit.value = { it.finish(); oldOnExit() }
    } }

    MenuBar {
        Menu("Game") {
            Item("New", onClick = vm::new)
            Item("Join", onClick = vm::join)
            Item("Refresh", onClick = vm::refresh, enabled = vm.isMP && !vm.isYourTurn && !vm.autoRefreshSetting)
            Item("Leave Game", onClick = vm::leaveGame, enabled = vm.isRun)
            Item("Exit", onClick = { onExit.value() })
        }
        Menu("Play") {
            Item("Pass", onClick = vm::pass, enabled = vm.canPass)
        }
        Menu("Options") {
            CheckboxItem("Show Targets", checked = vm.targetsAssistanceSetting, enabled = vm.isRun, onCheckedChange = vm::targets)
            CheckboxItem("Auto-Refresh", checked = vm.autoRefreshSetting, enabled = vm.isMP, onCheckedChange = vm::changeAutoRefreshSetting)
        }
    }

    MaterialTheme {
        if (vm.isRun) Column {
            labeledGrid(vm.game, vm.animations, targetsAssistance = vm.showTargets, onClick = vm::play)
            StatusBar(vm.game.state, vm.you.playerColor, vm.isMP, vm.blackPiecesCounter, vm.whitePiecesCounter)
        }
        else
            Box(Modifier.width(GRID_SIDE).height(GRID_SIDE+STATUS_HEIGHT))
        vm.editMode?.let{ EditDialog(it, vm::cancelEdit, vm::doAction ) }
        vm.message?.let{ MessageInfo(it, vm::clearMessage) }
        if (vm.isWaiting) WaitingIndicator() // Auto-Refresh indicator
    }
}

fun main() = application {
    val onExit = remember { mutableStateOf<()->Unit>(::exitApplication) }
    Window(
        onCloseRequest = { onExit.value() },
        title = "ReversiKMP",
        icon = painterResource(Res.drawable.cross),
        state = WindowState(size= DpSize(TOTAL_WIDTH,TOTAL_HEIGHT)),
        resizable = true,
    ) {
        App(onExit)
    }
}
