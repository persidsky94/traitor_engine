package com.example.composetestapp.engine

import kotlin.math.sqrt

val ZERO_VECTOR = 0.0 to 0.0
val ZERO_COORDS = 0.0 to 0.0
fun Coords(first: Float, second: Float): Coords =
    first.toDouble() to second.toDouble()

operator fun Vector.plus(other: Vector) =
    first + other.first to second + other.second

operator fun Coords.minus(other: Coords) =
    first - other.first to second - other.second

operator fun Vector.times(times: Float): Vector =
    first*times to second*times

operator fun Vector.times(times: Double): Vector =
    first*times to second*times

operator fun Vector.times(times: Int): Vector = times(times.toFloat())
operator fun Vector.times(times: Long): Vector = times(times.toFloat())
operator fun Float.times(vector: Vector): Vector =
    this*vector.first to this*vector.second

fun Vector.normalize(): Vector {
    val length = length()
    return first/length to second/length
}

fun Vector.length(): Double = sqrt(first * first + second * second)