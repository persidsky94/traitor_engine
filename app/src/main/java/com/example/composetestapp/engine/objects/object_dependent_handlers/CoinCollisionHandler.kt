package com.example.composetestapp.engine.objects.object_dependent_handlers

import com.example.composetestapp.engine.systems.removation.RemoveObjectMediator
import com.example.composetestapp.engine.systems.collision.CollidableObject
import com.example.composetestapp.engine.systems.collision.collision_handling.CollisionHandler

class CoinCollisionHandler(
    private val objectRemover: RemoveObjectMediator
): CollisionHandler {
    override fun handleCollision(
        hittedObject: CollidableObject<*, *>,
        hittingObject: CollidableObject<*, *>
    ) {
        objectRemover.onRemoveObject(hittedObject.objectId)
    }
}