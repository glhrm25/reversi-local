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
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import reversi_kmp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import reversi.model.*

val CELL_SIDE = 60.dp
const val FLIP_FRAME_DELAY = 30
val BlackToWhiteFrames = listOf(
    Res.drawable.Black,
    Res.drawable.BlackToWhite1,
    Res.drawable.BlackToWhite2,
    Res.drawable.BlackToWhite3,
    Res.drawable.Half,
    Res.drawable.WhiteToBlack3,
    Res.drawable.WhiteToBlack2,
    Res.drawable.WhiteToBlack1,
    Res.drawable.White
)
val WhiteToBlackFrames = BlackToWhiteFrames.reversed()
val animationFramesSize = BlackToWhiteFrames.size // Size of the animation's sprites array (Arrays above)

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
    animation: Boolean = false,
    modifier: Modifier = Modifier.background(Color.Green).size(CELL_SIDE),
    onClickInEmpty: ()->Unit = { }
) {
    if (showValidMoves)
        Box(modifier.padding(CELL_SIDE / 8).clickable(onClick = onClickInEmpty).background(Color.Yellow, CircleShape))
    else if (player == null)
        Box(modifier.clickable(onClick = onClickInEmpty))
    else {
        if (!animation) {
            val resource = when (player) {
                PlayerColor.BLACK -> Res.drawable.Black
                PlayerColor.WHITE -> Res.drawable.White
            }
            Image(painterResource(resource), null, modifier = modifier)
        }
        else
            animatedPiece(player, modifier)
    }
}

@Composable
fun animatedPiece(player: PlayerColor = PlayerColor.BLACK, modifier: Modifier = Modifier.size(CELL_SIDE)) {
    val animationFrames = if (player == PlayerColor.BLACK) WhiteToBlackFrames else BlackToWhiteFrames
    val resource = animationFrames[0]
    var sprite by remember { mutableStateOf(resource) }

    Image(painterResource(sprite), null, modifier = modifier)

    LaunchedEffect(player) {
        var frameIndex = 0
        while (frameIndex < animationFramesSize) {
            delay(FLIP_FRAME_DELAY.toLong())
            sprite = animationFrames[frameIndex]
            frameIndex++
        }
    }
}