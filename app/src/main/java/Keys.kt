package com.darothub.galleria
object Keys {

    init {
        System.loadLibrary("native-lib")
    }

    external fun token(): String
}