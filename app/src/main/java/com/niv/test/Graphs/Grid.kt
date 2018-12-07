package com.niv.test.Graphs

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class Grid : View {


    /** The total number of X ticks - also controls the number of vertical lines.
     * Default is 3 */
    private var xTicksCount = 3
    /** The total number of Y ticks- also controls the number of horizontal lines.
     * Default is 3 */
    private var yTicksCount = 3


    /** Determines whether to draw X axis ticks.
     * Default is true */
    var drawsXTicks = true

    /** Determines whether to draw Y axis ticks.
     * Default is true */
    var drawsYTicks = true


    /** Determines whether to draw the vertical lines.
     * Default is true */
    var drawsVerticalLines = true

    /** Determines whether to draw the horizontal lines.
     * Default is true */
    var drawsHorizontalLines = true


    /** The jump between each X value */
    private var xTick: Float = 0f

    /** The jump between each Y value */
    private var yTick: Float = 0f


    /** The formatter for the xTicks, default is null */
    var xTickFormatter: TickLabelFormatter? = null

    /** The formatter for the yTicks, default is null */
    var yTickFormatter: TickLabelFormatter? = null


    /** The color of the horizontal and vertical lines */
    var lineColor = Color.LTGRAY

    var tickTextColor = Color.DKGRAY

    /** The weight of the horizontal and vertical lines */
    var lineWeight = 1f


    /** The width of the Y axis,
     * set this to a positive number to get margin from the left side */
    var yAxisWidth = 0f

    /** The height of the X axis,
     * set this to a positive number to get margin from the bottom side,
     * the minimum value of it should be the tickTextSize, otherwise it may cause clipping.
     * Default is 30 */
    var xAxisHeight = 30f

    /** The text size of the ticks,
     * the maximum value should be the xAxisHeight, otherwise it may cause clipping.
     * Default is 30 */
    var tickTextSize = 30f


    /** The grid's rect is the frame where the vertical and horizontal lines are drawn-
    * that means it's the entire frame subtracted by the width of the Y axis (from the left side)
    * and the height of X axis (from the bottom side) */
    fun getGridRect(canvasWidth: Float, canvasHeight: Float) = RectF(0f, 0f, canvasWidth - yAxisWidth, canvasHeight - xAxisHeight)
    fun getGridRect(canvas: Canvas) = RectF(0f, 0f, canvas.width - yAxisWidth, canvas.height - xAxisHeight)

    private fun getTextWidth(text: String, textSize: Float): Int {
        val paint = Paint()
        paint.textSize = textSize

        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)

        return bounds.width()
    }

    private fun getXFormattedTickString(tick: Float, tickIndex: Int): String {

        if (xTickFormatter != null) return xTickFormatter!!.getFormattedTick(tick, tickIndex)

        if (tick % 1 == 0f) return tick.toInt().toString()

        return tick.toString()
    }

    private fun getYFormattedTickString(tick: Float, tickIndex: Int): String {

        if (yTickFormatter != null) return yTickFormatter!!.getFormattedTick(tick, tickIndex)

        if (tick % 1 == 0f) return tick.toInt().toString()

        return tick.toString()
    }


    /** Draws the horizontal lines */
    fun drawHorizontalLines(canvas: Canvas, linePaint: Paint) {
        if (yTicksCount < 2) return

        val yTicksCount = this.yTicksCount - 1

        val gridRect = getGridRect(canvas)

        for (i in 0..yTicksCount) {
            var lineYValue = gridRect.top + (yTicksCount - i) * (gridRect.height() / yTicksCount)

            if (i == 0) lineYValue -= lineWeight / 2
            else if (i == yTicksCount) lineYValue += lineWeight / 2

            val startingXValue = gridRect.left + lineWeight
            val endingXValue = gridRect.right

            val startingPoint = PointF(startingXValue, lineYValue)
            val endingPoint = PointF(endingXValue, startingPoint.y)

            canvas.drawLine(startingPoint, endingPoint, linePaint)
        }
    }

    /** Draws the vertical lines */
    fun drawVerticalLines(canvas: Canvas, linePaint: Paint) {
        if (xTicksCount < 2) return

        val xTicksCount = this.xTicksCount - 1

        val gridRect = getGridRect(canvas)

        for (i in 0..xTicksCount) {
            val xValue = i * (gridRect.width() / xTicksCount)

            val startingPoint = PointF(xValue, gridRect.bottom)
            val endingPoint = PointF(startingPoint.x, 0f)

            canvas.drawLine(startingPoint, endingPoint, linePaint)

        }

    }

    /** Draws the Y axis ticks */
    fun drawYTicks(canvas: Canvas, tickPaint: Paint) {
        if (yTicksCount < 2) return

        val yTicksCount = this.yTicksCount - 1

        val gridRect = getGridRect(canvas)

        for (i in 0..yTicksCount) {

            var lineYValue = gridRect.top + (yTicksCount - i) * (gridRect.height() / yTicksCount)

            if (i == 0) lineYValue -= lineWeight / 2
            else if (i == yTicksCount) lineYValue += lineWeight / 2

            val tickXValue = gridRect.left + lineWeight

            val startingPoint = PointF(tickXValue, lineYValue)

            if (i != 0) {
                val pointBeneathLine = PointF(startingPoint.x, startingPoint.y + lineWeight + tickTextSize)

                val currentTick = yTick * i
                val currentFormattedTick = getYFormattedTickString(currentTick, i)

                canvas.drawText(currentFormattedTick, tickPaint, pointBeneathLine)
            }
        }
    }

    /** Draws the X axis ticks */
    fun drawXTicks(canvas: Canvas, tickPaint: Paint) {
        if (xTicksCount < 2) return

        val xTicksCount = this.xTicksCount - 1

        val gridRect = getGridRect(canvas)

        for (i in 0..xTicksCount) {
            val xValue = i * (gridRect.width() / xTicksCount)

            val tickPoint = PointF(xValue, gridRect.bottom + tickPaint.textSize)

            val currentTick = xTick * i

            val formattedTick = getXFormattedTickString(currentTick, i)

            if (i == xTicksCount) {
                // on the right corner of the canvas.
                // we can't go off limits-
                // so we're drawing the text aligned in such way that the end of it will be the end of the canvas

                val formattedTickWidth = getTextWidth(formattedTick, tickTextSize)

                val pointLeftToRightEndOfCanvas = PointF(tickPoint.x - formattedTickWidth, tickPoint.y)
                canvas.drawText(formattedTick, tickPaint, pointLeftToRightEndOfCanvas)

            } else if (i == 0){
                // on the left corner of the canvas.
                // we can't go off limits-
                // so we're drawing the text aligned in such way that the start of it will be the start of the canvas

                val pointRightToLeftEndOfCanvas = PointF(tickPoint.x, tickPoint.y)
                canvas.drawText(formattedTick, tickPaint, pointRightToLeftEndOfCanvas)

            }else{
                // drawing the text in such way that he's horizontially aligned at the middle of the line and vertically stands just beneath it
                val halfFormattedTickWidth = getTextWidth(formattedTick, tickTextSize) / 2

                val pointBeneathGrid = PointF(tickPoint.x - halfFormattedTickWidth, tickPoint.y)
                canvas.drawText(formattedTick, tickPaint, pointBeneathGrid)
            }
        }
    }


    fun drawGrid(canvas: Canvas) {

        val linePaint = Paint()
        linePaint.color = lineColor
        linePaint.strokeWidth = lineWeight

        val tickPaint = Paint()
        tickPaint.color = tickTextColor
        tickPaint.textSize = tickTextSize

        if (drawsHorizontalLines) drawHorizontalLines(canvas, linePaint)
        if (drawsYTicks) drawYTicks(canvas, tickPaint)
        if (drawsVerticalLines) drawVerticalLines(canvas, linePaint)
        if (drawsXTicks) drawXTicks(canvas, tickPaint)

    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawGrid(canvas)
    }

    constructor(context: Context, yTicksCount: Int, xTicksCount: Int, yTick: Float, xTick: Float) : super(context) {
        this.yTicksCount = yTicksCount
        this.xTicksCount = xTicksCount
        this.yTick = yTick
        this.xTick = xTick
    }

    constructor(context: Context, graph: GridableGraph): this(context, graph.yTicksCount, graph.xTicksCount, graph.yTick, graph.xTick)

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
}

