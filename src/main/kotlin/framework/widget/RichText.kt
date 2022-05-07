package framework.widget

import framework.render.RenderObject
import framework.render.RenderParagraph
import framework.render.TextSpan

class RichText(
    val text: TextSpan,
): MultiChildRenderObjectWidget(listOf()) {
    override fun createRenderObject(): RenderObject {
        return RenderParagraph(
            text
        )
    }
}