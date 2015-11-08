package com.augustl.pathtravelagent

/**
 * Default implementation of path to path segments, i.e. "/projects/123?test" to ["project", "123"].
 */
object DefaultPathToPathSegments {

    fun parse(path: String): List<String> {
        val pathSegmentsAry = extractPath(path).split('/').dropLastWhile { it.isEmpty() }.toTypedArray()
        return if (pathSegmentsAry.isEmpty()) emptyList() else listOf(*pathSegmentsAry).drop(1)
    }

    fun extractPath(path: String): String {
        return if (path.isEmpty()) path else path.split('?')[0]
    }
}
