package com.example.composetestapp.engine.systems.collision.collision_handling

import com.example.composetestapp.engine.systems.collision.CollidableObject
import com.example.composetestapp.engine.systems.collision.trait.CollidableTrait

interface CollisionHandler {
    fun handleCollision(hittedObject: CollidableObject<*, *>, hittingObject: CollidableObject<*, *>)
}

interface CollisionHandlerForTraits {
    fun handleCollision(hittedObject: CollidableTrait<*, *>, hittingObject: CollidableTrait<*, *>)
}