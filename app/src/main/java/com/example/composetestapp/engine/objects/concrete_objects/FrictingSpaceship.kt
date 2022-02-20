package com.example.composetestapp.engine.objects.concrete_objects

import com.example.composetestapp.engine.*
import com.example.composetestapp.engine.systems.collision.CollidableObject
import com.example.composetestapp.engine.systems.collision.hitboxes.CircleHitboxTemplate
import com.example.composetestapp.engine.systems.collision.hitboxes.NoHitboxTemplate
import com.example.composetestapp.engine.traits_without_systems.type.ObjectType

class FrictingSpaceship(
    startCoords: Coords = ZERO_COORDS,
    startVelocity: Vector = ZERO_VECTOR,
    friction: Int = BASE_FRICTION
): MovingFrictingObject(
    objectType = ObjectType.Spaceship(),
    startCoords = startCoords,
    startVelocity = startVelocity,
    friction = friction
), CollidableObject<Unit, Coords> {
    override val coords: Coords
        get() = params.coords

    override val hitMeBoxTemplate = NoHitboxTemplate
    override fun hitMeBoxParams() = Unit

    override val hitThemBoxTemplate = CircleHitboxTemplate(radius = 2.0)
    override fun hitThemBoxParams(): Coords {
        return params.coords
    }
}

