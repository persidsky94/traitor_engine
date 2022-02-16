package com.example.composetestapp.engine

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

sealed class ObjectType {
    class Spaceship: ObjectType()
    class Coin(val radius: Double): ObjectType()
    class Asteroid(val radius: Double): ObjectType()
}