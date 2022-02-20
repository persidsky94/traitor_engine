package com.example.composetestapp.engine.systems.collision.collision_handling

import com.example.composetestapp.engine.systems.collision.CollidableObject
import com.example.composetestapp.engine.systems.collision.trait.CollidableTrait

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


fun CollisionHandlerForTraits.with(
    additionalHandleCollision: ((CollidableTrait<*, *>, CollidableTrait<*, *>) -> Unit)
): CollisionHandlerForTraits {
    val thisRef = this
    return object: CollisionHandlerForTraits {
        override fun handleCollision(
            hittedObject: CollidableTrait<*, *>,
            hittingObject: CollidableTrait<*, *>
        ) {
            thisRef.handleCollision(hittedObject, hittingObject)
            additionalHandleCollision(hittedObject, hittingObject)
        }
    }
}