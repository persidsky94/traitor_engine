package com.example.composetestapp.engine.systems.collision.system

import com.example.composetestapp.engine.*
import com.example.composetestapp.engine.systems.base_system.BaseRulesSystem
import com.example.composetestapp.engine.systems.collision.collision_detection.CollisionDetector
import com.example.composetestapp.engine.systems.collision.collision_handling.CollisionHandlerForTraits
import com.example.composetestapp.engine.systems.collision.trait.CollidableTrait
import com.example.composetestapp.engine.systems.collision.trait.CollidableTrait.Companion.COLLIDABLE_SYSTEM_ID

class CollisionSystem(
    private val collisionDetector: CollisionDetector,
    private val collisionHandler: CollisionHandlerForTraits
): BaseRulesSystem<CollidableTrait<*,*>>() {

    override val systemId: SystemId = SystemId(COLLIDABLE_SYSTEM_ID)

    //TODO: maybe use more effective data structure?
    private val collidableTraits: MutableList<CollidableTrait<*, *>> = mutableListOf()

    override fun addTrait(trait: CollidableTrait<*, *>) {
        collidableTraits.add(trait)
    }

    override fun removeTrait(trait: CollidableTrait<*, *>) {
        collidableTraits.remove(trait)
    }

    override fun removeTraitById(id: TraitObjId) {
        collidableTraits.removeAll { it.traitObjId == id }
    }

    override fun removeTraitsByParentObjectId(id: ObjId) {
        collidableTraits.removeAll { it.parentObjId == id }
    }

    override fun applyRules() {
        detectAndHandleAllCollisions()
    }

    private fun detectAndHandleAllCollisions() {
        val allHitMeBoxesToObjects = collidableTraits.map { it.getHitMeBox() to it }
        val allHitThemBoxesToObjects = collidableTraits.map { it.getHitThemBox() to it }
        val hitMeBoxesSortedByX = allHitMeBoxesToObjects.sortedBy { it.first.coords.first }
//        val hitMeBoxesSortedByY = allHitMeBoxesToObjects.sortedBy { it.first.coords.second }
        val hitThemBoxesSortedByX = allHitThemBoxesToObjects.sortedBy { it.first.coords.first }
//        val hitThemBoxesSortedByY = allHitThemBoxesToObjects.sortedBy { it.first.coords.second }
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