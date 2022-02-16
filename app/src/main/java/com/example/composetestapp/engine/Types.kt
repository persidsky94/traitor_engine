package com.example.composetestapp.engine

typealias Vector = Pair<Double, Double>
typealias Coords = Pair<Double, Double>
typealias ObjId = Int

const val ZeroAsDouble = 0.toDouble()

data class GameField(
    val width: Int,
    val height: Int
)