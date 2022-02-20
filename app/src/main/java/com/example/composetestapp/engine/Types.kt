package com.example.composetestapp.engine

typealias Vector = Pair<Double, Double>
typealias Coords = Pair<Double, Double>
typealias ObjId = Int

@JvmInline
value class TraitObjId(private val value: Int)

@JvmInline
value class SystemId(private val value: String)


const val ZeroAsDouble = 0.toDouble()

data class GameField(
    val width: Int,
    val height: Int
)