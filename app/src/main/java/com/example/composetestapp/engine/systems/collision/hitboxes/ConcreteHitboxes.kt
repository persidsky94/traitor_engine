package com.example.composetestapp.engine.systems.collision.hitboxes

import com.example.composetestapp.engine.Coords
import com.example.composetestapp.engine.ZERO_COORDS
import com.example.composetestapp.engine.ZeroAsDouble


class CircleHitboxTemplate(
    val radius: Double
): HitMeBoxTemplate<Coords, CircleHitbox>,
    HitThemBoxTemplate<Coords, CircleHitbox>
{
    override val maxDistanceToHit
        get() = radius

    override val minDistanceToHit
        get() = ZeroAsDouble

    override fun applyParams(params: Coords): CircleHitbox {
        return CircleHitbox(radius = radius, center = params)
    }
}

class CircleHitbox(
    val radius: Double,
    val center: Coords
): HitMeBox, HitThemBox {
    override val hitMeType = HitMeBox.Type.Circle(radius = radius, center = center)
    override val hitThemType = HitThemBox.Type.Circle(radius = radius, center = center)
    override val coords = center
    override val diameter = radius
}

object NoHitboxTemplate : HitMeBoxTemplate<Unit, NoHitbox>, HitThemBoxTemplate<Unit, NoHitbox> {
    override val maxDistanceToHit = ZeroAsDouble
    override val minDistanceToHit = ZeroAsDouble

    override fun applyParams(params: Unit): NoHitbox {
        return NoHitbox()
    }
}

class NoHitbox: HitMeBox, HitThemBox {
    override val hitMeType = HitMeBox.Type.None
    override val hitThemType = HitThemBox.Type.None
    override val coords = ZERO_COORDS
    override val diameter = ZeroAsDouble
}