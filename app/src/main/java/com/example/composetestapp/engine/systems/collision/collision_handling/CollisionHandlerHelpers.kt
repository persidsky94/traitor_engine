package com.example.composetestapp.engine.systems.collision.collision_handling

import com.example.composetestapp.engine.systems.collision.CollidableObject

fun CollisionHandler.with(
    additionalHandleCollision: ((CollidableObject<*, *>, CollidableObject<*, *>) -> Unit)
): CollisionHandler {
    val thisRef = this
    return object: CollisionHandler {
        override fun handleCollision(
            hittedObject: CollidableObject<*, *>,
            hittingObject: CollidableObject<*, *>
        ) {
            thisRef.handleCollision(hittedObject, hittingObject)
            additionalHandleCollision(hittedObject, hittingObject)
        }
    }
}