package framework.widget

import framework.render.RenderObject

abstract class RenderObjectWidget<RenderObjectType: RenderObject> : Widget() {
    abstract fun createRenderObject(): RenderObjectType

    /**
     * RenderObjectの情報を更新する
     *
     * [Element.performRebuild]で発火される
     */
    open fun updateRenderObject(renderObject: RenderObjectType) {}
}