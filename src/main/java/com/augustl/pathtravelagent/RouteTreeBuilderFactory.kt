package com.augustl.pathtravelagent

class RouteTreeBuilderFactory<T_REQ : IRequest, T_RES> {
    fun builder(): RouteTreeBuilder<T_REQ, T_RES> {
        return RouteTreeBuilder()
    }
}
