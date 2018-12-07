package com.niv.test.Graphs

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF

fun Canvas.drawLine(startingPoint: PointF, endingPoint: PointF, paint: Paint){
    this.drawLine(startingPoint.x, startingPoint.y, endingPoint.x, endingPoint.y, paint)
}

fun Canvas.drawText(text: String, paint: Paint, point: PointF){
    this.drawText(text, point.x, point.y, paint)
}

fun Canvas.drawCircle(point: PointF, radius: Float, paint: Paint){
    this.drawCircle(point.x, point.y, radius, paint)
}
