package com.example.composetestapp.engine.objects.concrete_objects

import com.example.composetestapp.engine.Coords
import com.example.composetestapp.engine.traits_without_systems.type.ObjectType
import com.example.composetestapp.engine.Vector
import com.example.composetestapp.engine.systems.collision.CollidableObject
import com.example.composetestapp.engine.systems.collision.hitboxes.CircleHitboxTemplate
import com.example.composetestapp.engine.systems.collision.hitboxes.HitMeBoxTemplate
import com.example.composetestapp.engine.systems.collision.hitboxes.HitThemBoxTemplate
import com.example.composetestapp.engine.systems.collision.hitboxes.NoHitboxTemplate
import com.example.composetestapp.engine.systems.moving.MovingObjectImpl
import com.example.composetestapp.engine.systems.moving.MovingObjectParams

open class CollidableCircle(
    objectType: ObjectType,
    startCoords: Coords,
    startVelocity: Vector,
    formula: (Coords, Vector, Long, Vector) -> MovingObjectParams,
    radius: Double
): CollidableObject<Coords, Unit>,
    MovingObjectImpl(
        objectType = objectType,
        startCoords = startCoords,
        startVelocity = startVelocity,
        formula = formula
    )
{
    override val coords: Coords
        get() = params.coords

    override val hitMeBoxTemplate: HitMeBoxTemplate<Coords, *> = CircleHitboxTemplate(radius = radius)
    override fun hitMeBoxParams(): Coords {
        return coords
    }
    override val hitThemBoxTemplate: HitThemBoxTemplate<Unit, *> = NoHitboxTemplate
    override fun hitThemBoxParams() = Unit
}