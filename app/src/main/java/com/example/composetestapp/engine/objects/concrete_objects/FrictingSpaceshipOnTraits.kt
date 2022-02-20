package com.example.composetestapp.engine.objects.concrete_objects

import com.example.composetestapp.engine.*
import com.example.composetestapp.engine.objects.base.BaseObject
import com.example.composetestapp.engine.objects.base.BaseObjectImpl
import com.example.composetestapp.engine.objects.base.BaseTrait
import com.example.composetestapp.engine.systems.collision.hitboxes.CircleHitboxTemplate
import com.example.composetestapp.engine.systems.collision.hitboxes.NoHitboxTemplate
import com.example.composetestapp.engine.systems.collision.trait.CollidableTrait
import com.example.composetestapp.engine.traits_without_systems.position.MutablePositionTrait
import com.example.composetestapp.engine.traits_without_systems.position.PositionTrait
import com.example.composetestapp.engine.traits_without_systems.type.ObjectType

fun frictingSpaceshipOnTraits(
    startCoords: Coords = ZERO_COORDS,
    startVelocity: Vector = ZERO_VECTOR,
    friction: Int = BASE_FRICTION
): BaseObject {
    val obj = BaseObjectImpl()
    val positionTrait = MutablePositionTrait(coords = startCoords, parentObjId = obj.objId)
    val collisionTrait = hitThemCircleCollisionTrait(
        parentObjId = obj.objId,
        objectType = ObjectType.Spaceship(),
        positionTrait = positionTrait
    )
    val frictingObj = movingFrictingObjectOnTraits(
        objectType = ObjectType.Spaceship(),
        startCoords = startCoords,
        startVelocity = startVelocity,
        friction = friction,
        obj = obj,
        positionTrait = positionTrait
    )
    return frictingObj.addTrait(collisionTrait)
}

fun hitThemCircleCollisionTrait(
    parentObjId: ObjId,
    objectType: ObjectType,
    positionTrait: PositionTrait,
    circleHitboxRadius: Double = 2.0
): CollidableTrait<Unit, Coords> {
    return object: CollidableTrait<Unit, Coords> {
        override fun objectType() = objectType

        override val parentObjId = parentObjId
        override val traitObjId = BaseTrait.generateNextTraitObjId()

        override val hitMeBoxTemplate = NoHitboxTemplate
        override fun hitMeBoxParams() = Unit

        override val hitThemBoxTemplate = CircleHitboxTemplate(radius = circleHitboxRadius)
        override fun hitThemBoxParams(): Coords {
            return positionTrait.data
        }
    }
}