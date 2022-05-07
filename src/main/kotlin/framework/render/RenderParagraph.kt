package framework.render

import common.Offset
import common.Size
import framework.PaintingContext
import framework.RenderPipeline
import framework.render.mixin.ContainerRenderObject
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.FontMgr
import org.jetbrains.skia.paragraph.FontCollection
import org.jetbrains.skia.paragraph.Paragraph
import org.jetbrains.skia.paragraph.ParagraphBuilder
import org.jetbrains.skia.paragraph.ParagraphStyle
import org.jetbrains.skia.paragraph.TextStyle
import kotlin.math.ceil

class RenderParagraph(
    text: TextSpan,
) : RenderBox(), ContainerRenderObject<RenderBox> {
    private val textPainter = TextPainter(text)
    override val thisRef: RenderObject = this
    override val children: MutableList<RenderBox> = mutableListOf()

    override fun performLayout() {
        textPainter.layout(minWidth = constraints.minWidth, maxWidth = constraints.maxWidth)

        val textSize = textPainter.size
        size = constraints.constrain(textSize)
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        textPainter.paint(context.canvas, offset)
    }

    override fun attach(owner: RenderPipeline) {
        super.attach(owner)
        attachChildren(owner)
    }
}

class TextSpan(
    val text: String,
    val textStyle: TextStyle? = null,
) {
    fun build(builder: ParagraphBuilder) {
        builder.addText(text)
    }
}

class TextPainter(
    text: TextSpan,
) {
    var text: TextSpan = text
        set(value) {
            if (field.text == value.text) return
            field = value
            markNeedsLayout()
        }
    private var paragraph: Paragraph? = null
    private var lastMinWidth: Double? = null
    private var lastMaxWidth: Double? = null
    val width: Double get() = paragraph!!.maxWidth.toDouble()
    val height: Double get() = paragraph!!.height.toDouble()
    val size: Size get() = Size(width, height)

    private fun markNeedsLayout() {
        paragraph = null
    }

    private fun createParagraphStyle(): ParagraphStyle {
        return ParagraphStyle().apply {
            textStyle = text.textStyle ?: textStyle.apply {
                color = 0xFF000000.toInt()
                fontSize = 30f
            }
        }
    }

    private fun createParagraph() {
        val builder = ParagraphBuilder(createParagraphStyle(),
            FontCollection().apply { setDefaultFontManager(FontMgr.default) })
        text.build(builder)
        paragraph = builder.build()
    }

    private fun layoutParagraph(minWidth: Double, maxWidth: Double) {
        paragraph!!.layout(maxWidth.toFloat())
        if (minWidth != maxWidth) {
            var newWidth = ceil(paragraph!!.maxIntrinsicWidth)
            newWidth = newWidth.coerceIn(minWidth.toFloat(), maxWidth.toFloat())
            if (newWidth != ceil(paragraph!!.maxWidth)) {
                paragraph!!.layout(newWidth)
            }
        }
    }

    fun layout(minWidth: Double = 0.0, maxWidth: Double = Double.POSITIVE_INFINITY) {
        lastMinWidth = minWidth
        lastMaxWidth = maxWidth
        if (paragraph == null) {
            createParagraph()
        }
        layoutParagraph(minWidth, maxWidth)
    }

    fun paint(canvas: Canvas, offset: Offset) {
        paragraph!!.paint(canvas, offset.dx.toFloat(), offset.dy.toFloat())
    }
}
