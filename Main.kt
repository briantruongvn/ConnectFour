package connectfour


object UserInput {
    var end = false
    var rows = 0
    var columns = 0
    var firstPlayer = ""
    var secondPlayer = ""
    var numGame = 1
    var firstPlayerScore = 0
    var secondPlayerScore = 0
    var turn = false
    fun askUserInput() {
        println("Connect Four")
        println("First player's name:")
        firstPlayer = readLine()!!
        println("Second player's name:")
        secondPlayer = readLine()!!
        val boardSize = getBoardSize()
        rows = boardSize.first
        columns = boardSize.second
    }

    fun chooseNumGame(): Int {
        var exit = false
        while(!exit) {
            try {
                println("Do you want to play single or multiple games?")
                println("For a single game, input 1 or press Enter")
                println("Input a number of games:")
                val res = readLine()!!
                if(res == "" || res == "1") {
                    return 1
                }
                numGame = res.toInt()
                exit = if (numGame <= 0) {
                    println("Invalid input")
                    false
                } else true
            } catch (e: Exception) {
                println("Invalid input")
            }
        }
        return numGame
    }

    fun displayUserInfo() {
        println("$firstPlayer VS $secondPlayer")
        println("$rows X $columns board")
        if (numGame != 1) {
            println("Total $numGame games")
        }
    }
}

fun getBoardSize(): Pair<Int, Int> {
    var stop = false
    var rows = 0
    var columns = 0
    while (!stop) {
        println("Set the board dimensions (Rows x Columns)")
        println("Press Enter for default (6 x 7)")
        try {
            var answer = readLine()!!
            if (answer == "") {
                rows = 6
                columns = 7
                stop = true
            } else {
                answer = answer.lowercase().replace("\\s".toRegex(), "")
                rows = answer.split("x")[0].toInt()
                columns = answer.split("x")[1].toInt()
                stop = if (rows < 5 || rows > 9) {
                    println("Board rows should be from 5 to 9")
                    false
                } else if (columns < 5 || columns > 9) {
                    println("Board columns should be from 5 to 9")
                    false
                } else true
            }
        } catch (e: Exception) {
            println("Invalid input")
        }
    }
    return Pair(rows, columns)
}


class Game {
    private val board = MutableList(UserInput.rows) { MutableList (UserInput.columns){ " " }}
    private val rows = board.size
    private val columns = board.first().size

    private fun drawGameBoard(board: MutableList<MutableList<String>>) {
        val vertical = '\u2551'
        val horizontal = '\u2550'
        val cornerLeft = '\u255A'
        val cornerRight = '\u255D'
        val between = '\u2569'
        val symbol = MutableList(rows) { MutableList(columns + 1) { vertical } }

        for (i in 0..rows + 1) {
            when (i) {
                0 -> {
                    for (j in 1..columns) {
                        print(" $j")
                    }
                    println()
                }
                in 1..rows -> {
                    for (j in 1..columns) {
                        print(symbol[i - 1][j - 1])
                        print(board[i - 1][j - 1])
                        if (j == columns) {
                            print(symbol[i - 1][j - 1])
                        }
                    }
                    println()
                }
                else -> {
                    for (j in 1..columns) {
                        if (j == 1) print("$cornerLeft")
                        if (j == columns) {
                            print("$horizontal$cornerRight")
                        }
                        else {
                            print("$horizontal$between")
                        }
                    }
                    println()
                }
            }
        }
    }

    private fun addPlayerTurn(playerName: String, symbol: String, board: MutableList<MutableList<String>>) {
        var stop = false
        while(!stop) {
            println("$playerName\'s turn:")
            try {
                val res = readLine()!!
                if (res == "end") {
                    println("Game over!")
                    UserInput.end = true
                    return
                }
                val playerTurn = res.toInt()
                if (playerTurn < 1 || playerTurn > UserInput.columns) {
                    println("The column number is out of range (1 - ${UserInput.columns})")
                    stop = false
                } else if (
                    board[0][playerTurn - 1] == "*" ||
                    board[0][playerTurn - 1] == "o"
                ) {
                    println("Column $playerTurn is full")
                    stop = false
                } else {
                    for (i in UserInput.rows downTo 1) {
                        if (board[i - 1][playerTurn - 1] == " ") {
                            board[i - 1][playerTurn - 1] = symbol
                            drawGameBoard(board)
                            break
                        }
                    }
                    stop = true

                }
            } catch (e: Exception) {
                println("Incorrect column number")
                stop = false
            }
        }
        UserInput.end = false
    }

    private fun isDraw(board: MutableList<MutableList<String>>): Boolean {
        if (!board[0].contains(" ")) {
            println("It is a draw")
            UserInput.firstPlayerScore += 1
            UserInput.secondPlayerScore += 1
            return true
        }
        return false
    }

    private fun checkPattern(patternBoard: String): Boolean {
        if (patternBoard == "oooo") {
            UserInput.firstPlayerScore += 2
            println("Player ${UserInput.firstPlayer} won")
            return true
        }
        if (patternBoard == "****") {
            UserInput.secondPlayerScore += 2
            println("Player ${UserInput.secondPlayer} won")
            return true
        }
        return false
    }

    private fun isWon(board: MutableList<MutableList<String>>): Boolean {
        val rows = board.size
        val columns = board[0].size
        for (i in rows downTo 1) {
            for (j in 0..columns - 4) {
                val horizontalBoard = board[i - 1].subList(j, 4 + j).joinToString("")
                if (checkPattern(horizontalBoard)) return true
            }
        }
        for (i in 0.. rows - 4) {
            for ( j in 0 until columns) {
                val verticalBoard = board[i][j] + board[1 + i][j] + board[2 + i][j] + board[3 + i][j]
                if (checkPattern(verticalBoard)) return true
            }
        }
        for (i in 0..rows - 4) {
            for (j in 0.. columns - 4) {
                val diagonalBoardLeft = board[i][j] + board[1 + i][1 + j] + board[2 + i][2 + j] + board[3 + i][3 + j]
                val diagonalBoardRight = board[3 + i][j] + board[2 + i][1 + j] + board[1 + i][2 + j] + board[i][3 + j]
                if (checkPattern(diagonalBoardLeft) || checkPattern(diagonalBoardRight)) return true
            }
        }
        return false
    }

    fun displayScore() {
        println("Score")
        println("${UserInput.firstPlayer}: ${UserInput.firstPlayerScore} ${UserInput.secondPlayer}: ${UserInput.secondPlayerScore}")
    }

    fun playGameBoard() {
        drawGameBoard(board)
        if(UserInput.turn) {
            loop@ while (!UserInput.end && !isWon(board) && !isDraw(board)) {
                addPlayerTurn(UserInput.secondPlayer, "*", board)
                if (UserInput.end || isWon(board) || isDraw(board)) break@loop
                addPlayerTurn(UserInput.firstPlayer, "o", board)
            }
        } else {
            loop@ while (!UserInput.end && !isWon(board) && !isDraw(board)) {
                addPlayerTurn(UserInput.firstPlayer, "o", board)
                if (UserInput.end || isWon(board) || isDraw(board)) break@loop
                addPlayerTurn(UserInput.secondPlayer, "*", board)
            }
        }
    }

}
fun playMultiGames() {
    for (i in 1..UserInput.numGame) {
        val game = Game()
        println(if (UserInput.numGame == 1) "Single game" else "Game #$i")
        game.playGameBoard()
        if (UserInput.end) return
        game.displayScore()
        UserInput.turn = !UserInput.turn
    }
    println("Game over!")
}

fun test() {
    //This is to test
}
fun main() {
    UserInput.askUserInput()
    UserInput.chooseNumGame()
    UserInput.displayUserInfo()
    playMultiGames()
    //Test
}