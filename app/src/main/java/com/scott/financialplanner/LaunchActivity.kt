package com.scott.financialplanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Greeting(name = "me")
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!",
        modifier = Modifier
            .padding(24.dp)
            .width(50.dp)
            .height(60.dp)
            .clickable { })
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Greeting(name = "Phillip")
}