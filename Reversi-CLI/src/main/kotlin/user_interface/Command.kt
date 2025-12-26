package user_interface
import model.*

class Command (
    val syntaxArgs: String = "",
    val isTerminate: Boolean = false,
    val toShow : Boolean = true,
    val execute: Clash.(args: List<String>) -> Clash = {this},
)

private val new = Command("<FirstTurn> <Name>"){ args ->
    require(args.isNotEmpty()){"Missing arguments"}
    val argSymbol = args.first()
    require(argSymbol.length == 1) {"Invalid argument $argSymbol"} // If argument is not a character

    val symbol = argSymbol.first()
    require(symbol == BLACK_SYMBOL || symbol == WHITE_SYMBOL){"Invalid symbol $argSymbol"} // If symbol is not a valid symbol

    val argName = args.drop(1).firstOrNull()

    new(name = argName?.let { Name(it) }, owner = symbol.color()).also { deleteIfOwner() }
}

private val play = Command("<position>") { args ->
    val arg = requireNotNull(args.firstOrNull()) {"Missing position"}
   // val pos = requireNotNull(arg.toCellOrNull()) {"Invalid position $arg."}
    require(arg.length == 2) { "Invalid argument $arg." }

    play(arg.toBoardPosition())
}

private val targets = Command("<ON/OFF>", toShow = false){ args ->
    if (args.isEmpty()) this.also{ showPlayersTargetsConfig() }
    else {
        val arg = args.first().uppercase()
        require((arg == "ON") || (arg == "OFF")){"Invalid argument"}
        targets(arg == "ON")
    }
}

private val join = Command("<Name>"){args ->
    require(args.isNotEmpty()){"Missing game's name"}
    join(Name(args.first())).also { deleteIfOwner() }
}

fun getCommands() = mapOf(
    "EXIT" to Command(isTerminate = true){_ -> this.also { deleteIfOwner() }},
    "NEW" to new,
    "PLAY" to play,
    "SHOW" to Command(toShow = false) { _ -> this.also{ show() } },
    "PASS" to Command{_ -> pass() },
    "TARGETS" to targets,
    "JOIN" to join,
    "REFRESH" to Command{_ -> refresh() },
)