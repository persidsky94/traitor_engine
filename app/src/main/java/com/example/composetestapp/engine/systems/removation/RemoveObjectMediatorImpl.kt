package com.example.composetestapp.engine.systems.removation

import com.example.composetestapp.engine.ObjId
import com.example.composetestapp.engine.systems.collision.CollisionEngine
import com.example.composetestapp.engine.systems.moving.MoveEngine

class RemoveObjectMediatorImpl(
    private val moveEngine: MoveEngine,
    private val collisionEngine: CollisionEngine
): RemoveObjectMediator {
    override fun onRemoveObject(objId: ObjId) {
        moveEngine.removeObjectById(objId)
        collisionEngine.removeObjectById(objId)
    }
}