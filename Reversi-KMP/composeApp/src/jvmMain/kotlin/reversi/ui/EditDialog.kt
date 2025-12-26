package reversi.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import reversi.model.Name
import reversi.model.PlayerColor
import reversi.model.otherColor

@Composable
fun EditDialog(mode: EditMode, onCancel: ()->Unit, onAction: (String, PlayerColor, Boolean)->Unit) {
    var name by mutableStateOf("")
    var side by mutableStateOf(PlayerColor.BLACK)
    var isMultiplayer by mutableStateOf(true)
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Name for ${mode.text}") },
        text = {
            Column(modifier = Modifier.padding(15.dp)) {
                if (mode == EditMode.START) {
                    // Select game's state (multiplayer / singleplayer)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = isMultiplayer, onClick = { isMultiplayer = !isMultiplayer })
                        Text("Multiplayer")
                    }
                    // Select user's pieces
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = side == PlayerColor.BLACK, onClick = { side = side.otherColor })
                        Text("Black Pieces")
                    }
                }
                // Fill game's name
                if (isMultiplayer) {
                    OutlinedTextField(
                        value = name,
                        label = { Text("clash name") },
                        onValueChange = { if (Name.isValid(it)) name = it }
                    )
                }
            }
        },
        dismissButton = { Button(onClick = onCancel) { Text("Cancel") } },
        confirmButton = { Button(onClick = { onAction(name, side, isMultiplayer) }, enabled = !isMultiplayer || Name.isValid(name)) { Text(mode.text) } }
    )
}