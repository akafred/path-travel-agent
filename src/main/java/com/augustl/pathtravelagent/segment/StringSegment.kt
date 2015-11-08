package com.augustl.pathtravelagent.segment

import com.augustl.pathtravelagent.RouteMatchResult

class StringSegment(override val paramName: String) : IParametricSegment {

    override fun getValue(rawValue: String): RouteMatchResult.IResult? {
        return RouteMatchResult.StringResult(rawValue)
    }
}
