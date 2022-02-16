package com.example.composetestapp.engine.systems.moving

import com.example.composetestapp.engine.*
import com.example.composetestapp.engine.objects.random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class MovingObjectParams(
    val coords: Coords,
    val velocity: Vector,
    val direction: Vector
)

typealias MoveFormulaType = (Coords, Vector, Long, Vector) -> MovingObjectParams

val ignoreForceFormula: MoveFormulaType = { coords, velocity, deltaT, _ ->
    MovingObjectParams(
        coords = coords + velocity*deltaT,
        velocity = velocity,
        direction = velocity
    )
}

fun verticalSinCoordinateFormula(startingCoords: Coords, maxDeviation: Double): MoveFormulaType {
    val totalTime = TotalTime(0.0)
    val periodInSeconds = random.nextDouble(from=0.5, until=3.0)
    val deviation = random.nextDouble(from=maxDeviation/2, until = maxDeviation)
    val formula: MoveFormulaType = { coords, velocity, deltaT, _ ->
        totalTime.seconds += deltaT/1000.0
        val arg = totalTime.seconds* PI /periodInSeconds
        val newVelocity = Vector(0.0, cos(arg) *deviation)
        MovingObjectParams(
            coords = startingCoords + Coords(0.0, sin(arg) *deviation),
            velocity = newVelocity,
            direction = newVelocity
        )
    }
    return formula
}

private class TotalTime(var seconds: Double)
