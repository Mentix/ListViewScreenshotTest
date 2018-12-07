package com.niv.test.Graphs

/** Returns the best tick based on the data, this function only deals with graphs that starts from 0 so the minimum value is not considered, only the maximum */
fun getTick(values: List<Float>, jumps: Int): Float {

    var maxValue = values[0]
    for (value in values) {
        if (value > maxValue) {
            maxValue = value
        }
    }
    return getTick(maxValue, jumps)
}


fun getTick(maximumValue: Float, jumps: Int): Float {

    if (jumps == 0) return 0f

    if (maximumValue != 0f) {

        // here we set the values we consider as pretty, for example- graphs may have ticks like 25,50,75 but not 23,46,69...

        // CAUTION- when changing this values please take a look on on the "for prettyValue in prettyValues" loop!
        val prettyValues: List<Float> = listOf(0.1f, 0.2f, 0.25f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.75f, 0.8f, 0.9f, 1f)

        var tick = maximumValue / jumps.toFloat()

        // dividing the tick to values between 0...1 so we can compare it with our pretty values
        var numberOfDivisions = 1.0
        while (tick > 1) {
            tick /= 10
            numberOfDivisions += 1
        }

        // finding our pretty value
        for (prettyValue in prettyValues) {

            if (tick <= prettyValue) {

                if (numberOfDivisions == 2.0) {
                    // our tick must be an integer,
                    // this condition checks if the tick will have a remainder when set to this prettyValue,
                    // because our prettyValues numbers are in format of 0.XX we only need to check for it in case numberOfDivisions == 2

                    // sometimes it numbers come out like 2.0000000298023224 so we're rounding it...
                    val checkedTick = Math.round(prettyValue * Math.pow(10.0, numberOfDivisions - 1) * 10000) / 10000

                    if (checkedTick % 1 > 0) {
                        continue
                    }
                }


                tick = prettyValue
                break
            }
        }
        // returns tick to his normal scale
        tick *= (Math.pow(10.0, (numberOfDivisions - 1).toDouble())).toFloat()

        return tick
    }

    return 0f
}


