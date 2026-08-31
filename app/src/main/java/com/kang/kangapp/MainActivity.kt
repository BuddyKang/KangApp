package com.kang.kangapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kang.kangapp.ui.KangAppRoot
import com.kang.kangapp.ui.theme.KangAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KangAppTheme {
                KangAppRoot()
            }
        }
    }
}
