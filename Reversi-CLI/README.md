# 33d-2526i-grupo07

52559 - Isadora Mendes 

52599 - Duarte Rodrigues

52673 - Guilherme Santos

_______________________________________________________________

# Reversi Game
This project implements a simple version of the Reversi (Othello) game in Kotlin, playable through a command-line interface.
The game supports:
 - Playing local games or games via persistent file storage.
 - Playing moves, passing turns, viewing the board, and toggling move hints (targets On).
 - Persistent storage of games using a generic Storage interface and a text-based implementation TextFileStorage. 
 - Serialization and deserialization of game states.

# Project Structure:

    src/
    ├── model/                  # Core game logic
    │   ├── Clash.kt            # Main game controller
    │   ├── Game.kt             # Game representation and rules
    │   ├── Moves.kt            # Valid move and capture calculation
    │   ├── GameSerializer.kt   # Serialization and deserialization logic
    │   ├── Color.kt            # Color definitions and utilities used on Player
    │   ├── Name.kt             # Game name validation
    │   ├── Player.kt           # Player state
    │   └── Position.kt         # Board position representation
    │
    ├── storage/                # Generic persistence layer
    │   ├── Storage.kt          # CRUD interface for persistence
    │   └── TextFileStorage.kt  # Text file-based implementation
    │
    └── user_interface/         # Command-line interface (CLI)
        ├── main.kt             # Entry point (main function)
        ├── Command.kt          # Command parsing and execution
        ├── Input.kt            # User input handling
        ├── Output.kt           # Game visualization

# How to run:
Use -git clone https://github.com/isel-leic-tds/33d-2526i-grupo07.git to clone the repository.
Then you can choose:
- to play locally, doing a run on main.kt and using the command new (#|@).
- to play via persistent files, allowing the main to have multiple instances and giving a name to the game while joining on the other instance.

# Implemented commands:

    Command	            Arguments	                    Description
    NEW <Color> <Name>	(#|@) and a possible lobbyId    Creates a new game, optionally saved to file.
    JOIN <Name>	        —	                            Joins an existing game.
    PLAY <Position>	    e.g., 2B	                    Plays a piece at the specified position.
    PASS	            —	                            Passes the turn if no valid moves are available.
    TARGETS <ON/OFF>	(ON|OFF)	                    Toggles move suggestions.
    REFRESH	            —	                            Reloads the state of a persisted game.
    SHOW	            —	                            Displays the current game board.
    EXIT	            —	                            Exits the current game.

# Example of a gameplay: 
    *(The gameplay has BOARD_SIZE = 8:)*

    >new #
    You are player # in local game.
    A B C D E F G H
    1 . . . . . . . .
    2 . . . . . . . .
    3 . . . . . . . .
    4 . . . @ # . . .
    5 . . . # @ . . .
    6 . . . . . . . .
    7 . . . . . . . .
    8 . . . . . . . .
    # =  2 | @ =  2
    Turn: #
    > targets On
    > show
    You are player # in local game.
    A B C D E F G H
    1 . . . . . . . .
    2 . . . . . . . .
    3 . . . * . . . .
    4 . . * @ # . . .
    5 . . . # @ * . .
    6 . . . . * . . .
    7 . . . . . . . .
    8 . . . . . . . .
    # =  2 | @ =  2
    Turn: #
    > play 3D
    You are player @ in local game.
    A B C D E F G H
    1 . . . . . . . .
    2 . . . . . . . .
    3 . . * # * . . .
    4 . . . # # . . .
    5 . . * # @ . . .
    6 . . . . . . . .
    7 . . . . . . . .
    8 . . . . . . . .
    # =  4 | @ =  1
    Turn: @

The next step is to use this version to implement a visual interface with auto-refresh for the second part of the project, while storing the current game state in the cloud using "MongoDB".

For more info about the current state of the project, feel free to check the .puml that describe the logic behind each module.