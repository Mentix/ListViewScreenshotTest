package com.niv.test.Graphs

interface GridableGraph{

    val yTicksCount: Int
    val xTicksCount: Int

    val yTick: Float
    val xTick: Float

    // explanation for -1: the zero line is not adding any value so the lines that really matter for the calculation are the other lines
    val maxYValue: Float get() = (yTicksCount - 1) * yTick
    val maxXValue: Float get() = (xTicksCount - 1) * xTick

}