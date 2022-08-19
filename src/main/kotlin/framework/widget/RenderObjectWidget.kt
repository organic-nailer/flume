package framework.widget

import framework.render.RenderObject

abstract class RenderObjectWidget<RenderObjectType: RenderObject> : Widget() {
    abstract fun createRenderObject(): RenderObjectType
}