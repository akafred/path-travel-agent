package com.augustl.pathtravelagent.segment

import com.augustl.pathtravelagent.RouteMatchResult

class NumberSegment(override val paramName: String) : IParametricSegment {

    override fun getValue(rawValue: String): RouteMatchResult.IResult? {
        try {
            val parsed = Integer.parseInt(rawValue, 10)
            return RouteMatchResult.IntegerResult(parsed)
        } catch (e: NumberFormatException) {
            return null
        }

    }
}
