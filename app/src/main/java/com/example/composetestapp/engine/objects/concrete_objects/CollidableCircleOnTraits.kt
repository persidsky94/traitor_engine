package com.example.composetestapp.engine.objects.concrete_objects

import com.example.composetestapp.engine.Coords
import com.example.composetestapp.engine.ObjId
import com.example.composetestapp.engine.traits_without_systems.type.ObjectType
import com.example.composetestapp.engine.Vector
import com.example.composetestapp.engine.objects.base.BaseObject
import com.example.composetestapp.engine.objects.base.BaseObjectImpl
import com.example.composetestapp.engine.objects.base.BaseTrait
import com.example.composetestapp.engine.systems.collision.hitboxes.CircleHitboxTemplate
import com.example.composetestapp.engine.systems.collision.hitboxes.HitMeBoxTemplate
import com.example.composetestapp.engine.systems.collision.hitboxes.HitThemBoxTemplate
import com.example.composetestapp.engine.systems.collision.hitboxes.NoHitboxTemplate
import com.example.composetestapp.engine.systems.collision.trait.CollidableTrait
import com.example.composetestapp.engine.systems.moving.MovingObjectParams
import com.example.composetestapp.engine.systems.moving.trait.MoveTraitImpl
import com.example.composetestapp.engine.traits_without_systems.position.MutablePositionTrait
import com.example.composetestapp.engine.traits_without_systems.position.PositionTrait
import com.example.composetestapp.engine.traits_without_systems.type.ObjectTypeTraitImpl

fun collidableCircleOnTraits(
    objectType: ObjectType,
    startCoords: Coords,
    startVelocity: Vector,
    formula: (Coords, Vector, Long, Vector) -> MovingObjectParams,
    radius: Double
): BaseObject {
    val obj = BaseObjectImpl()
    val objectTypeTrait = ObjectTypeTraitImpl(
        objectType = objectType,
        parentObjId = obj.objId
    )
    val positionTrait = MutablePositionTrait(coords = startCoords, parentObjId = obj.objId)
    val moveTrait = MoveTraitImpl(
        objectTypeTrait = objectTypeTrait,
        startVelocity = startVelocity,
        formula = formula,
        positionTrait = positionTrait,
        parentObjId = obj.objId
    )
    val collisionTrait = hitMeCircleCollisionTrait(
        parentObjId = obj.objId,
        objectType = objectType,
        positionTrait = positionTrait,
        circleHitboxRadius = radius
    )

    return obj
        .addTrait(objectTypeTrait)
        .addTrait(positionTrait)
        .addTrait(moveTrait)
        .addTrait(collisionTrait)
}

fun hitMeCircleCollisionTrait(
    parentObjId: ObjId,
    objectType: ObjectType,
    positionTrait: PositionTrait,
    circleHitboxRadius: Double = 2.0
): CollidableTrait<Coords, Unit> {
    return object: CollidableTrait<Coords, Unit> {
        override fun objectType() = objectType

        override val parentObjId = parentObjId
        override val traitObjId = BaseTrait.generateNextTraitObjId()

        override val hitMeBoxTemplate: HitMeBoxTemplate<Coords, *> = CircleHitboxTemplate(radius = circleHitboxRadius)
        override fun hitMeBoxParams(): Coords {
            return positionTrait.data
        }
        override val hitThemBoxTemplate: HitThemBoxTemplate<Unit, *> = NoHitboxTemplate
        override fun hitThemBoxParams() = Unit
    }
}