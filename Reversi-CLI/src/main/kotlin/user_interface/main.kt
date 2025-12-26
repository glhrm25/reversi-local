package user_interface

import model.*
import storage.TextFileStorage

private const val gamesDirectory = "games"

fun main() {

    val gameStorage = TextFileStorage<Name, Game>(gamesDirectory, GameSerializer)
    var clash = Clash(gameStorage)

    val cmds: Map<String, Command> = getCommands()
    while(true) {
        val (name, args) = readCommand()

        val command = cmds[name]
        if (command == null) println("Invalid command $name")

        else try {
            clash = command.execute(clash, args)
            if (command.isTerminate) {
                println("Game ended.")
                return
            }
            if (command.toShow) clash.show()
        }
        catch (ex: IllegalArgumentException) {
            println(ex.message)
            println("Use $name ${command.syntaxArgs}")
        } catch (ex: IllegalStateException) {
            println(ex.message)
        }
    }
}