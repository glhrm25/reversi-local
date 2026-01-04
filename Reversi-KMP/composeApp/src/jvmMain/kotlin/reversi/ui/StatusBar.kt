package reversi.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import reversi.model.*


val STATUS_HEIGHT = 40.dp
val STATUS_WIDTH = GRID_SIDE + WIDTH * 2 + LINE_THICKNESS

@Composable
@Preview
fun StatusBarTest() {
    StatusBar(Run(PlayerColor.BLACK), PlayerColor.WHITE, true, 10, 10)
}

@Composable
fun StatusBar(state: GameState, you: PlayerColor, isMp: Boolean, amountBlackPieces: Int, amountWhitePieces: Int) =
    Row(
        Modifier
            .height(STATUS_HEIGHT)
            .background(Color.LightGray)
            .width(STATUS_WIDTH),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (isMp) {
            LabeledCell("You:", you)
            Spacer(Modifier.width(STATUS_HEIGHT * 3))
        }
        val (txt, player) = when (state) {
            is Run -> "Turn:" to state.turn
            is Win -> "Winner:" to state.winner
            is Draw -> "Draw" to null
        }
        Text(txt, style = MaterialTheme.typography.bodyLarge)
        if (player != null) animatedPiece(player, modifier = Modifier.background(Color.LightGray).size(CELL_SIDE / 2))
        Spacer(Modifier.width(STATUS_HEIGHT * 3))
        LabeledCell("$amountBlackPieces x", PlayerColor.BLACK)
        Spacer(Modifier.width(STATUS_HEIGHT / 5))
        LabeledCell("$amountWhitePieces x", PlayerColor.WHITE)
    }


@Composable
fun LabeledCell(txt: String, player: PlayerColor?) {
    Text(txt, style = MaterialTheme.typography.bodyLarge)
    Cell(player, modifier = Modifier.background(Color.LightGray).padding(4.dp))
}