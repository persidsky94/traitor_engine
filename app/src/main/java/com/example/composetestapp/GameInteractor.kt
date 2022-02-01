package com.example.composetestapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import kotlin.math.absoluteValue

interface IGameInteractor {
    fun setCurrentTouchCoords(coords: Coords?)
    suspend fun startEngine()
    fun stopEngine()
    val gameState: Flow<GameState>
}


class GameInteractor: IGameInteractor {
    private val playerTouchController = ForceTouchController()
    private val movingEngine = setupGameEngine(playerTouchController)
    private val gameTimer: ITimer = Timer()
    private var gameJob: Job? = null

    override fun setCurrentTouchCoords(coords: Coords?) {
        playerTouchController.touchCoordinates = coords
    }

    private var timestampsToVisualEffects: MutableList<Pair<VisualEffectProgress, List<RawMoveVisualEffect>>> = mutableListOf()
    private fun removeExpiredVisualEffects(currentTime: Long, expirationTime: Long) {
        timestampsToVisualEffects.removeAll { (progressInfo, _) -> currentTime - progressInfo.timeStart > expirationTime }
    }
    private fun updateVisualEffectsProgress(currentTime: Long) {
        timestampsToVisualEffects.forEach {
            val newProgress = (currentTime - it.first.timeStart).toFloat()/VISUAL_EFFECT_EXPIRATION_TIME_MS
            it.first.progress = newProgress
        }
    }
    private val activeVisualEffects: List<MoveVisualEffect>
        get() = timestampsToVisualEffects.fold(emptyList()) { accList, moreEffects ->
            accList + moreEffects.second.map { it.toMoveVisualEffect(moreEffects.first.progress)}
        }

    override suspend fun startEngine() {
        gameTimer.startTimer()
        coroutineScope {
            gameJob = launch(Dispatchers.Default) {
               while (true) {
                   sleep(GAME_TICK_MS)
                   movingEngine.update(gameTimer.timeSinceLastRequestMs())
                   val currentTime = gameTimer.currentTime()
                   removeExpiredVisualEffects(
                       currentTime = currentTime,
                       expirationTime = VISUAL_EFFECT_EXPIRATION_TIME_MS
                   )
                   updateVisualEffectsProgress(currentTime = currentTime)
                   val movingObjectParams = movingEngine.movingObjectsParams
                   val newVisualEffects = movingObjectParams
                       .filter { it.velocity.first.absoluteValue > 0.01 || it.velocity.second.absoluteValue > 0.01 }
                       .map { it.toRawMoveVisualEffect() }
                   timestampsToVisualEffects.add(VisualEffectProgress(currentTime, 0f) to newVisualEffects)
                   gameState.value = GameState(
                       movingObjectParams = movingObjectParams,
                       moveVisualEffects = activeVisualEffects
                   )
               }
            }
        }
    }

    override fun stopEngine() {
        gameJob?.cancel()
        gameJob = null
        gameTimer.stopTimer()
    }

    override val gameState: MutableStateFlow<GameState> = MutableStateFlow(EMPTY_GAME_STATE)

    companion object {
        val EMPTY_GAME_STATE = GameState(
            movingObjectParams = emptyList(),
            moveVisualEffects = emptyList()
        )

        const val VISUAL_EFFECT_EXPIRATION_TIME_MS = 1000L
    }
}

fun MovingObjectParams.toRawMoveVisualEffect(): RawMoveVisualEffect =
    RawMoveVisualEffect(
        coords = coords,
        velocity = velocity
    )

class VisualEffectProgress(
    val timeStart: Long,
    var progress: Float
)

fun RawMoveVisualEffect.toMoveVisualEffect(progress: Float): MoveVisualEffect {
    return MoveVisualEffect(
        coords = coords,
        velocity = velocity,
        progress = progress
    )
}


interface ITimer {
    fun startTimer()
    fun stopTimer()
    fun timeSinceLastRequestMs(): Long
    fun currentTime(): Long
}

class Timer: ITimer {
    override fun currentTime(): Long {
        return System.currentTimeMillis()
    }

    private var lastRequestTime = System.currentTimeMillis()

    override fun startTimer() {
        lastRequestTime = System.currentTimeMillis()
    }

    override fun stopTimer() {
        lastRequestTime = -1
    }

    override fun timeSinceLastRequestMs(): Long {
        return if (lastRequestTime == -1L) {
            0
        } else {
            val currentTime = System.currentTimeMillis()
            val timeElapsed = currentTime - lastRequestTime
            lastRequestTime = currentTime
            timeElapsed
        }
    }
}