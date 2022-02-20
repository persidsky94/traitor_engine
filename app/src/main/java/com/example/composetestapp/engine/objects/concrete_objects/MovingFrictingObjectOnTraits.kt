package com.example.composetestapp.engine.objects.concrete_objects

import com.example.composetestapp.engine.*
import com.example.composetestapp.engine.objects.base.BaseObject
import com.example.composetestapp.engine.objects.base.BaseObjectImpl
import com.example.composetestapp.engine.systems.moving.standardFormulaWithForceAndFriction
import com.example.composetestapp.engine.systems.moving.trait.MoveTraitImpl
import com.example.composetestapp.engine.traits_without_systems.position.MutablePositionTrait
import com.example.composetestapp.engine.traits_without_systems.type.ObjectType
import com.example.composetestapp.engine.traits_without_systems.type.ObjectTypeTraitImpl

fun movingFrictingObjectOnTraits(
    objectType: ObjectType,
    startCoords: Coords = ZERO_COORDS,
    startVelocity: Vector = ZERO_VECTOR,
    friction: Int = BASE_FRICTION,
    obj: BaseObject = BaseObjectImpl(),
    positionTrait: MutablePositionTrait = MutablePositionTrait(
        coords = startCoords,
        parentObjId = obj.objId
    )
): BaseObject {
    val objectTypeTrait = ObjectTypeTraitImpl(
        objectType = objectType,
        parentObjId = obj.objId
    )
    val moveTrait = MoveTraitImpl(
        objectTypeTrait = objectTypeTrait,
        startVelocity = startVelocity,
        formula = standardFormulaWithForceAndFriction(friction),
        positionTrait = positionTrait,
        parentObjId = obj.objId
    )
    return obj
        .addTrait(objectTypeTrait)
        .addTrait(positionTrait)
        .addTrait(moveTrait)
}