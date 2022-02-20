package com.example.composetestapp.engine.systems.removation

import com.example.composetestapp.engine.GameEngine
import com.example.composetestapp.engine.ObjId

class RemoveObjectMediatorForGameEngine(
    private val gameEngine: GameEngine
): RemoveObjectMediator {
    override fun onRemoveObject(objId: ObjId) {
        gameEngine.removeObjectById(objId)
    }
}