package com.example.composetestapp.engine.systems.collision.collision_handling

import com.example.composetestapp.engine.ObjId
import com.example.composetestapp.engine.systems.collision.CollidableObject

class CollisionHandlerImpl: CollisionHandler {
    private val trackedHittedObjectIds: MutableMap<ObjId, CollisionHandler> = mutableMapOf()
    private val trackedHittingObjectIds: MutableMap<ObjId, CollisionHandler> = mutableMapOf()

    fun addCollisionHandlerForHittedObject(hittedObject: CollidableObject<*, *>, collisionHandler: CollisionHandler) {
        trackedHittedObjectIds[hittedObject.objectId] = collisionHandler
    }

    fun addCollisionHandlerForHittingObject(hittingObject: CollidableObject<*, *>, collisionHandler: CollisionHandler) {
        trackedHittingObjectIds[hittingObject.objectId] = collisionHandler
    }

    fun removeCollisionHandlerForObject(objId: ObjId) {
        trackedHittedObjectIds.remove(objId)
        trackedHittingObjectIds.remove(objId)
    }

    override fun handleCollision(
        hittedObject: CollidableObject<*, *>,
        hittingObject: CollidableObject<*, *>
    ) {
        trackedHittedObjectIds[hittedObject.objectId]?.handleCollision(hittedObject, hittingObject)
        trackedHittingObjectIds[hittingObject.objectId]?.handleCollision(hittedObject, hittingObject)
    }
}