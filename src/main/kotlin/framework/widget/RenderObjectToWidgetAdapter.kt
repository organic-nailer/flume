package framework.widget

import framework.element.BuildOwner
import framework.element.Element
import framework.element.RenderObjectToWidgetElement
import framework.render.RenderView

class RenderObjectToWidgetAdapter(
    val child: Widget?, val container: RenderView,
) : RenderObjectWidget<RenderView>() {
    override fun createElement(): Element = RenderObjectToWidgetElement(this)

    override fun createRenderObject(): RenderView = container

    fun attachToRenderTree(
        owner: BuildOwner,
        element: RenderObjectToWidgetElement<*>? = null,
    ): RenderObjectToWidgetElement<*> {
        val result: RenderObjectToWidgetElement<*>
        if (element == null) {
            result = createElement() as RenderObjectToWidgetElement<*>
            result.owner = owner
            owner.buildScope {
                result.mount(null)
            }
        } else {
            result = element
            result.newWidget = this
            result.markNeedsBuild()
        }
        return result
    }
}