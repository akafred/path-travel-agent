package com.augustl.pathtravelagent;

import com.augustl.pathtravelagent.segment.IParametricSegment;
import com.augustl.pathtravelagent.segment.StringSegment;

public class RouteTreeBuilder<T_REQ extends IRequest, T_RES> {
    private final String pathPrefix = "/";
    private final String paramNamePrefix = "/:";
    private RouteTreeNodeBuilder<T_REQ, T_RES> nodeBuilder = new RouteTreeNodeBuilder<T_REQ, T_RES>();

    public RouteTreeBuilder<T_REQ, T_RES> handler(IRouteHandler<T_REQ, T_RES> handler) {
        nodeBuilder.setHandler(handler);
        return this;
    }

    public RouteTreeBuilder<T_REQ, T_RES> path(final String path, RouteTreeBuilder<T_REQ, T_RES> childBuilder) {
        String normalizedPath = path.startsWith(pathPrefix) ? path.substring(pathPrefix.length()) : path;
        nodeBuilder.addPathSegmentChild(normalizedPath, childBuilder.build());
        return this;
    }

    public RouteTreeBuilder<T_REQ, T_RES> param(final String paramName, final RouteTreeBuilder<T_REQ, T_RES> childBuilder) {
        String normalizedParam = paramName.startsWith(paramNamePrefix) ? paramName.substring(paramNamePrefix.length()) : paramName;
        nodeBuilder.setParametricChild(new StringSegment(normalizedParam), childBuilder.build());
        return this;
    }

    public RouteTreeBuilder<T_REQ, T_RES> param(final IParametricSegment segment, final RouteTreeBuilder<T_REQ, T_RES> childBuilder) {
        nodeBuilder.setParametricChild(segment, childBuilder.build());
        return this;
    }

    public RouteTreeBuilder<T_REQ, T_RES> wildcard(final RouteTreeBuilder<T_REQ, T_RES> childBuilder) {
        nodeBuilder.setWildcardChild(childBuilder.build());
        return this;
    }

    public RouteTreeNode<T_REQ, T_RES> build() {
        return nodeBuilder.createNode();
    }
}
