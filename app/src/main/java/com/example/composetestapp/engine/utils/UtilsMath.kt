package com.example.composetestapp.engine

import kotlin.math.pow
import kotlin.math.sqrt

fun <T : Comparable<T>> rangesIntersect(
    firstRange: ClosedFloatingPointRange<T>,
    secondRange: ClosedFloatingPointRange<T>
): Boolean {
    return firstRange.contains(secondRange.start) ||
            firstRange.contains(secondRange.endInclusive) ||
            secondRange.contains(firstRange.start) ||
            secondRange.contains(firstRange.endInclusive)
}

fun rangeFromMinToMax(a: Double, b: Double): ClosedFloatingPointRange<Double> {
    return if (a <= b) a.rangeTo(b) else b.rangeTo(a)
}

fun distance(firstPoint: Coords, secondPoint: Coords): Double {
    return sqrt(
        (firstPoint.first - secondPoint.first).pow(2) + (firstPoint.second - secondPoint.second).pow(
            2
        )
    )
}