package com.example.composetestapp.engine.systems.collision

import com.example.composetestapp.engine.ObjId

interface CollisionEngine {
    fun addCollidableObject(collidableObject: CollidableObject<*,*>)
    fun removeCollidableObject(collidableObject: CollidableObject<*, *>)
    fun removeObjectById(id: ObjId)
    fun detectAndHandleAllCollisions()
}