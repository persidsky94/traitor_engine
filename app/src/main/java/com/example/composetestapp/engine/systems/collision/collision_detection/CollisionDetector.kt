package com.example.composetestapp.engine.systems.collision.collision_detection

import com.example.composetestapp.engine.rangeFromMinToMax
import com.example.composetestapp.engine.rangesIntersect
import com.example.composetestapp.engine.systems.collision.hitboxes.HitMeBox
import com.example.composetestapp.engine.systems.collision.hitboxes.HitThemBox
import com.example.composetestapp.engine.systems.collision.hitboxes.Segment

interface CollisionDetector {
    fun detectCollision(hitMeBox: HitMeBox, hitThemBox: HitThemBox): Boolean
}

sealed class SegmentAsLinePart {
    abstract fun intersect(segmentAsLinePart: SegmentAsLinePart): Boolean


    class Vertical(val x: Double, val yRange: ClosedFloatingPointRange<Double>): SegmentAsLinePart() {
        override fun intersect(segmentAsLinePart: SegmentAsLinePart): Boolean {
            when (segmentAsLinePart) {
                is Vertical -> {
                    return x == segmentAsLinePart.x && rangesIntersect(yRange, segmentAsLinePart.yRange)
                }
                is NotVertical -> {
                    val yIntersection = segmentAsLinePart.y(x)
                    return yRange.contains(yIntersection) && segmentAsLinePart.xRange.contains(x)
                }
            }
        }
    }
    // y=kx+b
    class NotVertical(val k: Double, val b: Double, val xRange: ClosedFloatingPointRange<Double>): SegmentAsLinePart() {
        fun y(x: Double) = k*x+b

        override fun intersect(segmentAsLinePart: SegmentAsLinePart): Boolean {
            return when (segmentAsLinePart) {
                is Vertical ->  {
                    segmentAsLinePart.intersect(this)
                }
                is NotVertical -> {
                    when {
                        k == segmentAsLinePart.k && b == segmentAsLinePart.b -> {
                            rangesIntersect(xRange, segmentAsLinePart.xRange)
                        }
                        k == segmentAsLinePart.k -> {
                            false
                        }
                        else -> {
                            val xIntersection = (segmentAsLinePart.b - b)/(k-segmentAsLinePart.k)
                            xRange.contains(xIntersection) && segmentAsLinePart.xRange.contains(xIntersection)
                        }
                    }
                }
            }
        }
    }


    companion object {
        fun from(segment: Segment): SegmentAsLinePart {
            return if (segment.start.first == segment.end.first) {
                Vertical(
                    x = segment.start.first,
                    yRange = rangeFromMinToMax(segment.start.second, segment.end.second)
                )
            } else {
                val k = (segment.end.second - segment.start.second)/(segment.end.first - segment.start.first)
                val b = segment.start.second - segment.start.first*k
                NotVertical(
                    k = k,
                    b = b,
                    xRange = rangeFromMinToMax(segment.start.first, segment.end.first)
                )
            }
        }
    }
}