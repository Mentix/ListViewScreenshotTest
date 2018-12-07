package com.niv.test

import android.view.View
import com.niv.test.Graphs.GraphContainer

class GraphViewHolder(val view: View) {
    val graphContainer: GraphContainer = view.findViewById(R.id.graphContainer)

    init{
        graphContainer.headerHeight = 0f
    }
}

