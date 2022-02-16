package com.example.composetestapp.engine.systems.collision

import com.example.composetestapp.engine.ObjId
import com.example.composetestapp.engine.length
import com.example.composetestapp.engine.minus
import com.example.composetestapp.engine.systems.collision.collision_detection.CollisionDetector
import com.example.composetestapp.engine.systems.collision.collision_handling.CollisionHandler

class CollisionEngineImpl(
    private val collisionDetector: CollisionDetector,
    private val collisionHandler: CollisionHandler
): CollisionEngine {
    //TODO: maybe use more effective data structure?
    private val collidableObjects: MutableList<CollidableObject<*, *>> = mutableListOf()

    override fun addCollidableObject(collidableObject: CollidableObject<*, *>) {
        collidableObjects.add(collidableObject)
    }

    override fun removeCollidableObject(collidableObject: CollidableObject<*, *>) {
        collidableObjects.remove(collidableObject)
    }

    override fun removeObjectById(id: ObjId) {
        collidableObjects.removeAll { it.objectId == id }
    }

    override fun detectAndHandleAllCollisions() {
        val allHitMeBoxesToObjects = collidableObjects.map { it.getHitMeBox() to it }
        val allHitThemBoxesToObjects = collidableObjects.map { it.getHitThemBox() to it }
        val hitMeBoxesSortedByX = allHitMeBoxesToObjects.sortedBy { it.second.coords.first }
        val hitMeBoxesSortedByY = allHitMeBoxesToObjects.sortedBy { it.second.coords.second }
        val hitThemBoxesSortedByX = allHitThemBoxesToObjects.sortedBy { it.second.coords.first }
        val hitThemBoxesSortedByY = allHitThemBoxesToObjects.sortedBy { it.second.coords.second }
        val maxHitMeDiameter = hitMeBoxesSortedByX.maxByOrNull { it.first.diameter }?.first?.diameter ?: return
        val maxHitThemDiameter = hitThemBoxesSortedByX.maxByOrNull { it.first.diameter }?.first?.diameter ?: return
        val maxDiameter = maxHitMeDiameter + maxHitThemDiameter
        val potentialHitted = hitMeBoxesSortedByX.map { (hitMeBox, hitMeObj) ->
            hitThemBoxesSortedByX
                .filter { (it.first.coords - hitMeBox.coords).length() < maxDiameter }
                .map { it to (hitMeBox to hitMeObj) }
        }.flatten()
        potentialHitted.forEach { (hitThem, hitMe) ->
            val (hitThemBox, hitThemObj) = hitThem
            val (hitMeBox, hitMeObj) = hitMe
            val collision = collisionDetector.detectCollision(hitMeBox = hitMeBox, hitThemBox = hitThemBox)
            if (collision) {
                collisionHandler.handleCollision(hittedObject = hitMeObj, hittingObject = hitThemObj)
            }
        }
    }
}