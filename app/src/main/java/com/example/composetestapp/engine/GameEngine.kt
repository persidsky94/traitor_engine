package com.example.composetestapp.engine

import com.example.composetestapp.engine.objects.base.BaseObject
import com.example.composetestapp.engine.systems.base_system.BaseSystem

interface GameEngine {
    fun addSystem(system: BaseSystem<*>): GameEngine
    fun addObject(baseObject: BaseObject)
    fun removeObjectById(objId: ObjId)

    suspend fun update(deltaT: Long)
}

