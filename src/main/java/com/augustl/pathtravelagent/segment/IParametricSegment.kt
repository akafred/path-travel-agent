package com.augustl.pathtravelagent.segment

import com.augustl.pathtravelagent.RouteMatchResult

interface IParametricSegment {
    val paramName: String
    fun getValue(rawValue: String): RouteMatchResult.IResult?
}
