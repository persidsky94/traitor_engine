package com.example.composetestapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetestapp.engine.Coords
import com.example.composetestapp.engine.GameField
import com.example.composetestapp.engine.traits_without_systems.type.ObjectType
import com.example.composetestapp.engine.Vector
import com.example.composetestapp.engine.systems.moving.MovingObjectParams
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val gameInteractor = GameInteractor()

    val gameState: Flow<GameState> =
        gameInteractor.gameState


    fun onTouchCoordsChanged(touchCoords: Coords?) {
        gameInteractor.setCurrentTouchCoords(touchCoords)
    }

    fun startGame(gameField: GameField) {
        viewModelScope.launch {
            gameInteractor.startEngine(gameField = gameField)
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