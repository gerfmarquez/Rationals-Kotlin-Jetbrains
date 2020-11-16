package board

import java.lang.IllegalArgumentException

/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Copyright 2020, Gerardo Marquez.
 */

data class Cell(val i: Int, val j: Int) {
    override fun toString()= "($i, $j)"
}

enum class Direction {
    UP, DOWN, RIGHT, LEFT;

    fun reversed() = when (this) {
        UP -> DOWN
        DOWN -> UP
        RIGHT -> LEFT
        LEFT -> RIGHT
    }
}

interface SquareBoard {
    val width: Int

    fun getCellOrNull(i: Int, j: Int): Cell?
    fun getCell(i: Int, j: Int): Cell

    fun getAllCells(): Collection<Cell>

    fun getRow(i: Int, jRange: IntProgression): List<Cell>
    fun getColumn(iRange: IntProgression, j: Int): List<Cell>

    fun Cell.getNeighbour(direction: Direction): Cell?
}
class SquareBoardImpl private constructor(
        override val width: Int,
        private val cells: MutableList<Cell>): SquareBoard {

    override fun getCellOrNull(i: Int, j: Int): Cell? {
        return cells.find { cell -> cell.i == i && cell.j == j }
    }

    override fun getCell(i: Int, j: Int): Cell {
        fun fail() : Nothing { throw IllegalArgumentException("Non-existent element") }
        return cells.find { cell -> cell.i == i && cell.j == j }?:fail()
    }

    override fun getAllCells(): Collection<Cell> {
        return cells
    }

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> {
        val filter = cells.filter { cell -> cell.i == i && cell.j in jRange }
        return if(jRange.first < jRange.last) {
            filter.sortedBy { it.j }
        } else {
            filter.sortedByDescending { it.j }
        }
    }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> {
        val filter = cells.filter { cell -> cell.j == j && cell.i in iRange }
        return if(iRange.first < iRange.last) {
            filter.sortedBy { it.i }
        } else {
            filter.sortedByDescending { it.i }
        }
    }

    override fun Cell.getNeighbour(direction: Direction): Cell? {
        val (i,j) = when(direction) {
            Direction.UP -> (this.i - 1 to this.j)
            Direction.DOWN -> (this.i + 1 to this.j)
            Direction.LEFT -> (this.i to this.j - 1)
            Direction.RIGHT -> (this.i to this.j + 1)
        }
        return cells.find { cell -> cell.i == i && cell.j  == j }
    }

    companion object {
        fun create(width: Int) : SquareBoard {
            val cells = mutableListOf<Cell>()
            for(rowIndex in 1..width) {
                for(columnIndex in 1..width) {
                    cells.add(Cell(rowIndex,columnIndex))
                }
            }
            return SquareBoardImpl(width, cells)
        }
    }
}

interface GameBoard<T> : SquareBoard {

    operator fun get(cell: Cell): T?
    operator fun set(cell: Cell, value: T?)

    fun filter(predicate: (T?) -> Boolean): Collection<Cell>
    fun find(predicate: (T?) -> Boolean): Cell?
    fun any(predicate: (T?) -> Boolean): Boolean
    fun all(predicate: (T?) -> Boolean): Boolean
}

class GameBoardImpl<T> private constructor (
        private val gameBoard: MutableMap<Cell,T>,
        override val width: Int,
        squareBoard: SquareBoard) : SquareBoard by squareBoard, GameBoard<T> {


    override operator fun get(cell: Cell): T? {
        return gameBoard[cell]
    }
    override operator fun set(cell: Cell, value: T?) {
        if(value != null) {
            gameBoard[cell] = value
        }
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> {
        val list = mutableListOf<Cell>()
        for(element in gameBoard) {
            if(predicate(element.value)) list.add(element.key)
        }
        return list
    }
    override fun find(predicate: (T?) -> Boolean): Cell? {
        for(element in gameBoard) {
            if(predicate(element.value)) return element.key
        }
        return null
    }
    override fun any(predicate: (T?) -> Boolean): Boolean {
        for(element in gameBoard) {
            if(predicate(element.value)) return true
        }
        return false
    }
    override fun all(predicate: (T?) -> Boolean): Boolean {
        var all = true
        for(element in gameBoard) {
            if(predicate(element.value) && all) all = true
            else return false
        }
        return all
    }
    companion object {
        fun <T> create(width: Int) : GameBoard<T> {
            val squareBoard = SquareBoardImpl.create(width)
            val map : MutableMap<Cell,T> = squareBoard.getAllCells()
                    .map { it to null as T }.toMap().toMutableMap()
            return GameBoardImpl(map,width,squareBoard)
        }
    }
}