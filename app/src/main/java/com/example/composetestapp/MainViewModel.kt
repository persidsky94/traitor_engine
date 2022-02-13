package com.example.composetestapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val gameInteractor = GameInteractor()

    val gameState: Flow<GameState> =
        gameInteractor.gameState


    fun onTouchCoordsChanged(touchCoords: Coords?) {
        gameInteractor.setCurrentTouchCoords(touchCoords)
    }

    fun startGame() {
        viewModelScope.launch {
            gameInteractor.startEngine()
        }
    }

    fun pauseGame() {
        gameInteractor.stopEngine()
    }
}

data class GameState(
    val movingObjectParamsToTypes: List<Pair<MovingObjectParams, ObjectType>>,
    val moveVisualEffects: List<MoveVisualEffect>
)

data class MoveVisualEffect(
    val coords: Coords,
    val velocity: Vector,
    val progress: Float
)

data class RawMoveVisualEffect(
    val coords: Coords,
    val velocity: Vector
)