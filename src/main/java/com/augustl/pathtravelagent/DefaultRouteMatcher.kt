package com.augustl.pathtravelagent

/**
 * The default implementation of taking a RouteTreeNode and a request and returning a response.
 *
 * @param <T_REQ> A request object, implementing IRequest.
 * @param <T_RES> The return value for the handler. Can be any type you want, not used for anything by PathTravelAgent.
 */
class DefaultRouteMatcher<T_REQ : IRequest, T_RES> {
    fun match(rootNode: RouteTreeNode<T_REQ, T_RES>, req: T_REQ): T_RES? {
        val pathSegments = req.pathSegments
        var targetNode: RouteTreeNode<T_REQ, T_RES>? = rootNode
        val routeMatchResult = RouteMatchResult()

        var i: Int
        i = 0
        while (i < pathSegments.size) {
            var pathSegment = pathSegments[i]

            if (targetNode!!.containsPathSegmentChildNodes(pathSegment)) {
                targetNode = targetNode.getPathSegmentChildNode(pathSegment)
                i++
                continue
            }

            if (targetNode.hasParametricChild()) {
                if (!routeMatchResult.addParametricSegment(targetNode.parametricChildSegment, pathSegment)) {
                    return null
                }
                targetNode = targetNode.parametricChildNode
                i++
                continue
            }

            if (targetNode.hasWildcardChild()) {
                while (i < pathSegments.size) {
                    pathSegment = pathSegments[i]
                    routeMatchResult.addToWildcardMatches(pathSegment)
                    i++
                }
                targetNode = targetNode.wildcardChildNode
                break
            }

            return null
        }

        if (targetNode != null && targetNode.handler != null) {
            return targetNode.handler.call(RouteMatch(req, routeMatchResult))
        }

        return null
    }
}
