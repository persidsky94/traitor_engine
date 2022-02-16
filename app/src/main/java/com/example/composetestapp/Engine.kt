package com.example.composetestapp

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.math.*
import kotlin.random.Random

fun setupGameEngine(forceTouchController: ForceTouchController, gameField: GameField): Pair<MoveEngine, CollisionEngine> {
    val forceEngine = ForceEngineImpl()
    val moveEngine = MoveEngineImpl(forceEngine)

    val collisionHandler = CollisionHandlerImpl()
    val collisionEngine = CollisionEngineImpl(
        collisionDetector = CollisionDetectorImpl(),
        collisionHandler = collisionHandler
    )

    val removeObjectMediator = RemoveObjectMediatorImpl(moveEngine, collisionEngine)
    var coinCreator: (()->Unit)? = null
    val coinOnCollisionRemover = CoinCollisionHandler(objectRemover = removeObjectMediator)
        .with { hittedObj, hittingObj ->
            coinCreator?.invoke()
        }
    coinCreator = {
        val coin = createRandomMovingCoin(gameField)
        collisionEngine.addCollidableObject(coin)
        collisionHandler.addCollisionHandlerForHittedObject(coin, coinOnCollisionRemover)
        if (coin is MovingObject) {
            moveEngine.addMovingObject(coin)
        }
    }
    val asteroidOnCollisionSpaceshipDestroyer = AsteroidCollisionHandler(objectRemover = removeObjectMediator)

    val spaceships = buildList(5) { createRandomFrictingSpaceship(gameField, random.nextInt(from = 95, until = 99)) }
    val coins = buildList(6) { createRandomMovingCoin(gameField) }
    val asteroids = buildList(3) { createRandomAsteroid(gameField) }

    spaceships.forEach { spaceship ->
        forceEngine.addObjectController(spaceship.objectId, forceTouchController)
        moveEngine.addMovingObject(spaceship)
        collisionEngine.addCollidableObject(spaceship)
    }

    coins.forEach { coin ->
        collisionEngine.addCollidableObject(coin)
        collisionHandler.addCollisionHandlerForHittedObject(coin, coinOnCollisionRemover)
        if (coin is MovingObject) {
            moveEngine.addMovingObject(coin)
        }
    }

    asteroids.forEach { asteroid ->
        collisionEngine.addCollidableObject(asteroid)
        collisionHandler.addCollisionHandlerForHittedObject(asteroid, asteroidOnCollisionSpaceshipDestroyer)
        moveEngine.addMovingObject(asteroid)
    }

    return moveEngine to collisionEngine
}

data class GameField(
    val width: Int,
    val height: Int
)

fun repeat(times: Int, action: (() -> Unit)) {
    if (times >= 1) {
        for (i in 1..times) {
            action()
        }
    }
}

fun <T> buildList(times: Int, createElement: (() -> T)): List<T> {
    val result = mutableListOf<T>()
    repeat(times) { result.add(createElement()) }
    return result
}

class FrictingSpaceship(
    startCoords: Coords = ZERO_COORDS,
    startVelocity: Vector = ZERO_VECTOR,
    friction: Int = BASE_FRICTION
): MovingFrictingObject(
    objectType = ObjectType.Spaceship(),
    startCoords = startCoords,
    startVelocity = startVelocity,
    friction = friction
), CollidableObject<Unit, Coords> {
    override val coords: Coords
        get() = params.coords

    override val hitMeBoxTemplate = NoHitboxTemplate
    override fun hitMeBoxParams() = Unit

    override val hitThemBoxTemplate = CircleHitboxTemplate(radius = 2.0)
    override fun hitThemBoxParams(): Coords {
        return params.coords
    }
}

typealias MoveFormulaType = (Coords, Vector, Long, Vector) -> MovingObjectParams

val ignoreForceFormula: MoveFormulaType = { coords, velocity, deltaT, _ ->
    MovingObjectParams(
        coords = coords + velocity*deltaT,
        velocity = velocity,
        direction = velocity
    )
}

fun verticalSinCoordinateFormula(startingCoords: Coords, maxDeviation: Double): MoveFormulaType {
    val totalTime = TotalTime(0.0)
    val periodInSeconds = random.nextDouble(from=0.5, until=3.0)
    val deviation = random.nextDouble(from=maxDeviation/2, until = maxDeviation)
    val formula: MoveFormulaType = { coords, velocity, deltaT, _ ->
        totalTime.seconds += deltaT/1000.0
        val arg = totalTime.seconds*PI/periodInSeconds
        val newVelocity = Vector(0.0, cos(arg)*deviation)
        MovingObjectParams(
            coords = startingCoords + Coords(0.0, sin(arg)*deviation),
            velocity = newVelocity,
            direction = newVelocity
        )
    }
    return formula
}

class TotalTime(var seconds: Double)

val random = Random(1)
fun createRandomFrictingSpaceship(gameField: GameField, friction: Int): FrictingSpaceship {
    return FrictingSpaceship(
        startCoords = Coords(
            random.nextDouble(gameField.width.toDouble()),
            random.nextDouble(gameField.height.toDouble())
        ),
        startVelocity = ZERO_VECTOR,
        friction = friction
    )
}

fun createRandomCoin(): CoinObject {
    return CoinObject(
        startCoords = Coords(
            random.nextDouble(1000.0),
            random.nextDouble(1000.0)
        ),
        startVelocity = ZERO_VECTOR,
        formula = ignoreForceFormula,
        radius = 30.0
    )
}

fun createRandomMovingCoin(gameField: GameField, coinRadius: Double = 30.0, maxDeviation: Double = 50.0): CoinObject {
    val startCoords = Coords(
        random.nextDouble(gameField.width.toDouble()),
        random.nextDouble(gameField.height.toDouble())
    )
    return CoinObject(
        startCoords = startCoords,
        startVelocity = ZERO_VECTOR,
        formula = verticalSinCoordinateFormula(startingCoords = startCoords, maxDeviation = maxDeviation),
        radius = coinRadius
    )
}

fun createRandomAsteroid(
    gameField: GameField,
    asteroidRadius: Double = 20.0,
    maxVelocityCoordinate: Double = 0.1
): AsteroidObject {
    return AsteroidObject(
        startCoords = Coords(
            random.nextDouble(gameField.width.toDouble()),
            random.nextDouble(gameField.height.toDouble())
        ),
        startVelocity = Vector(
            random.nextDouble(from = -maxVelocityCoordinate, until = maxVelocityCoordinate),
            random.nextDouble(from = -maxVelocityCoordinate, until = maxVelocityCoordinate)
        ),
        formula = ignoreForceFormula,
        radius = asteroidRadius
    )
}


interface MoveEngine {
    suspend fun update(deltaT: Long)
    fun addMovingObject(movingObject: MovingObject)
    fun removeMovingObject(movingObject: MovingObject)
    fun removeObjectById(id: ObjId)
    val movingObjectsParamsToTypes: List<Pair<MovingObjectParams, ObjectType>>
}

class MoveEngineImpl(
    val forceEngine: ForceEngine
): MoveEngine {
    private val movingObjects = mutableListOf<MovingObject>()
    override val movingObjectsParamsToTypes: List<Pair<MovingObjectParams, ObjectType>>
        get() = movingObjects.map { it.params to it.objectType}

    override fun addMovingObject(movingObject: MovingObject) {
        movingObjects.add(movingObject)
    }

    override fun removeMovingObject(movingObject: MovingObject) {
        movingObjects.remove(movingObject)
    }

    override fun removeObjectById(id: ObjId) {
        movingObjects.removeAll { it.objectId == id }
    }

    override suspend fun update(deltaT: Long) {
        movingObjects.forEachParallel { movingObject ->
            val force = forceEngine.forceForObject(movingObject)
            movingObject.update(deltaT = deltaT, force = force)
        }
    }
}


interface CollisionEngine {
    fun addCollidableObject(collidableObject: CollidableObject<*,*>)
    fun removeCollidableObject(collidableObject: CollidableObject<*, *>)
    fun removeObjectById(id: ObjId)
    fun detectAndHandleAllCollisions()
}

class CollisionEngineImpl(
    private val collisionDetector: CollisionDetector,
    private val collisionHandler: CollisionHandler
): CollisionEngine {
    //TODO: maybe use more effective data structure?
    private val collidableObjects: MutableList<CollidableObject<*,*>> = mutableListOf()

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

interface CollisionHandler {
    fun handleCollision(hittedObject: CollidableObject<*,*>, hittingObject: CollidableObject<*,*>)
}

class CollisionHandlerImpl: CollisionHandler {
    private val trackedHittedObjectIds: MutableMap<ObjId, CollisionHandler> = mutableMapOf()
    private val trackedHittingObjectIds: MutableMap<ObjId, CollisionHandler> = mutableMapOf()

    fun addCollisionHandlerForHittedObject(hittedObject: CollidableObject<*, *>, collisionHandler: CollisionHandler) {
        trackedHittedObjectIds[hittedObject.objectId] = collisionHandler
    }

    fun addCollisionHandlerForHittingObject(hittingObject: CollidableObject<*, *>, collisionHandler: CollisionHandler) {
        trackedHittingObjectIds[hittingObject.objectId] = collisionHandler
    }

    fun removeCollisionHandlerForObject(objId: ObjId) {
        trackedHittedObjectIds.remove(objId)
        trackedHittingObjectIds.remove(objId)
    }

    override fun handleCollision(
        hittedObject: CollidableObject<*, *>,
        hittingObject: CollidableObject<*, *>
    ) {
        trackedHittedObjectIds[hittedObject.objectId]?.handleCollision(hittedObject, hittingObject)
        trackedHittingObjectIds[hittingObject.objectId]?.handleCollision(hittedObject, hittingObject)
    }
}

interface RemoveObjectMediator {
    fun onRemoveObject(objId: ObjId)
}

class RemoveObjectMediatorImpl(
    private val moveEngine: MoveEngine,
    private val collisionEngine: CollisionEngine
): RemoveObjectMediator {
    override fun onRemoveObject(objId: ObjId) {
        moveEngine.removeObjectById(objId)
        collisionEngine.removeObjectById(objId)
    }
}

class CoinCollisionHandler(
    private val objectRemover: RemoveObjectMediator
): CollisionHandler {
    override fun handleCollision(
        hittedObject: CollidableObject<*, *>,
        hittingObject: CollidableObject<*, *>
    ) {
        objectRemover.onRemoveObject(hittedObject.objectId)
    }
}

class AsteroidCollisionHandler(
    private val objectRemover: RemoveObjectMediator
): CollisionHandler {
    override fun handleCollision(
        hittedObject: CollidableObject<*, *>,
        hittingObject: CollidableObject<*, *>
    ) {
        objectRemover.onRemoveObject(hittedObject.objectId)
        //TODO: should be handled by hitMeBox on spaceships
        if (hittingObject.objectType is ObjectType.Spaceship) {
            objectRemover.onRemoveObject(hittingObject.objectId)
        }
    }
}

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

interface CollisionDetector {
    fun detectCollision(hitMeBox: HitMeBox, hitThemBox: HitThemBox): Boolean
}

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
    return sqrt((firstPoint.first - secondPoint.first).pow(2) + (firstPoint.second - secondPoint.second).pow(2))
}

sealed class ObjectType {
    class Spaceship: ObjectType()
    class Coin(val radius: Double): ObjectType()
    class Asteroid(val radius: Double): ObjectType()
}

class CoinObject(
    startCoords: Coords,
    startVelocity: Vector,
    formula: (Coords, Vector, Long, Vector) -> MovingObjectParams,
    radius: Double
): CollidableCircle(
    objectType = ObjectType.Coin(radius),
    startCoords = startCoords,
    startVelocity = startVelocity,
    formula = formula,
    radius = radius
)

class AsteroidObject(
    startCoords: Coords,
    startVelocity: Vector,
    formula: (Coords, Vector, Long, Vector) -> MovingObjectParams,
    radius: Double
): CollidableCircle(
    objectType = ObjectType.Asteroid(radius),
    startCoords = startCoords,
    startVelocity = startVelocity,
    formula = formula,
    radius = radius
)

open class CollidableCircle(
    objectType: ObjectType,
    startCoords: Coords,
    startVelocity: Vector,
    formula: (Coords, Vector, Long, Vector) -> MovingObjectParams,
    radius: Double
): CollidableObject<Coords, Unit>,
    MovingObjectImpl(
        objectType = objectType,
        startCoords = startCoords,
        startVelocity = startVelocity,
        formula = formula
    )
{
    override val coords: Coords
        get() = params.coords

    override val hitMeBoxTemplate: HitMeBoxTemplate<Coords, *> = CircleHitboxTemplate(radius = radius)
    override fun hitMeBoxParams(): Coords {
        return coords
    }
    override val hitThemBoxTemplate: HitThemBoxTemplate<Unit, *> = NoHitboxTemplate
    override fun hitThemBoxParams() = Unit
}

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

class Segment(
    val start: Coords,
    val end: Coords
)
//interface HitboxParams

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

const val ZeroAsDouble = 0.toDouble()



interface ForceEngine {
    fun forceForObject(movingObject: MovingObject): Vector
}

class ForceEngineImpl: ForceEngine {
    private val objectIdsToControllers = mutableMapOf<ObjId, ForceEngine>()

    fun addObjectController(objectId: ObjId, forceEngine: ForceEngine) {
        objectIdsToControllers[objectId] = forceEngine
    }

    override fun forceForObject(movingObject: MovingObject): Vector {
        return objectIdsToControllers[movingObject.objectId]
            ?.forceForObject(movingObject)
            ?: ZERO_VECTOR
    }
}


class ForceTouchController: ForceEngine {
    var touchCoordinates: Coords? = null
    private fun getTouchCoords(): Coords? {
        return touchCoordinates // TODO: получать координаты тача
    }
    override fun forceForObject(movingObject: MovingObject): Vector {
        return (getTouchCoords()?.let { touchCoords ->
            (touchCoords - movingObject.params.coords).normalize()
        } ?: ZERO_VECTOR)*FORCE_QUOTIOENT
    }

    companion object {
        const val FORCE_QUOTIOENT = 1.0/240.0
    }
}

data class MovingObjectParams(
    val coords: Coords,
    val velocity: Vector,
    val direction: Vector
)

interface EngineObject {
    val objectId: ObjId
    val objectType: ObjectType

    companion object {
        var nextFreeId = 0
        fun generateNextObjId(): ObjId {
            return ++nextFreeId
        }
    }
}

interface MovingObject: EngineObject {
    fun update(deltaT: Long, force: Vector = ZERO_VECTOR)
    val params: MovingObjectParams
}

open class MovingObjectImpl(
    override val objectType: ObjectType,
    startCoords: Coords,
    startVelocity: Vector,
    private val formula: (Coords, Vector, Long, Vector) -> MovingObjectParams,
    override val objectId: ObjId = EngineObject.generateNextObjId()
): MovingObject {
    private var _coords: Coords = startCoords

    private var _velocity: Vector = startVelocity
    set(value) {
        field = value
        _direction = value
    }

    private var _direction: Vector = 1.0 to 0.0
    set(value) {
        if (value.first.absoluteValue > 0.01 || value.second.absoluteValue > 0.01) {
            field = value
        }
    }

    override val params: MovingObjectParams
        get() = MovingObjectParams(_coords, _velocity, _direction)

    override fun update(deltaT: Long, force: Vector) {
        val movingObjectParams = formula(_coords, _velocity, deltaT, force)
        _coords = movingObjectParams.coords
        _velocity = movingObjectParams.velocity
    }
}

open class MovingFrictingObject(
    objectType: ObjectType,
    startCoords: Coords = ZERO_COORDS,
    startVelocity: Vector = ZERO_VECTOR,
    friction: Int = BASE_FRICTION
): MovingObjectImpl(
    objectType = objectType,
    startCoords = startCoords,
    startVelocity = startVelocity,
    formula = { coords, velocity, deltaT, force ->
        val _coords = coords + velocity*deltaT
        val frictionQuotient = friction.toFloat()/100.0
        val ticks = deltaT.toDouble()/FRICTION_TICKS_MS
        val _velocity = velocity*frictionQuotient.pow(ticks) + force*deltaT

        MovingObjectParams(_coords, _velocity, _velocity)
    }
) {
    companion object {

        const val FRICTION_TICKS_MS = 1000.0/(60.0*1.0)
    }
}

typealias Vector = Pair<Double, Double>
typealias Coords = Pair<Double, Double>
typealias ObjId = Int
val ZERO_VECTOR = 0.0 to 0.0
val ZERO_COORDS = 0.0 to 0.0
val BASE_FRICTION = 95
const val GAME_TICK_MS = (1000.0/120.0).toLong()

fun Coords(first: Float, second: Float): Coords =
    first.toDouble() to second.toDouble()

operator fun Vector.plus(other: Vector) =
    first + other.first to second + other.second

operator fun Coords.minus(other: Coords) =
    first - other.first to second - other.second

operator fun Vector.times(times: Float): Vector =
    first*times to second*times

operator fun Vector.times(times: Double): Vector =
    first*times to second*times

operator fun Vector.times(times: Int): Vector = times(times.toFloat())
operator fun Vector.times(times: Long): Vector = times(times.toFloat())

operator fun Float.times(vector: Vector): Vector =
    this*vector.first to this*vector.second

fun Vector.normalize(): Vector {
    val length = length()
    return first/length to second/length
}

fun Vector.length(): Double = sqrt(first*first + second*second)

suspend fun <A, B> Iterable<A>.mapParallel(f: suspend (A) -> B): List<B> = coroutineScope {
    map { async { f(it) } }.awaitAll()
}

suspend fun <A> Iterable<A>.forEachParallel(f: suspend (A) -> Unit) = coroutineScope {
    map { async { f(it) } }.awaitAll()
}