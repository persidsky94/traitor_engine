package com.example.composetestapp.engine.systems.collision.trait

import com.example.composetestapp.engine.SystemId
import com.example.composetestapp.engine.traits_without_systems.type.ObjectType
import com.example.composetestapp.engine.objects.base.BaseTrait
import com.example.composetestapp.engine.systems.collision.hitboxes.HitMeBox
import com.example.composetestapp.engine.systems.collision.hitboxes.HitMeBoxTemplate
import com.example.composetestapp.engine.systems.collision.hitboxes.HitThemBox
import com.example.composetestapp.engine.systems.collision.hitboxes.HitThemBoxTemplate

interface CollidableTrait<HitMeBoxParams, HitThemBoxParams>: BaseTrait {
    fun objectType(): ObjectType // TODO: maybe no need? just handle via TypeSystem

    override val traitTypeId: SystemId
        get() = SystemId(COLLIDABLE_SYSTEM_ID)

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

    companion object {
        const val COLLIDABLE_SYSTEM_ID = "CollidableSystemId"
    }
}