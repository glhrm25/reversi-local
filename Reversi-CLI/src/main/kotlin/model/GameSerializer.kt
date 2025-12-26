package model

import storage.Serializer
object GameSerializer: Serializer<Game>{
    override fun serialize(data: Game): String =
        buildString {
            appendLine(data.owner)
            appendLine(StateSerializer.serialize(data.state))
            appendLine(data.board.entries.joinToString(" "){ (k, v) -> "${k.index}:$v" })
        }

    override fun deserialize(text: String): Game {
        val (l1, l2, l3) = text.split("\n")

        return Game(
            owner = l1.toColor(), // take out????
            state = StateSerializer.deserialize(l2),
            board = l3.split(' ').associate {
                val (pos, pl) = it.split(':')
                Position(pos.toInt()) to pl.toColor()
            },
        )
    }
}

/**
 *  GameState Serializer that transforms a gamestate object into a string.
 */
object StateSerializer: Serializer<GameState> {
    override fun serialize(data: GameState): String =
        when(data) {
            is Run -> buildString {
                if (data is RunPassed) append("RunPassed:${data.turn}")
                else append("Run:${data.turn}")
            }
        is Win -> "Win:${data.winner}"
        is Draw -> "Draw:"
    }

    override fun deserialize(text: String): GameState {
        val (type, content) = text.split(":")
        return when(type){
            "Run" -> {
                Run(Color.valueOf(content))
            }
            "RunPassed" -> {
                RunPassed(Color.valueOf(content))
            }
            "Win" -> Win(Color.valueOf(content))
            "Draw"-> Draw
            else -> error("Invalid game state $type")
        }
    }
}
