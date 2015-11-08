package com.augustl.pathtravelagent

import com.augustl.pathtravelagent.segment.IParametricSegment
import com.augustl.pathtravelagent.segment.StringSegment

/**
 * The main entry-point for building complete route trees. It uses a builder pattern
 * that organizes the Java source code in a natural tree structure.
 *
 * `class MyHandler implements IRouteHandler&lt;MyReq, MyRes&gt; {
 * // ...
 * }
 * RouteTreeNode&lt;MyReq, MyRes&gt; r = new RouteTreeBuilder&lt;MyReq, MyRes&gt;()
 * .handler(new MyHandler(&quot;Hello, /&quot;))
 * .path(&quot;/foo&quot;, new RouteTreeBuilder&lt;MyReq, MyRes&gt;()
 * .handler(new MyHandler(&quot;Hello, /foo!&quot;))
 * .path(&quot;/bar&quot;, new RouteTreeBuilder&lt;MyReq, MyRes&gt;()
 * .handler(new MyHandler(&quot;Hello, /foo/bar&quot;))))
 * .build();`
 *
 * @param <T_REQ> A request object, implementing IRequest.
 * @param <T_REQ> The return type if your IRouteHandler implementation. Can be any type you want, not used for anything
 *               by PathTravelAgent.
 *
 * @see com.augustl.pathtravelagent.IRouteHandler
 * @see com.augustl.pathtravelagent.SingleRouteBuilder
 */
class RouteTreeBuilder<T_REQ : IRequest, T_RES> {
    private val pathPrefix = "/"
    private val paramNamePrefix = "/:"
    private val nodeBuilder = RouteTreeNodeBuilder<T_REQ, T_RES>()

    fun handler(handler: IRouteHandler<T_REQ, T_RES>?): RouteTreeBuilder<T_REQ, T_RES> {
        nodeBuilder.setHandler(handler)
        return this
    }

    fun path(path: String, childBuilder: RouteTreeBuilder<T_REQ, T_RES>): RouteTreeBuilder<T_REQ, T_RES> {
        val normalizedPath = if (path.startsWith(pathPrefix)) path.substring(pathPrefix.length) else path
        nodeBuilder.addPathSegmentChild(normalizedPath, childBuilder.build(normalizedPath))
        return this
    }

    fun param(paramName: String, childBuilder: RouteTreeBuilder<T_REQ, T_RES>): RouteTreeBuilder<T_REQ, T_RES> {
        val normalizedParam = if (paramName.startsWith(paramNamePrefix)) paramName.substring(paramNamePrefix.length) else paramName
        nodeBuilder.setParametricChild(StringSegment(normalizedParam), childBuilder.build("::PARAM:$normalizedParam::"))
        return this
    }

    fun param(segment: IParametricSegment, childBuilder: RouteTreeBuilder<T_REQ, T_RES>): RouteTreeBuilder<T_REQ, T_RES> {
        nodeBuilder.setParametricChild(segment, childBuilder.build("::PARAM:" + segment.paramName + "::"))
        return this
    }

    fun wildcard(childBuilder: RouteTreeBuilder<T_REQ, T_RES>): RouteTreeBuilder<T_REQ, T_RES> {
        nodeBuilder.setWildcardChild(childBuilder.build("::WILDCARD::"))
        return this
    }

    fun build(): RouteTreeNode<T_REQ, T_RES> {
        return nodeBuilder.createNode("::ROOT::")
    }

    fun build(label: String): RouteTreeNode<T_REQ, T_RES> {
        return nodeBuilder.createNode(label)
    }


}
