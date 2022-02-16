package com.example.composetestapp.engine.systems.collision.hitboxes

import com.example.composetestapp.engine.Coords

interface HitMeBox {
    val hitMeType: Type
    val coords: Coords
    val diameter: Double

    sealed class Type {
        object None: Type()
        class Circle(val radius: Double, val center: Coords): Type()
        class ConvexPolygon(val vertices: List<Coords>): Type()
    }
}

interface HitThemBox {
    val hitThemType: Type
    val coords: Coords
    val diameter: Double

    sealed class Type {
        object None: Type()
        class Circle(val radius: Double, val center: Coords): Type()
        class Segments(val segments: List<Segment>): Type()
    }
}

interface HitMeBoxTemplate<Params, ConcreteHitMeBox: HitMeBox> {
    val maxDistanceToHit: Double
    val minDistanceToHit: Double
    fun applyParams(params: Params): ConcreteHitMeBox
}

interface HitThemBoxTemplate<Params, ConcreteHitThemBox: HitThemBox> {
    val maxDistanceToHit: Double
    val minDistanceToHit: Double
    fun applyParams(params: Params): ConcreteHitThemBox
}


class Segment(
    val start: Coords,
    val end: Coords
)