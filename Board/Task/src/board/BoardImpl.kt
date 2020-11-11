package board

import board.Direction.*

fun createSquareBoard(width: Int): SquareBoard = SquareBoardImpl.create(width)
fun <T> createGameBoard(width: Int): GameBoard<T> = GameBoardImpl.create(width)

