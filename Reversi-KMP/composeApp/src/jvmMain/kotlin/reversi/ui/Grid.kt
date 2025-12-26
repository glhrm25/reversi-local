package reversi.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import reversi.model.*
import reversi.model.Position

@Composable
@Preview
fun GridTest() {
    Grid(generateBoard(), onClick = { } )
}

val LINE_THICKNESS = 4.dp
val WIDTH = 16.dp
val GRID_SIDE = CELL_SIDE * BOARD_SIZE + LINE_THICKNESS * (BOARD_SIZE - 1)
@Composable
fun Grid(
    board: Board,
    validMoves: List<Position> = emptyList(),
    targetsAssistance: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: (Position)->Unit
) {
    Column(
        modifier.height(GRID_SIDE).background(Color.Black),
        verticalArrangement = Arrangement.spacedBy(LINE_THICKNESS)
    ) {
        repeat(BOARD_SIZE) { row ->
            Row(
                modifier.width(GRID_SIDE),
                horizontalArrangement = Arrangement.spacedBy(LINE_THICKNESS)
            ) {
                repeat(BOARD_SIZE) { col ->
                    val pos = Position(row*BOARD_SIZE+col)
                    Cell(board[pos], showValidMoves = pos in validMoves && targetsAssistance) { onClick(pos) }
                }
            }
        }
    }
}

@Composable
fun labeledGrid(
    board: Board,
    validMoves: List<Position> = emptyList(),
    targetsAssistance: Boolean = false,
    modifier: Modifier = Modifier.background(Color.DarkGray),
    onClick: (Position)->Unit
) {
    Column(modifier) {
        Row(
            modifier = Modifier
                .height(WIDTH)
                .width(WIDTH + LINE_THICKNESS + GRID_SIDE),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(WIDTH + LINE_THICKNESS))

            Row(
                modifier = Modifier.width(GRID_SIDE),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(BOARD_SIZE) { i ->
                    Box(
                        modifier = Modifier
                            .width(CELL_SIDE)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(('A' + i).toString(), color = Color.White)
                    }
                }
            }
        }

        Spacer(Modifier.height(LINE_THICKNESS))

        Row {
            Column(
                verticalArrangement = Arrangement.spacedBy(LINE_THICKNESS),
                modifier = Modifier.width(WIDTH)
            ) {
                repeat(BOARD_SIZE) { i ->
                    Box(
                        modifier = Modifier
                            .height(CELL_SIDE)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = (i + 1).toString(), color = Color.White)
                    }
                }
            }

            Spacer(Modifier.width(LINE_THICKNESS))
            Grid(board, validMoves, targetsAssistance, modifier, onClick)
        }
    }
}