package reversi.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
@Preview
fun MessageTest() = MessageInfo("Hello World!", {})

@Composable
fun MessageInfo(msg: String, onClose: ()->Unit ) = AlertDialog(
    onDismissRequest = onClose,
    title = { Text(msg) },
    confirmButton = { Button(onClick = onClose){ Text("Ok") } }
)

