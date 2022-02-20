package com.example.composetestapp.engine.objects.object_dependent_handlers

import com.example.composetestapp.engine.systems.collision.collision_handling.CollisionHandlerForTraits
import com.example.composetestapp.engine.systems.collision.trait.CollidableTrait
import com.example.composetestapp.engine.systems.removation.RemoveObjectMediator
import com.example.composetestapp.engine.traits_without_systems.type.ObjectType

class AsteroidCollisionHandlerForTraits(
    private val objectRemover: RemoveObjectMediator
): CollisionHandlerForTraits {
    override fun handleCollision(
        hittedObject: CollidableTrait<*, *>,
        hittingObject: CollidableTrait<*, *>
    ) {
        objectRemover.onRemoveObject(hittedObject.parentObjId)
        //TODO: should be handled by hitMeBox on spaceships
        if (hittingObject.objectType() is ObjectType.Spaceship) {
            objectRemover.onRemoveObject(hittingObject.parentObjId)
        }
    }
}