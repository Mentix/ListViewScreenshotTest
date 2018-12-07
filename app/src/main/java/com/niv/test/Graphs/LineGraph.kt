package com.niv.test.Graphs

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.graphics.Shader
import android.graphics.LinearGradient
import org.jetbrains.anko.withAlpha


class LineGraph : View, GridableGraph {

    /** The color of the line */
    var graphLineColor = Color.BLUE
    /** The weight of the line */
    var graphLineWeight = 5f

    /** Determines whether to draw the area shape beneath the graph */
    var drawBeneathShape = true
    /** Determines whether to draw the area shape beneath the graph as a gradient */
    var isBeneathShapeGradient = false

    /** Determines whether to draw a circle at each data point */
    var drawCircles = true
    /** Determines the radius of the circles that are drawn at each data point*/
    var circleRadius = 8f

    /**
     * When set to true- the graph considers X values as numerals, then, with the calculation of xTick and xTicksCount the graph dislpays the points
     * When set to false- the graph considers X values as non numerals, then, the X axis takes the full width of the graph's bounds and points are added accordingly
     */
    var isXNumeral = true

    var grid: Grid? = null
    var drawsGrid = true
    fun addGraphGrid() {
        grid = Grid(this.context, this)
        this.invalidate()
    }


    /** The total number of horizontal lines in the graph, after changing this value,
     * please consider calling recalculateYTick() */
    override var yTicksCount = 5

    /** The total number of vertical lines in the graph, after changing this value,
     * please consider calling recalculateXTick() */
    override var xTicksCount = 5

    override var yTick: Float = 1f
    override var xTick: Float = 1f

    fun recalculateXTick() { xTick = getXTick(points, xTicksCount) }
    fun recalculateYTick() { yTick = getYTick(points, yTicksCount) }

    fun scale(toGraph: LineGraph){

        yTicksCount = toGraph.yTicksCount
        yTick = toGraph.yTick

        isXNumeral = toGraph.isXNumeral

        xTick = toGraph.xTick
        xTicksCount = toGraph.xTicksCount
    }

    // holds the current canvas width and height
    private var canvasWidth = 0f
    private var canvasHeight = 0f

    // In case we've got a grid we display the graph in the grid's rect- the grid's rect considered to be the rect where lines are drawn-
    // In that way, the graph won't collapse with the Y and X axis of the grid.
    // Incase there's no grid to display, we're using the whole canvas as our rect.
    private val graphRect: RectF
        get() = grid?.getGridRect(canvasWidth, canvasHeight) ?: RectF(0f,0f,canvasWidth, canvasHeight)



    /** Recalculates X and Y ticks */
    fun notifyDataSetChanged(){
        recalculateXTick()
        recalculateYTick()
    }

    /** Holds all data points */
    var points: List<LineGraphPoint> = listOf()


    /** Returns the best tick based on the data */
    fun getYTick(points: List<LineGraphPoint>, yLines: Int): Float{
        val values: MutableList<Float> = mutableListOf()

        for (point in points){
            values.add(point.y)
        }

        // explanation for -1: the zero line is not adding any value so the lines that really matter for the calculation are the other lines
        return getTick(values, yLines - 1)
    }

    /** Returns the best tick based on the data */
    fun getXTick(points: List<LineGraphPoint>, xLines: Int): Float{
        val values: MutableList<Float> = mutableListOf()

        for (point in points){
            values.add(point.x)
        }

        // explanation for -1: the zero line is not adding any value so the lines that really matter for the calculation are the other lines
        return getTick(values, xLines - 1)
    }


    // Unlike our canvas logic, rounded height/width calculates himself by the data points provided, that way we can calculate where each
    // data point should appear on the screen in relation for other data points
    private val roundedHeight: Float get() {
        // Why (yTicks.count - 1)? - the zero line is not adding any value so the lines that really matter for the calculation are the other lines
        return (yTick * (yTicksCount - 1))
    }
    private val roundedWidth: Float get() {
        if (isXNumeral) return (xTick * (xTicksCount - 1))
        else return graphRect.width()
    }


    // Returns the relative point on canvas based on our data
    private fun getRelativePoint(point: LineGraphPoint): PointF{

        val indexOfPoint = points.indexOf(point)
        if (indexOfPoint < 0 || indexOfPoint > points.size) throw IndexOutOfBoundsException("Couldn't find point in relative to the other points")

        var xRelativeValue = graphRect.left + graphRect.width() * (point.x / roundedWidth)

        if (!isXNumeral)
            xRelativeValue = graphRect.left + graphRect.width() * (indexOfPoint.toFloat() / (points.size.toFloat() - 1))

        if (point.y < 0) return PointF(xRelativeValue, graphRect.height())

        val yRelativeValue = graphRect.height() - (graphRect.height() * (point.y / roundedHeight))

        return PointF(xRelativeValue, yRelativeValue)

    }


    private fun addShapeBeneathGraph(canvas: Canvas){

        if (points.isEmpty()) return

        val paint = Paint()
        paint.color = graphLineColor.withAlpha(30)

        if (isBeneathShapeGradient) {
            paint.shader = LinearGradient(graphRect.centerX(), graphRect.top, graphRect.centerX(), graphRect.bottom, graphLineColor.withAlpha(250), Color.TRANSPARENT, Shader.TileMode.MIRROR)
        }

        val path = Path()

        // bottom left corner- the graph starts there
        path.moveTo(graphRect.left, graphRect.bottom)

        for (point in points){
            val relativePoint = getRelativePoint(point)
            path.lineTo(relativePoint.x, relativePoint.y)
        }

        val rightEndOfGraphPoint = getRelativePoint(points.last())
        // right end of the graph and the bottom of the canvas- the graph ends here
        path.lineTo(rightEndOfGraphPoint.x, graphRect.bottom)

        // bottom left corner- allows us to close the shape
        path.lineTo(graphRect.left, graphRect.bottom)

        path.close()

        canvas.drawPath(path, paint)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvasWidth = canvas.width.toFloat()
        canvasHeight = canvas.height.toFloat()

        if (grid != null && drawsGrid) {
            val gridBitmap = Bitmap.createBitmap(canvas.width, canvas.height, Bitmap.Config.ARGB_8888)
            val gridCanvas = Canvas(gridBitmap)
            grid!!.draw(gridCanvas)

            canvas.drawBitmap(gridBitmap, 0f, 0f, Paint())
        }

        val paint = Paint()
        paint.color = graphLineColor
        paint.strokeWidth = graphLineWeight

        if (drawBeneathShape) addShapeBeneathGraph(canvas)

        for (i in points.indices){

            val startingPoint = getRelativePoint(points[i])

            if (i < points.size - 1) {
                val endingPoint = getRelativePoint(points[i + 1])
                canvas.drawLine(startingPoint, endingPoint, paint)
            }

            if (i != 0 && i != (points.size - 1)) canvas.drawCircle(startingPoint, circleRadius, paint)
        }
    }

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle)
}