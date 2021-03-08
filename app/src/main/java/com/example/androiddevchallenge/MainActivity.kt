/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    Surface(
        color = MaterialTheme.colors.background,
    ) {
        Card(
            elevation = 4.dp,
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
        ) {
            CountDownUI()
        }
    }
}

@Composable
fun CountDownUI() {
    var progress by remember { mutableStateOf(1f) }
    var timer: Long = 10_000
    var tempTimer by remember { mutableStateOf(timer) }
    var time by remember { mutableStateOf("00h 00m ${
        TimeUnit.MILLISECONDS
            .toSeconds(timer)
            .toString()
            .padStart(2, '0')
    }s")}
    val animatedColor by animateColorAsState(
        targetValue = when {
            progress < 0.3f -> purple200
            progress < 0.6f -> purple500
            progress < 0.9f -> purple700
            else -> MaterialTheme.colors.onBackground
        }
    )
    var changeClockCountDown by remember { mutableStateOf(false) }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .wrapContentHeight()
                .padding(bottom = 16.dp)

        ) {
            CircularProgressIndicator(
                progress = 1f,
                strokeWidth = 2.dp,
                color = teal200,
                modifier = Modifier
                    .height(300.dp)
                    .width(300.dp)
            )
            CircularProgressIndicator(
                progress = progress,
                strokeWidth = 2.dp,
                color = animatedColor,
                modifier = Modifier.width(300.dp)
            )
            Row(
                modifier = Modifier
                    .height(300.dp)
                    .width(300.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!changeClockCountDown)
                    Text(
                        text = time,
                        style = typography.h3,
                        color = animatedColor
                    )
                else
                    TextField(
                        value = time,
                        onValueChange = { time = it },
                        textStyle = typography.h3,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    )
            }
        }

        Row(
        ) {
            var counterCancel by remember { mutableStateOf(false) }
            var counterPause by remember { mutableStateOf(false) }
            var counterStarted by remember { mutableStateOf(false) }

            val countDownTimer = object : CountDownTimer(tempTimer, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    if (counterPause) {
                        this.cancel()
                        tempTimer = millisUntilFinished
                    }

                    time = "00h 00m ${
                        TimeUnit.MILLISECONDS
                            .toSeconds(millisUntilFinished)
                            .toString()
                            .padStart(2, '0')
                    }s"
                    val percentageCompleted = millisUntilFinished / (timer / 100)
                    progress = percentageCompleted / 100f

                    if (counterCancel)
                        this.onFinish()
                }

                override fun onFinish() {
                    progress = 1f
                    time = "00h 00m ${
                        TimeUnit.MILLISECONDS
                            .toSeconds(timer)
                            .toString()
                            .padStart(2, '0')
                    }s"
                    tempTimer = timer
                    counterStarted = false
                }
            }


            if (!counterStarted){
                Button(onClick = {
                        progress = 0.0f
                        countDownTimer.start()
                        counterCancel = false
                        counterStarted = true
                        counterPause = false

                }) {
                    Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "")
                }
            } else {
                Button(onClick = {
                    counterStarted = false
                    counterPause = true }) {
                    Icon(imageVector = Icons.Filled.Pause, contentDescription = "")
                }
            }
            Spacer(modifier = Modifier.padding(8.dp))
            Button(onClick = {
                if(counterPause)
                    countDownTimer.onFinish()
                else {
                    counterPause = true
                    counterCancel = true
                }
            }) {
                Icon(imageVector = Icons.Filled.Stop, contentDescription = "")
            }
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
