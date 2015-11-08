package com.augustl.pathtravelagent

import com.augustl.pathtravelagent.segment.IParametricSegment

import java.util.ArrayList
import java.util.HashMap

/**
 * Internal representation of the data obtained from matching a route.
 */
class RouteMatchResult {
    private val integerMatches = HashMap<String, Int>()
    private val stringMatches = HashMap<String, String>()
    private val wildcardMatches = ArrayList<String>()

    fun addParametricSegment(parametricSegment: IParametricSegment, rawValue: String): Boolean {
        val value = parametricSegment.getValue(rawValue) ?: return false

        value.addToMatchResult(parametricSegment.paramName, this)
        return value.isSuccess
    }

    fun addToIntegerMatches(pathSegment: String, value : Int) {
        this.integerMatches.put(pathSegment, value)
    }

    fun getIntegerMatch(pathSegment: String): Int? {
        return this.integerMatches[pathSegment]
    }

    fun addToStringMatches(pathSegment: String, value: String) {
        this.stringMatches.put(pathSegment, value)
    }

    fun getStringMatch(pathSegment: String): String? {
        return this.stringMatches[pathSegment]
    }

    fun addToWildcardMatches(pathSegment: String) {
        this.wildcardMatches.add(pathSegment)
    }

    fun getWildCardMatches(): List<String> {
        return this.wildcardMatches
    }

    /**
     * Associates a parametric segment with a value
     */
    interface IResult {
        val isSuccess: Boolean
        fun addToMatchResult(paramName: String, res: RouteMatchResult)
    }

    /**
     * Internal class for associating a parametric segment with an integer value
     *
     * @see com.augustl.pathtravelagent.segment.NumberSegment
     */
    class IntegerResult(private val value: Int) : IResult {

        override val isSuccess: Boolean
            get() = true

        override fun addToMatchResult(paramName: String, res: RouteMatchResult) {
            res.addToIntegerMatches(paramName, this.value)
        }
    }

    /**
     * Internal class for associating a parametric segment with a string value
     *
     * @see com.augustl.pathtravelagent.segment.StringSegment
     */
    class StringResult(private val value: String) : IResult {

        override val isSuccess: Boolean
            get() = true

        override fun addToMatchResult(paramName: String, res: RouteMatchResult) {
            res.addToStringMatches(paramName, this.value)
        }
    }
}
