package com.augustl.pathtravelagent

import com.augustl.pathtravelagent.segment.IParametricSegment

/**
 * Used internally to represent an item in the routing tree that is parametric, i.e. it takes any (valid) value,
 * instead of a static path segment.

 * @param <T_REQ> A request object, implementing IRequest.
 * @param <T_RES> The return value for the handler. Can be any type you want, not used for anything by PathTravelAgent.
 */
class ParametricChild<T_REQ : IRequest, T_RES>(val parametricSegment: IParametricSegment?, val childNode: RouteTreeNode<T_REQ, T_RES>?) {
    init {
        if ((parametricSegment != null && childNode == null) || (parametricSegment == null && childNode != null)) {
            throw IllegalArgumentException("Both parametricSegment and parametricChildNode must be either non-null or null")
        }
    }
}
