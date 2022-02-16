package com.example.composetestapp.engine.systems.collision.collision_detection

import com.example.composetestapp.engine.Coords
import com.example.composetestapp.engine.distance
import com.example.composetestapp.engine.systems.collision.hitboxes.HitMeBox
import com.example.composetestapp.engine.systems.collision.hitboxes.HitThemBox
import com.example.composetestapp.engine.systems.collision.hitboxes.Segment
import kotlin.math.pow

class CollisionDetectorImpl: CollisionDetector {
    override fun detectCollision(hitMeBox: HitMeBox, hitThemBox: HitThemBox): Boolean {
        val hitMeBoxType = hitMeBox.hitMeType
        val hitThemBoxType = hitThemBox.hitThemType
        return when (hitMeBoxType) {
            is HitMeBox.Type.None -> {
                false
            }
            is HitMeBox.Type.Circle -> {
                when (hitThemBoxType) {
                    is HitThemBox.Type.None -> false
                    is HitThemBox.Type.Circle -> circleIntersectCircle(
                        hitThemCircle = hitThemBoxType,
                        hitMeCircle = hitMeBoxType
                    )
                    is HitThemBox.Type.Segments -> segmentsIntersectCircle(
                        hitThemSegments = hitThemBoxType,
                        hitMeCircle = hitMeBoxType
                    )
                }
            }
            is HitMeBox.Type.ConvexPolygon -> {
                when (hitThemBoxType) {
                    is HitThemBox.Type.None -> false
                    is HitThemBox.Type.Circle -> circleIntersectPolygon(
                        hitThemCircle = hitThemBoxType,
                        hitMePolygon = hitMeBoxType
                    )
                    is HitThemBox.Type.Segments -> segmentsIntersectPolygon(
                        hitThemSegments = hitThemBoxType,
                        hitMePolygon = hitMeBoxType
                    )
                }
            }
        }
    }

    private fun circleIntersectCircle(
        hitThemCircle: HitThemBox.Type.Circle,
        hitMeCircle: HitMeBox.Type.Circle
    ): Boolean {
        return distance(hitThemCircle.center, hitMeCircle.center) < hitThemCircle.radius + hitMeCircle.radius
    }

    private fun circleIntersectPolygon(
        hitThemCircle: HitThemBox.Type.Circle,
        hitMePolygon: HitMeBox.Type.ConvexPolygon
    ): Boolean {
        if (hitMePolygon.vertices.size < 3) return false
        val nearestVertex = hitMePolygon.vertices.minByOrNull { distance(it, hitThemCircle.center) }!!
        if (distance(nearestVertex, hitThemCircle.center) < hitThemCircle.radius) return true
        val nearestIndex = hitMePolygon.vertices.indexOf(nearestVertex)
        val numVertices = hitMePolygon.vertices.size
        val (nextIndex, prevIndex) = (nearestIndex + 1)%numVertices to (nearestIndex - 1)%numVertices
        val nearestSegments = listOf(
            Segment(nearestVertex, hitMePolygon.vertices[nextIndex]),
            Segment(hitMePolygon.vertices[prevIndex], nearestVertex)
        )
        return nearestSegments.any { segmentIntersectCircle(it, hitThemCircle.center, hitThemCircle.radius) }
    }

    private fun segmentsIntersectCircle(
        hitThemSegments: HitThemBox.Type.Segments,
        hitMeCircle: HitMeBox.Type.Circle
    ): Boolean {
        return hitThemSegments.segments.any { segmentIntersectCircle(it, hitMeCircle.center, hitMeCircle.radius)}
    }

    private fun segmentsIntersectPolygon(
        hitThemSegments: HitThemBox.Type.Segments,
        hitMePolygon: HitMeBox.Type.ConvexPolygon
    ): Boolean {
        return hitThemSegments.segments.any { segmentIntersectPolygon(it, hitMePolygon) }
    }

    private fun segmentIntersectPolygon(
        segment: Segment,
        hitMePolygon: HitMeBox.Type.ConvexPolygon
    ): Boolean {
        val numVertices = hitMePolygon.vertices.size
        val polygonSegments = hitMePolygon.vertices.mapIndexed { i, vertice ->
            Segment(vertice, hitMePolygon.vertices[(i+1)%numVertices])
        }
        return polygonSegments.any { segmentIntersectSegment(it, segment) }
    }


    private fun segmentIntersectSegment(s1: Segment, s2: Segment): Boolean {
        return SegmentAsLinePart.from(s1).intersect(SegmentAsLinePart.from(s2))
    }

    //https://stackoverflow.com/questions/30844482/what-is-most-efficient-way-to-find-the-intersection-of-a-line-and-a-circle-in-py
    fun segmentIntersectCircle(
        segment: Segment,
        circleCenter: Coords,
        circleRadius: Double
    ): Boolean {
        val (x1, y1) = segment.start.first - circleCenter.first to segment.start.second - circleCenter.second
        val (x2, y2) = segment.end.first - circleCenter.first to segment.end.second - circleCenter.second
        val (dx, dy) = x2 - x1 to y2 - y1
        val dr = (dx.pow(2) + dy.pow(2)).pow(0.5)
        val bigD = x1*y2 - x2*y1
        val discriminant = circleRadius.pow(2)*dr.pow(2) - bigD.pow(2)
        return discriminant >= 0
    }

}
