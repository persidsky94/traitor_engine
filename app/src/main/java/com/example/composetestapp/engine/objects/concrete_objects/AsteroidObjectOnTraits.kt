package com.example.composetestapp.engine.objects.concrete_objects

import com.example.composetestapp.engine.Coords
import com.example.composetestapp.engine.Vector
import com.example.composetestapp.engine.objects.base.BaseObject
import com.example.composetestapp.engine.systems.moving.MovingObjectParams
import com.example.composetestapp.engine.traits_without_systems.type.ObjectType

fun asteroidObjectOnTraits(
    startCoords: Coords,
    startVelocity: Vector,
    formula: (Coords, Vector, Long, Vector) -> MovingObjectParams,
    radius: Double
): BaseObject = collidableCircleOnTraits(
    objectType = ObjectType.Asteroid(radius),
    startCoords = startCoords,
    startVelocity = startVelocity,
    formula = formula,
    radius = radius
)