package com.example.composetestapp.engine

import com.example.composetestapp.engine.traits_without_systems.type.ObjectType

interface EngineObject {
    val objectId: ObjId
    val objectType: ObjectType

    companion object {
        var nextFreeId = 0
        fun generateNextObjId(): ObjId {
            return ++nextFreeId
        }
    }
}