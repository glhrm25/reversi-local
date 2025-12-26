package user_interface

data class LineCommand(val command: String, val args: List<String>)

tailrec fun readCommand(): LineCommand {
    print("> ")
    val line = readln().trim().split(' ').filter { it.isNotBlank() }

    return if (line.isEmpty()) readCommand()
    else LineCommand(line.first().uppercase(), line.drop(1))
}