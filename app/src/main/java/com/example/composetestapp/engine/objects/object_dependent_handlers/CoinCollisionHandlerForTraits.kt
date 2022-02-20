package com.example.composetestapp.engine.objects.object_dependent_handlers

import com.example.composetestapp.engine.systems.collision.collision_handling.CollisionHandlerForTraits
import com.example.composetestapp.engine.systems.collision.trait.CollidableTrait
import com.example.composetestapp.engine.systems.removation.RemoveObjectMediator

class CoinCollisionHandlerForTraits(
    private val objectRemover: RemoveObjectMediator
): CollisionHandlerForTraits {
    override fun handleCollision(
        hittedObject: CollidableTrait<*, *>,
        hittingObject: CollidableTrait<*, *>
    ) {
        objectRemover.onRemoveObject(hittedObject.parentObjId)
    }
}