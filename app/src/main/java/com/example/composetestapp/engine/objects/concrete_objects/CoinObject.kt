package com.example.composetestapp.engine.objects.concrete_objects

import com.example.composetestapp.engine.Coords
import com.example.composetestapp.engine.ObjectType
import com.example.composetestapp.engine.Vector
import com.example.composetestapp.engine.systems.moving.MovingObjectParams

class CoinObject(
    startCoords: Coords,
    startVelocity: Vector,
    formula: (Coords, Vector, Long, Vector) -> MovingObjectParams,
    radius: Double
): CollidableCircle(
    objectType = ObjectType.Coin(radius),
    startCoords = startCoords,
    startVelocity = startVelocity,
    formula = formula,
    radius = radius
)