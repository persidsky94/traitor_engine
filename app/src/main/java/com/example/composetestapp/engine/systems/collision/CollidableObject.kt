package com.example.composetestapp.engine.systems.collision

import com.example.composetestapp.engine.Coords
import com.example.composetestapp.engine.ObjId
import com.example.composetestapp.engine.ObjectType
import com.example.composetestapp.engine.systems.collision.hitboxes.HitMeBox
import com.example.composetestapp.engine.systems.collision.hitboxes.HitMeBoxTemplate
import com.example.composetestapp.engine.systems.collision.hitboxes.HitThemBox
import com.example.composetestapp.engine.systems.collision.hitboxes.HitThemBoxTemplate

interface CollidableObject<HitMeBoxParams, HitThemBoxParams> {
    val coords: Coords // is it needed?
    val objectId: ObjId
    val objectType: ObjectType
    val hitMeBoxTemplate: HitMeBoxTemplate<HitMeBoxParams, *>
    val hitThemBoxTemplate: HitThemBoxTemplate<HitThemBoxParams, *>

    fun hitMeBoxParams(): HitMeBoxParams
    fun hitThemBoxParams(): HitThemBoxParams

    fun getHitMeBox(): HitMeBox {
        return hitMeBoxTemplate.applyParams(hitMeBoxParams())
    }
    fun getHitThemBox(): HitThemBox {
        return hitThemBoxTemplate.applyParams(hitThemBoxParams())
    }
}



