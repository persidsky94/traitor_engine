package com.example.composetestapp

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class Engine {
}

fun setupGameEngine(forceTouchController: ForceTouchController): MoveEngine {
    val forceEngine = ForceEngineImpl()
    val moveEngine = MoveEngineImpl(forceEngine)
    val player = MovingFrictingObject(
        startCoords = Coords(10.0, 10.0),
        startVelocity = ZERO_VECTOR,
        friction = BASE_FRICTION
    )

    listOf(
        player,
        createRandomFrictingObject(96),
        createRandomFrictingObject(97),
        createRandomFrictingObject(98),
        createRandomFrictingObject(99)
    ).forEach { movingObject ->
        forceEngine.addObjectController(movingObject.objectId, forceTouchController)
        moveEngine.addMovingObject(movingObject)
    }

    return moveEngine
}

val random = Random(1)
fun createRandomFrictingObject(friction: Int): MovingObject {
    return MovingFrictingObject(
        startCoords = Coords(
            random.nextDouble(300.0),
            random.nextDouble(300.0)
        ),
        startVelocity = ZERO_VECTOR,
        friction = friction
    )
}


interface MoveEngine {
    suspend fun update(deltaT: Long)
    fun addMovingObject(movingObject: MovingObject)
    val movingObjectsParams: List<MovingObjectParams>
}

class MoveEngineImpl(
    val forceEngine: ForceEngine
): MoveEngine {
    private val movingObjects = mutableListOf<MovingObject>()
    override val movingObjectsParams: List<MovingObjectParams>
        get() = movingObjects.map { it.params }

    override fun addMovingObject(movingObject: MovingObject) {
        movingObjects.add(movingObject)
    }

    override suspend fun update(deltaT: Long) {
        movingObjects.forEachParallel { movingObject ->
            val force = forceEngine.forceForObject(movingObject)
            movingObject.update(deltaT = deltaT, force = force)
        }
    }
}

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
        val (coordinates, vel) = formula(_coords, _velocity, deltaT, force)
        _coords = coordinates
        _velocity = vel
    }
}

class MovingFrictingObject(
    startCoords: Coords = ZERO_COORDS,
    startVelocity: Vector = ZERO_VECTOR,
    friction: Int = BASE_FRICTION
): MovingObjectImpl(
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