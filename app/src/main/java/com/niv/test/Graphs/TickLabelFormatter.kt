package com.niv.test.Graphs

interface TickLabelFormatter{
    fun getFormattedTick(tickValue: Float, tickPosition: Int): String
}