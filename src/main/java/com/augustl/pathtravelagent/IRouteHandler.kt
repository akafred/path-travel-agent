package com.augustl.pathtravelagent

/**
 * Gets invoked when a path matches. Takes a T_REQ, returns a T_RES.
 *
 * @param <T_REQ> A request object, implementing IRequest.
 * @param <T_RES> The return value for the handler. Can be any type you want, not used for anything by PathTravelAgent.
 */
interface IRouteHandler<T_REQ : IRequest, T_RES> {
    /**
     * Note that since a handler is immutable, care must be taken to not modify either this handler or the handler that
     * it's being merged with when a merge is performed. A brand new handler must be created.
     *
     * @param other The other handler to merge with this handler
     * @return The new handler that was created from merging this handler with another handler.
     * @see RouteTreeNode.merge
     */
    fun merge(other: IRouteHandler<T_REQ, T_RES>): IRouteHandler<T_REQ, T_RES>
    fun call(match: RouteMatch<T_REQ>): T_RES
}
