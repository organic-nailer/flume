package framework.widget

import framework.render.RenderParagraph
import framework.render.TextSpan

class RichText(
    val text: TextSpan,
): MultiChildRenderObjectWidget<RenderParagraph>(listOf()) {
    override fun createRenderObject(): RenderParagraph {
        return RenderParagraph(
            text
        )
    }
}