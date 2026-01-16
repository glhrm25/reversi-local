package reversi.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import reversi.model.Name
import reversi.model.PlayerColor
import reversi.model.opponent

val SPACE_BETWEEN_ELEMENTS = 6.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditDialog(mode: EditMode, onCancel: ()->Unit, onAction: (String, PlayerColor, Boolean)->Unit) {
    var name by mutableStateOf("")
    var side by mutableStateOf(PlayerColor.BLACK)
    var isMultiplayer by mutableStateOf(true)
    /*Popup(
        properties = PopupProperties(focusable = true),
        alignment = Alignment.Center,
        onDismissRequest = onCancel,
        content = {
            Column(modifier = Modifier.padding(15.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(SPACE_BETWEEN_ELEMENTS)) {
                if (mode == EditMode.START) {
                    // Select game's state (multiplayer / singleplayer)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(SPACE_BETWEEN_ELEMENTS)) {
                        Checkbox(
                            checked = isMultiplayer,
                            onCheckedChange = { isMultiplayer = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color.Cyan,
                                uncheckedColor = Color.Gray
                            )
                        )
                        Text("Multiplayer", style = MaterialTheme.typography.titleLarge)
                    }
                    // Select user's pieces
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(SPACE_BETWEEN_ELEMENTS), modifier = Modifier.clickable{ side = side.opponent }) {
                        Text("Player: ", style = MaterialTheme.typography.titleLarge)
                        animatedPiece(side, modifier = Modifier.size(CELL_SIDE/2))
                    }
                }
                // Fill game's name
                TextField(
                    enabled = isMultiplayer,
                    value = name,
                    label = { Text("clash name") },
                    onValueChange = { name = it },
                    placeholder = { Text("Enter a name") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(SPACE_BETWEEN_ELEMENTS*2))
                Button(onClick = { onAction(name, side, isMultiplayer) },
                    colors = ButtonDefaults.buttonColors(Color.Blue),
                    enabled = !isMultiplayer || Name.isValid(name) ) { Text(mode.text) }
            }
        }
    )*/

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Name to ${mode.text}") },
        text = {
            Column(modifier = Modifier.padding(15.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(SPACE_BETWEEN_ELEMENTS)) {
                if (mode == EditMode.START) {
                    // Select game's state (multiplayer / singleplayer)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(SPACE_BETWEEN_ELEMENTS)) {
                        Checkbox(
                            checked = isMultiplayer,
                            onCheckedChange = { isMultiplayer = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color.Cyan,
                                uncheckedColor = Color.Gray
                            )
                        )
                        Text("Multiplayer", style = MaterialTheme.typography.titleLarge)
                    }
                    // Select user's pieces
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(SPACE_BETWEEN_ELEMENTS), modifier = Modifier.clickable{ side = side.opponent }) {
                        Text("Player: ", style = MaterialTheme.typography.titleLarge)
                        animatedPiece(side, modifier = Modifier.size(CELL_SIDE/2))
                    }
                }
                // Fill game's name
                TextField(
                    enabled = isMultiplayer,
                    value = name,
                    label = { Text("clash name") },
                    onValueChange = { name = it },
                    placeholder = { Text("Enter a name") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(SPACE_BETWEEN_ELEMENTS*2))
                Button(onClick = { onAction(name, side, isMultiplayer) },
                    colors = ButtonDefaults.buttonColors(Color.Blue),
                    enabled = !isMultiplayer || Name.isValid(name) ) { Text(mode.text) }
            }
        },
        confirmButton = {},
    )
}