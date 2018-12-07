package com.niv.test.Graphs

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.graphics.Bitmap


class GraphContainer: View {

    /** the color of the rect */
    var color = Color.TRANSPARENT

    /** graph's title */
    var title: String? = null
    var titleFontSize: Float = 60f

    /** when positive- moves title to right, when negative- moves title to left */
    var titleXOffset = 0f
    /** when positive- moves title up, when negative- moves title down */
    var titleYOffset = 0f

    var titleColor = Color.BLACK

    private var graphViews: MutableList<View> = mutableListOf()

    fun addGraph(graph: View){
        graphViews.add(graph)
    }

    fun addGraphs(graphs: List<View>){
        graphViews.addAll(graphs)
    }

    fun clearGraphs(){
        while (!graphViews.isEmpty()){
            graphViews.removeAt(0)
        }
    }

    /** the header's height (makes room for the graph's title) */
    var headerHeight = 100f // initialized later

    /** dedicated frame for header */
    val headerFrame: RectF get() = RectF(0f,0f,0f,0f) // initialized later

    /** the footer's height */
    var footerHeight = 0f

    /** dedicated frame for graphs- the space left when excluding the header and footer */
    val graphRect: RectF get() = RectF(0f, headerHeight, canvasWidth, canvasHeight - footerHeight)

    private var canvasWidth = 0f
    private var canvasHeight = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvasWidth = canvas.width.toFloat()
        canvasHeight = canvas.height.toFloat()

        canvas.drawColor(color)

        for (i in graphViews.indices){
            val graphView = graphViews[i]

            val graphBitmap = Bitmap.createBitmap(graphRect.width().toInt(),graphRect.height().toInt(), Bitmap.Config.ARGB_8888)

            val graphCanvas = Canvas(graphBitmap)
            graphView.draw(graphCanvas)

            canvas.drawBitmap(graphBitmap, graphRect.left, graphRect.top, Paint())
        }

        if (title != null){
            val paint = Paint()
            paint.textSize = titleFontSize
            paint.color = titleColor
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))

            val point = PointF(titleXOffset, titleFontSize + titleYOffset)
            canvas.drawText(title!!, paint, point)
        }


    }

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle)
}