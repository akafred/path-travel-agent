package com.augustl.pathtravelagent

import com.augustl.pathtravelagent.segment.IParametricSegment
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap

/**
 * The actual routes. An immutable value. Build with RouteTreeBuilder or SingleRouteBuilder.
 *
 * All the methods on this class are used when performing matching operations. The actual instance, being immutable,
 * can be considered a raw value. Use the various builders to create instances of these.
 *
 * @param <T_REQ> A request object, implementing IRequest.
 * @param <T_RES> The return value for the handler. Can be any type you want, not used for anything by PathTravelAgent.
 *
 * @see com.augustl.pathtravelagent.RouteTreeBuilder
 * @see com.augustl.pathtravelagent.SingleRouteBuilder
 */
class RouteTreeNode<T_REQ : IRequest, T_RES> {
    private val label: String
    /**
     * This method returns a handler or null. If a node has no handler, it means the tree has no handler at this
     * particular point. This is useful for deep trees where you only want a handler at the bottom. An example would
     * be handling /foo/bar/baz, but not /foo/bar. The node representing the "bar" level would have a null handler in
     * that case.

     * @return The handler associated with this node.
     */
    val handler: IRouteHandler<T_REQ, T_RES>?
    private val pathSegmentChildNodes: Map<String, RouteTreeNode<T_REQ, T_RES>>
    private val parametricChild: ParametricChild<T_REQ, T_RES>?
    val wildcardChildNode: RouteTreeNode<T_REQ, T_RES>?

    constructor() {
        this.label = "::ROOT::"
        this.handler = null
        this.pathSegmentChildNodes = Collections.unmodifiableMap(HashMap<String, RouteTreeNode<T_REQ, T_RES>>())
        this.parametricChild = null
        this.wildcardChildNode = null
    }

    constructor(
            label: String,
            handler: IRouteHandler<T_REQ, T_RES>?,
            pathSegmentChildNodes: HashMap<String, RouteTreeNode<T_REQ, T_RES>>,
            parametricChild: ParametricChild<T_REQ, T_RES>?,
            wildcardChild: RouteTreeNode<T_REQ, T_RES>?) {
        this.label = label
        this.handler = handler
        this.pathSegmentChildNodes = Collections.unmodifiableMap(pathSegmentChildNodes)
        this.parametricChild = parametricChild
        this.wildcardChildNode = wildcardChild
    }

    /**
     * If the node has a named child, the matcher should most likely prioritize this named child over any parametric
     * or wildcard child. For example, even if there's a parametric handler for /projects/myproj, if there happens to be
     * a named handler for "myproj", it should take precedence over the parametric handler.
     */
    fun containsPathSegmentChildNodes(pathSegment: String): Boolean {
        return this.pathSegmentChildNodes.containsKey(pathSegment)
    }

    fun getPathSegmentChildNode(pathSegment: String): RouteTreeNode<T_REQ, T_RES>? {
        return this.pathSegmentChildNodes[pathSegment]
    }

    /**
     * If a node has a parametric child, the matcher can use this child to handle arbitrary values. For example, given
     * the path /projects/myproj, if there is no named handler for "myproj", the parametric handler can be invoked for
     * "myproj", giving us a named parameter containing that value.
     */
    fun hasParametricChild(): Boolean {
        return this.parametricChild != null
    }

    val parametricChildSegment: IParametricSegment?
        get() = this.parametricChild!!.parametricSegment

    val parametricChildNode: RouteTreeNode<T_REQ, T_RES>?
        get() = this.parametricChild!!.childNode

    /**
     * When a node has a wildcard child node, it means that the rest of the path at this point will be passed to that
     * child, as a wildcard. For example, given the path /foo/bar/baz/maz and a wildcard handler at /foo/bar, any
     * segment beyond /foo/bar should be passed to the wildcard child, instead of looking for further child handlers.
     */
    fun hasWildcardChild(): Boolean {
        return this.wildcardChildNode != null
    }

    /**
     *
     * Deep left-to-right merges a node with another. Since this class is immutable, a new instance is returned, and
     * none of the two merged instances are changed.

     *
     * How two handlers are merged is up to the handler in question. When both the source and target tree has a
     * handler at a given point in the tree, the merge method is called on the source handler, getting the target
     * handler passed in. The details of how this merge takes place is up to the user, no default implementation is
     * provided.
     *
     * The other elements such as parametric routes and wildcard routes are automatically merged and can not be
     * configured by the user.
     *
     * @param other The (immutable) node to merge with
     * @return The new (immutable) node
     *
     * @see com.augustl.pathtravelagent.IRouteHandler.merge
     */
    fun merge(other: RouteTreeNode<T_REQ, T_RES>?): RouteTreeNode<T_REQ, T_RES> {
        if (other == null) throw NullPointerException("Cannot merge when 'other == null'.")
        return merge(other, ArrayList<String>())
    }

    private fun merge(other: RouteTreeNode<T_REQ, T_RES>, context: MutableList<String>): RouteTreeNode<T_REQ, T_RES> {
        var context = context
        context = ArrayList(context)
        context.add(other.label)

        return RouteTreeNode(
                other.label,
                this.getMergedHandler(other, context),
                this.getMergedPathSegmentChildNodes(other, context),
                this.getMergedParametricChild(other, context),
                this.getMergedWildcardChild(other, context))
    }

    private fun getMergedHandler(other: RouteTreeNode<T_REQ, T_RES>, context: List<String>): IRouteHandler<T_REQ, T_RES>? {
        if (other.handler == null) {
            return this.handler
        } else {
            if (this.handler == null) {
                return other.handler
            } else {
                return this.handler.merge(other.handler)
            }
        }
    }

    private fun getMergedPathSegmentChildNodes(other: RouteTreeNode<T_REQ, T_RES>, context: MutableList<String>): HashMap<String, RouteTreeNode<T_REQ, T_RES>> {
        val res = HashMap(this.pathSegmentChildNodes)

        for (pathSegment in other.pathSegmentChildNodes.keys) {
            if (this.pathSegmentChildNodes.containsKey(pathSegment)) {
                res.put(pathSegment, this.pathSegmentChildNodes[pathSegment]!!.merge(other.pathSegmentChildNodes[pathSegment]!!, context))
            } else {
                res.put(pathSegment, other.pathSegmentChildNodes[pathSegment])
            }
        }

        return res
    }

    private fun getMergedParametricChild(other: RouteTreeNode<T_REQ, T_RES>, context: MutableList<String>): ParametricChild<T_REQ, T_RES>? {
        if (this.parametricChild == null) {
            return other.parametricChild
        } else {
            if (other.parametricChild == null) {
                return this.parametricChild
            } else {
                return ParametricChild(other.parametricChild.parametricSegment, this.parametricChild.childNode!!.merge(other.parametricChild.childNode!!, context))
            }
        }
    }

    private fun getMergedWildcardChild(other: RouteTreeNode<T_REQ, T_RES>, context: MutableList<String>): RouteTreeNode<T_REQ, T_RES>? {
        if (this.wildcardChildNode == null) {
            return other.wildcardChildNode
        } else {
            return this.wildcardChildNode.merge(other.wildcardChildNode!!, context)
        }
    }
}
