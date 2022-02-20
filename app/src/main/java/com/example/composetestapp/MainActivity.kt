package com.example.composetestapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotateRad
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.example.composetestapp.GameInteractor.Companion.EMPTY_GAME_STATE
import com.example.composetestapp.engine.*
import com.example.composetestapp.engine.systems.moving.MovingObjectParams
import com.example.composetestapp.engine.traits_without_systems.type.ObjectType
import kotlin.math.atan2

@ExperimentalComposeUiApi
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var greeting: ComposeView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        greeting = findViewById<ComposeView>(R.id.greeting)
        greeting.setContent {
            instagramIcon(viewModel)
        }
    }

    override fun onResume() {
        super.onResume()
        greeting.post {
            viewModel.startGame(
                GameField(
                    width = greeting.width,
                    height = greeting.height
                )
            )
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pauseGame()
    }

    @ExperimentalComposeUiApi
    @Composable
    fun instagramIcon(mainViewModel: MainViewModel) {
        val gameState = mainViewModel.gameState.collectAsState(EMPTY_GAME_STATE)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.DarkGray)
                .pointerInteropFilter {
                    when (it.action) {
                        MotionEvent.ACTION_DOWN -> { viewModel.onTouchCoordsChanged(Coords(it.x, it.y)) }
                        MotionEvent.ACTION_MOVE -> { viewModel.onTouchCoordsChanged(Coords(it.x, it.y)) }
                        MotionEvent.ACTION_UP -> { viewModel.onTouchCoordsChanged(null) }
                        else -> false
                    }
                    true
                }
        ) {
            gameState.value.moveVisualEffects.forEach { moveVisualEffect ->
                rotateRad(
                    radians = atan2(-moveVisualEffect.velocity.second,-moveVisualEffect.velocity.first).toFloat(),
                    pivot = moveVisualEffect.coords.toOffset()
                ) {
                    val maxLen = (moveVisualEffect.velocity.length()*moveVisualEffect.progress*100).toFloat()
                    drawFireArcEffect(
                        center = (moveVisualEffect.coords + (10.0 to -5.0)*3).toOffset(),
                        maxLen = maxLen,
                        progress = moveVisualEffect.progress
                    )
                    drawFireArcEffect(
                        center = (moveVisualEffect.coords + (10.0 to 5.0)*3).toOffset(),
                        maxLen = maxLen,
                        progress = moveVisualEffect.progress
                    )
                }
            }
            gameState.value.movingObjectParamsToTypes.forEach { movingObjectParamsToType ->
                val (movingObjectParams, type) = movingObjectParamsToType
                when (type) {
                    is ObjectType.Spaceship -> drawSpaceship(
                        movingObjectParams = movingObjectParams
                    )
                    is ObjectType.Coin -> drawCoin(
                        movingObjectParams = movingObjectParams,
                        coin = type
                    )
                    is ObjectType.Asteroid -> drawAsteroid(
                        movingObjectParams = movingObjectParams,
                        asteroid = type
                    )
                }
            }
        }
    }
}

fun DrawScope.drawFireArcEffect(center: Offset, maxLen: Float, progress: Float) {
    drawArc(
        color = Color.Yellow,
        topLeft = center.copy(x = center.x - maxLen, y = center.y - maxLen),
        startAngle = 22f,
        sweepAngle = -44f,
        useCenter = true,
        size = Size(maxLen, maxLen),
        alpha = 1f - progress,
    )
}

fun DrawScope.drawSpaceship(movingObjectParams: MovingObjectParams) {
    rotateRad(
        radians = atan2(movingObjectParams.direction.second,movingObjectParams.direction.first).toFloat(),
        pivot = movingObjectParams.coords.toOffset()
    ) {
        drawLine(
            color = Color.Red,
            start = (movingObjectParams.coords - (10.0 to -5.0)*3).toOffset(),
            end = (movingObjectParams.coords + (10.0 to 0.0)*3).toOffset(),
            strokeWidth = 5.0f
        )
        drawLine(
            color = Color.Red,
            start = (movingObjectParams.coords + (10.0 to 0.0)*3).toOffset(),
            end = (movingObjectParams.coords - (10.0 to 5.0)*3).toOffset(),
            strokeWidth = 5.0f
        )
    }
}

fun DrawScope.drawCoin(movingObjectParams: MovingObjectParams, coin: ObjectType.Coin) {
    drawCircle(
        color = Color.Blue,
        center = movingObjectParams.coords.toOffset(),
        radius = coin.radius.toFloat() // TODO: move to render system
    )
}

fun DrawScope.drawAsteroid(movingObjectParams: MovingObjectParams, asteroid: ObjectType.Asteroid) {
    drawCircle(
        color = Color.Green,
        center = movingObjectParams.coords.toOffset(),
        radius = asteroid.radius.toFloat()
    )
}

fun Coords.toOffset(): Offset {
    return Offset(first.toFloat(), second.toFloat())
}