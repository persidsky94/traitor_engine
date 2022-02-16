package com.example.composetestapp.engine.objects.concrete_objects

import com.example.composetestapp.engine.*
import com.example.composetestapp.engine.systems.moving.MovingObjectImpl
import com.example.composetestapp.engine.systems.moving.MovingObjectParams
import kotlin.math.pow

open class MovingFrictingObject(
    objectType: ObjectType,
    startCoords: Coords = ZERO_COORDS,
    startVelocity: Vector = ZERO_VECTOR,
    friction: Int = BASE_FRICTION
): MovingObjectImpl(
    objectType = objectType,
    startCoords = startCoords,
    startVelocity = startVelocity,
    formula = { coords, velocity, deltaT, force ->
        val _coords = coords + velocity*deltaT
        val frictionQuotient = friction.toFloat()/100.0
        val ticks = deltaT.toDouble()/FRICTION_TICKS_MS
        val _velocity = velocity*frictionQuotient.pow(ticks) + force*deltaT

        MovingObjectParams(_coords, _velocity, _velocity)
    }
) {
    companion object {

        const val FRICTION_TICKS_MS = 1000.0/(60.0*1.0)
    }
}

const val BASE_FRICTION = 95