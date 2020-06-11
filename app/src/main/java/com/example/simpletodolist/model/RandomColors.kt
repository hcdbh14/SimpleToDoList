package com.example.simpletodolist.model

class RandomColors {

    private val colors = listOf(
        0xff1a1334,
        0xff26294a,
        0xff01545a,
        0xff017351,
        0xff03c383,
        0xffaad962,
        0xfffbbf45,
        0xffef6a32,
        0xffed0345,
        0xffa12a5e,
        0xff710162,
        0xff022c7d
    )

    fun getRandomColor() : Long {
        return colors.random()
    }
}