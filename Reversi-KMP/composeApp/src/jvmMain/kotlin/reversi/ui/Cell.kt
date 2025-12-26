package reversi.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import reversi_kmp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import reversi.model.PlayerColor

val CELL_SIDE = 60.dp

@Composable
@Preview
fun CellTest() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Cell(PlayerColor.BLACK)
        Cell(PlayerColor.WHITE, modifier = Modifier.size(100.dp))
    }
}

@Composable
fun Cell(
    player: PlayerColor?,
    showValidMoves: Boolean = false,
    modifier: Modifier = Modifier.background(Color.Green).size(CELL_SIDE),
    onClickInEmpty: ()->Unit = { }
) {
    if (showValidMoves)
        Box(modifier.padding(CELL_SIDE / 8).clickable(onClick = onClickInEmpty).background(Color.Yellow, CircleShape))
    else if (player==null)
        Box(modifier.clickable(onClick = onClickInEmpty))
    else {
        val resource = when (player) {
            PlayerColor.BLACK -> Res.drawable.Black
            PlayerColor.WHITE -> Res.drawable.White
        }
        Image(painterResource(resource), null, modifier = modifier)
    }
}