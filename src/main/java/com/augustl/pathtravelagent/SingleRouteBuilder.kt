package com.augustl.pathtravelagent


import com.augustl.pathtravelagent.segment.IParametricSegment
import com.augustl.pathtravelagent.segment.StringSegment

import java.util.ArrayList

/**
 * Builds a RouteTreeNode, but for a single path, as opposed to a complete
 * route tree.
 *
 * This is typically used to add a single route to an existing tree, or to
 * create a whole array of RouteTreeNodes and merge them later.
 *
 * @see com.augustl.pathtravelagent.RouteTreeBuilder
 */
class SingleRouteBuilder<T_REQ : IRequest, T_RES> {
    private val segments = ArrayList<ISegment<T_REQ, T_RES>>()

    fun path(path: String): SingleRouteBuilder<T_REQ, T_RES> {
        segments.add(PathSegment<T_REQ, T_RES>(path))
        return this
    }

    fun param(name: String): SingleRouteBuilder<T_REQ, T_RES> {
        segments.add(ParamSegment<T_REQ, T_RES>(StringSegment(name)))
        return this
    }

    fun build(handler: IRouteHandler<T_REQ, T_RES>): RouteTreeNode<T_REQ, T_RES> {
        val bottomNodeBuilder = RouteTreeNodeBuilder<T_REQ, T_RES>()

        if (segments.size == 0) {
            bottomNodeBuilder.setHandler(handler)
            return bottomNodeBuilder.createNode("::ROOT::")
        }

        bottomNodeBuilder.setHandler(handler)
        var res = bottomNodeBuilder.createNode("::BOTTOM::")
        for (i in segments.indices.reversed()) {
            res = segments[i].getNode(res)
        }

        return res
    }

    private interface ISegment<TT_REQ : IRequest, TT_RES> {
        fun getNode(childNode: RouteTreeNode<TT_REQ, TT_RES>): RouteTreeNode<TT_REQ, TT_RES>
    }

    private inner class PathSegment<TT_REQ : IRequest, TT_RES> internal constructor(private val path: String) : ISegment<TT_REQ, TT_RES> {
        override fun getNode(childNode: RouteTreeNode<TT_REQ, TT_RES>): RouteTreeNode<TT_REQ, TT_RES> {
            val builder = RouteTreeNodeBuilder<TT_REQ, TT_RES>()
            builder.addPathSegmentChild(this.path, childNode)
            return builder.createNode("::PATH:" + this.path + "::")
        }
    }

    private inner class ParamSegment<TT_REQ : IRequest, TT_RES> internal constructor(private val parametricSegment: IParametricSegment) : ISegment<TT_REQ, TT_RES> {
        override fun getNode(childNode: RouteTreeNode<TT_REQ, TT_RES>): RouteTreeNode<TT_REQ, TT_RES> {
            val builder = RouteTreeNodeBuilder<TT_REQ, TT_RES>()
            builder.setParametricChild(this.parametricSegment, childNode)
            return builder.createNode("::PARAM:" + this.parametricSegment.paramName + "::")
        }
    }
}
