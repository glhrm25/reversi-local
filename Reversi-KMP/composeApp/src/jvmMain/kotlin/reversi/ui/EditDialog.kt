package reversi.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import reversi.model.Name
import reversi.model.PlayerColor
import reversi.model.opponent

@Composable
fun EditDialog(mode: EditMode, onCancel: ()->Unit, onAction: (String, PlayerColor, Boolean)->Unit) {
    var name by mutableStateOf("")
    var side by mutableStateOf(PlayerColor.BLACK)
    var isMultiplayer by mutableStateOf(true)
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Name to ${mode.text}") },
        text = {
            Column(modifier = Modifier.padding(15.dp)) {
                if (mode == EditMode.START) {
                    // Select game's state (multiplayer / singleplayer)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = isMultiplayer, onCheckedChange = { isMultiplayer = !isMultiplayer })
                        Spacer(Modifier.width(10.dp))
                        Text("Multiplayer", style = MaterialTheme.typography.bodyLarge)
                    }
                    // Select user's pieces
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Player: ", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.width(10.dp))
                        animatedPiece(side, modifier = Modifier.size(CELL_SIDE/2).clickable{ side = side.opponent })
                    }
                }
                // Fill game's name
                if (isMultiplayer) {
                    OutlinedTextField(
                        value = name,
                        label = { Text("clash name") },
                        onValueChange = { name = it },
                        placeholder = { Text("Enter a name") },
                        isError = !Name.isValid(name)
                    )
                }
            }
        },
        dismissButton = { Button(onClick = onCancel) { Text("Cancel") } },
        confirmButton = { Button(onClick = { onAction(name, side, isMultiplayer) }, enabled = !isMultiplayer || Name.isValid(name)) { Text(mode.text) } }
    )
}