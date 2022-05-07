package framework.widget

import framework.render.RenderObject

abstract class RenderObjectWidget : Widget() {
    abstract fun createRenderObject(): RenderObject
}