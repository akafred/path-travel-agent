package com.augustl.pathtravelagent

import com.augustl.pathtravelagent.segment.IParametricSegment

import java.util.HashMap
import java.util.regex.Pattern

internal class RouteTreeNodeBuilder<T_REQ : IRequest, T_RES> {
    private val validSegmentChars = Pattern.compile("[\\w\\-\\._~]+")

    private var handler: IRouteHandler<T_REQ, T_RES>? = null
    private val pathSegmentChildNodes = HashMap<String, RouteTreeNode<T_REQ, T_RES>>()
    private var parametricChild: ParametricChild<T_REQ, T_RES>? = null
    private var wildcardChild: RouteTreeNode<T_REQ, T_RES>? = null

    fun setHandler(handler: IRouteHandler<T_REQ, T_RES>) {
        this.handler = handler
    }

    fun addPathSegmentChild(pathSegment: String, childNode: RouteTreeNode<T_REQ, T_RES>) {
        ensureContainsValidSegmentChars(pathSegment)
        this.pathSegmentChildNodes.put(pathSegment, childNode)
    }

    @Synchronized fun setParametricChild(parametricSegment: IParametricSegment, childNode: RouteTreeNode<T_REQ, T_RES>) {
        if (parametricChild != null) {
            throw IllegalStateException("Cannot assign parametric child, already has one")
        }

        parametricChild = ParametricChild(parametricSegment, childNode)
    }

    @Synchronized fun setWildcardChild(childNode: RouteTreeNode<T_REQ, T_RES>) {
        if (wildcardChild != null) {
            throw IllegalStateException("Cannot assign wildcard child, already has one")
        }

        wildcardChild = childNode
    }

    fun createNode(label: String): RouteTreeNode<T_REQ, T_RES> {
        return RouteTreeNode(
                label,
                this.handler,
                this.pathSegmentChildNodes,
                this.parametricChild,
                this.wildcardChild)
    }

    private fun ensureContainsValidSegmentChars(str: String) {
        val matcher = validSegmentChars.matcher(str)
        if (!matcher.matches()) {
            throw IllegalArgumentException("Param $str contains invalid characters")
        }
    }
}
