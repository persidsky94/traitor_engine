package com.example.composetestapp.engine.systems.collision.collision_handling

import com.example.composetestapp.engine.ObjId
import com.example.composetestapp.engine.TraitObjId
import com.example.composetestapp.engine.systems.collision.CollidableObject
import com.example.composetestapp.engine.systems.collision.trait.CollidableTrait

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


class CollisionHandlerForTraitsImpl: CollisionHandlerForTraits {
    private val hittedCollisionDelegates: MutableMap<TraitObjId, CollisionHandlerForTraits> = mutableMapOf()
    private val hittingCollisionDelegates: MutableMap<TraitObjId, CollisionHandlerForTraits> = mutableMapOf()

    fun addCollisionHandlerForHittedTrait(hittedObject: CollidableTrait<*, *>, collisionHandler: CollisionHandlerForTraits) {
        hittedCollisionDelegates[hittedObject.traitObjId] = collisionHandler
    }

    fun addCollisionHandlerForHittingTrait(hittingObject: CollidableTrait<*, *>, collisionHandler: CollisionHandlerForTraits) {
        hittingCollisionDelegates[hittingObject.traitObjId] = collisionHandler
    }

    fun removeCollisionHandlerForTrait(traitObjId: TraitObjId) {
        hittedCollisionDelegates.remove(traitObjId)
        hittingCollisionDelegates.remove(traitObjId)
    }

    override fun handleCollision(
        hittedObject: CollidableTrait<*, *>,
        hittingObject: CollidableTrait<*, *>
    ) {
        hittedCollisionDelegates[hittedObject.traitObjId]?.handleCollision(hittedObject, hittingObject)
        hittingCollisionDelegates[hittingObject.traitObjId]?.handleCollision(hittedObject, hittingObject)
    }
}