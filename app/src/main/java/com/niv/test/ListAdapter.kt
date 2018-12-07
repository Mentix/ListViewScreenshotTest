package com.niv.test

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.BaseExpandableListAdapter
import com.niv.test.Graphs.LineGraph
import com.niv.test.Graphs.LineGraphPoint
import com.niv.test.Graphs.getTick

class ListAdapter(val context: Context, var list: List<List<Float>>) : BaseAdapter() {

    override fun getCount(): Int = list.size
    override fun getItem(position: Int): List<Float> = list[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var view = convertView

        val graphViewHolder: GraphViewHolder

        if ((view == null) || (view.tag as? GraphViewHolder == null)) {
            val layoutInf = LayoutInflater.from(context)

            view = layoutInf.inflate(R.layout.graph_list_item, null)
            graphViewHolder = GraphViewHolder(view)
            view.tag = graphViewHolder

        } else {
            graphViewHolder = view.tag as GraphViewHolder
        }


        val lineGraph = LineGraph(context)

        val values = getItem(position)

        // converting our floats to LineGraphPoints
        val graphPoints: MutableList<LineGraphPoint> = mutableListOf()
        for (i in values.indices){
            graphPoints.add(LineGraphPoint((i).toFloat(), values[i]))
        }

        println(getTick(5f, 4))

        lineGraph.points = graphPoints
        lineGraph.notifyDataSetChanged()

        lineGraph.addGraphGrid()

        graphViewHolder.graphContainer.clearGraphs()
        graphViewHolder.graphContainer.addGraph(lineGraph)

        return view!!
    }
}
