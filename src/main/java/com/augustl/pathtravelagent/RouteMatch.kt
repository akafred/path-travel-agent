package com.augustl.pathtravelagent

/**
 * The value passed to handlers when they match a path. Contains information obtained from the URL.
 *
 * Parametric values in the URL can be obtained from this object. For type safety, different methods are
 * exposed for different types. Call the correct method with the correct param name depending on the type that
 * was defined when the route was created.
 *
 * The various route builders defaults to strings when no type has been specified.

 * @param  <T_REQ> A request object, implementing IRequest. Used to access the raw request object used for matching.
 */
class RouteMatch<T_REQ : IRequest>(
    /**
     * @return The raw request object
     */
    val request: T_REQ, private val routeMatchResult: RouteMatchResult) {

    /**
     * @param paramName The name used when defining the route
     * @return The integer value associated with the paramName
     */
    fun getIntegerRouteMatchResult(paramName: String): Int? {
        return this.routeMatchResult.getIntegerMatch(paramName)
    }

    /**
     * @param paramName The name used when defining the route
     * @return The string value associated with the paramName
     */
    fun getStringRouteMatchResult(paramName: String): String? {
        return this.routeMatchResult.getStringMatch(paramName)
    }

    val wildcardRouteMatchResult: List<String>
        get() = this.routeMatchResult.getWildCardMatches()
}
