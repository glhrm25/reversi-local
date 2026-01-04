package reversi.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.*
import androidx.compose.ui.unit.dp
import reversi_kmp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

/**
 * TO-DO:
 * - CHANGE LISTS TO SETS
 * - FUNCTION IN MOVES.KT COULD BE EXTENSIONS OF BOARD INSTEAD OF GAME
 *      - ALSO, MOVE THEM TO ANOTHER FILE AND DELETE MOVES.KT
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
            Item("Refresh", onClick = vm::refresh, enabled = vm.isMP && !vm.isYourTurn)
            Item("Exit", onClick = { onExit.value() })
        }
        Menu("Play") {
            Item("Pass", onClick = vm::pass, enabled = vm.isRun)
        }
        Menu("Options") {
            CheckboxItem("Show Targets", checked = vm.currentTargetsAssistanceOption, enabled = vm.isRun, onCheckedChange = vm::setTargetsAssistanceSetting)
            CheckboxItem("Auto-Refresh", checked = vm.autoRefreshOption, enabled = vm.isMP, onCheckedChange = vm::setAutoRefreshSetting)
        }
    }

    MaterialTheme {
        if (vm.isRun) Column {
            labeledGrid(vm.game, vm.animations, targetsAssistance = vm.you.toggleTargets, onClick = vm::play)
            StatusBar(vm.game.state, vm.you.playerColor, vm.isMP, vm.blackPiecesCounter, vm.whitePiecesCounter)
        }
        else
            Box(Modifier.width(GRID_SIDE).height(GRID_SIDE+STATUS_HEIGHT))
        vm.editMode?.let{ EditDialog(it, vm::cancelEdit, vm::doAction ) }
        vm.message?.let{ MessageInfo(it, vm::clearMessage) }
        if (vm.isWaiting) WaitingIndicator()
    }
}

val TOTAL_WIDTH = 544.dp
//val TOTAL_WIDTH = STATUS_WIDTH
val TOTAL_HEIGHT = 630.dp
//val TOTAL_HEIGHT = WIDTH + LINE_THICKNESS * 2 + GRID_SIDE + STATUS_HEIGHT

fun main() = application {
    val onExit = remember { mutableStateOf<()->Unit>(::exitApplication) }
    Window(
        onCloseRequest = { onExit.value() },
        title = "ReversiKMP",
        icon = painterResource(Res.drawable.cross),
        state = WindowState(size= DpSize(TOTAL_WIDTH,TOTAL_HEIGHT)),
        resizable = false,
    ) {
        App(onExit)
    }
}
