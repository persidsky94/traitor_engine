package com.example.composetestapp.engine.objects.concrete_objects

import com.example.composetestapp.engine.*
import com.example.composetestapp.engine.systems.moving.MovingObjectImpl
import com.example.composetestapp.engine.systems.moving.standardFormulaWithForceAndFriction
import com.example.composetestapp.engine.traits_without_systems.type.ObjectType

open class MovingFrictingObject(
    objectType: ObjectType,
    startCoords: Coords = ZERO_COORDS,
    startVelocity: Vector = ZERO_VECTOR,
    friction: Int = BASE_FRICTION
): MovingObjectImpl(
    objectType = objectType,
    startCoords = startCoords,
    startVelocity = startVelocity,
    formula = standardFormulaWithForceAndFriction(friction)
)


const val BASE_FRICTION = 95