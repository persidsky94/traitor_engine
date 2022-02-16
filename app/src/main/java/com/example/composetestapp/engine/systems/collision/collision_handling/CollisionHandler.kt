package com.example.composetestapp.engine.systems.collision.collision_handling

import com.example.composetestapp.engine.systems.collision.CollidableObject

interface CollisionHandler {
    fun handleCollision(hittedObject: CollidableObject<*, *>, hittingObject: CollidableObject<*, *>)
}