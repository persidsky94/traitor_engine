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
import kotlin.math.atan2

@ExperimentalComposeUiApi
class MainActivity : AppCompatActivity() {
    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val greeting = findViewById<ComposeView>(R.id.greeting)
        greeting.setContent {
//            MdcTheme { // or AppCompatTheme
            instagramIcon(viewModel)
//            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.startGame()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pauseGame()
    }

//    @Preview
//    @Composable
//    private fun Greeting() {
//
//        Text(
//            text = stringResource(R.string.greetings),
//            style = MaterialTheme.typography.h5,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = dimensionResource(R.dimen.margin_small))
//                .wrapContentWidth(Alignment.CenterHorizontally)
//        )
//    }

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
                    drawFireArc(
                        center = (moveVisualEffect.coords + (10.0 to -5.0)*3).toOffset(),
                        maxLen = maxLen,
                        progress = moveVisualEffect.progress
                    )
                    drawFireArc(
                        center = (moveVisualEffect.coords + (10.0 to 5.0)*3).toOffset(),
                        maxLen = maxLen,
                        progress = moveVisualEffect.progress
                    )
//                    drawArc(
//                        color = Color.Yellow,
//                        topLeft = (moveVisualEffect.coords - (10.0 to -5.0)*3).toOffset(),
//                        startAngle = 22f,
//                        sweepAngle = -44f,
//                        useCenter = true,
//                        size = Size(maxLen, maxLen),
//                        alpha = 1f - moveVisualEffect.progress,
//                    )
                }
            }
            gameState.value.movingObjectParams.forEach { movingObjectParams ->
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
//            drawRoundRect(
//                brush = Brush.linearGradient(colors = instaColors),
//                cornerRadius = CornerRadius(60f, 60f),
//                style = Stroke(width = 15f, cap = StrokeCap.Round)
//            )
//            drawCircle(
//                brush = Brush.linearGradient(colors = instaColors),
//                radius = 45f,
//                style = Stroke(width = 15f, cap = StrokeCap.Round)
//            )
//            drawCircle(
//                brush = Brush.linearGradient(colors = instaColors),
//                radius = 13f,
//                center = Offset(this.size.width * .80f, this.size.height * 0.20f),
//            )
        }
    }
}

fun DrawScope.drawFireArc(center: Offset, maxLen: Float, progress: Float) {
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

fun Coords.toOffset(): Offset {
    return Offset(first.toFloat(), second.toFloat())
}