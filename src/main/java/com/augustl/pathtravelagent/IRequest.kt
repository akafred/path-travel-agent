package com.augustl.pathtravelagent

/**
 * Represents a request.
 *
 * The unit of work is a `List<String>` of path segments. So a request does not contain /projects/123 directly, it
 * is expected to return a list of ["projects", "123"].

 * @see com.augustl.pathtravelagent.DefaultPathToPathSegments.parse
 */
interface IRequest {
    val pathSegments: List<String>
}
