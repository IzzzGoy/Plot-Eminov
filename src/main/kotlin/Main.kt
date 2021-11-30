// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.objecthunter.exp4j.ExpressionBuilder
import space.kscience.plotly.*
import space.kscience.plotly.Plotly.plot
import kotlin.random.Random

@Composable
@Preview
fun App() {
    var exp by remember { mutableStateOf("") }
    var func by remember { mutableStateOf("") }
    var count by remember { mutableStateOf(1000) }
    var hist by remember { mutableStateOf(10) }

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(25.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = count.toString(),
                onValueChange = {
                    count = it.toIntOrNull() ?: count
                },
                label = {
                    Text(
                        text = "Random numbers count"
                    )
                },
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent)
            )
            TextField(
                value = exp,
                onValueChange = { str ->
                    exp = str
                },
                label = {
                    Text(
                        text = "Expression"
                    )
                },
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent)
            )
            TextField(
                value = func,
                onValueChange = { str ->
                    func = str
                },
                label = {
                    Text(
                        text = "Reverse"
                    )
                },
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent)
            )
            TextField(
                value = hist.toString(),
                onValueChange = {
                    hist = it.toInt()
                },
                label = {
                    Text(
                        text = "Hist count"
                    )
                },
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent)
            )
            TextButton(
                onClick = {
                    CoroutineScope(Dispatchers.Default).launch {
                        val random = Random(System.currentTimeMillis())
                        val mainFunc = ExpressionBuilder(exp).variable("x").build()
                        val xSeries = DoubleArray(count) {
                            random.nextDouble(0.0, 1.0)
                        }.sortedArray()
                        val reverseFunction = ExpressionBuilder(func).variable("r").build()
                        plot {
                            histogram {
                                this.nbinsx = hist
                                xbins.end = 0.95
                                x.numbers = xSeries.map { reverseFunction.setVariable("r", it).evaluate()}
                                name = "Random data"
                            }
                            trace {
                                x.doubles = xSeries
                                y.doubles = xSeries.map { mainFunc.setVariable("x", it).evaluate() * count / hist }.toDoubleArray()
                                name = "Value"
                            }
                            layout {
                                bargap = 0.1
                                title {
                                    text = "Basic Histogram"
                                    font {
                                        size = 20
                                        color("black")
                                    }
                                }
                                xaxis {
                                    title {
                                        text = "Value"
                                        font {
                                            size = 16
                                        }
                                    }
                                }
                                yaxis {
                                    title {
                                        text = "Count"
                                        font {
                                            size = 16
                                        }
                                    }
                                }
                            }
                        }.makeFile()

                    }
                }
            ) {
                Text("Calculate")
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
