package framework

import common.Clip
import common.ClipPathLayer
import common.ClipRRectLayer
import common.ClipRectLayer
import common.ContainerLayer
import common.Layer
import common.Offset
import common.OffsetLayer
import common.OpacityLayer
import common.PictureLayer
import common.makeOffset
import framework.render.RenderObject
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Path
import org.jetbrains.skia.PictureRecorder
import org.jetbrains.skia.RRect
import org.jetbrains.skia.Rect

class PaintingContext(
    private val containerLayer: ContainerLayer,
    private val estimatedBounds: Rect,
) {
    companion object {
        /**
         * [RenderObject.isRepaintBoundary] == trueのRenderObjectの下位Layerを再構築する
         */
        fun repaintCompositedChild(child: RenderObject) {
            var childLayer = child.layer as OffsetLayer?
            if (childLayer == null) {
                childLayer = OffsetLayer()
                child.layer = childLayer
            } else {
                childLayer.removeAllChildren()
            }
            val childContext = PaintingContext(childLayer, child.size.and(Offset.zero))

            // paintWithContext
            child.needsPaint = false
            child.paint(childContext, Offset.zero)
            // end paintWithContext
            childContext.stopRecordingIfNeeded()
        }
    }

    private var currentLayer: PictureLayer? = null
    private var recorder: PictureRecorder? = null
    private var _canvas: Canvas? = null
    private val isRecording: Boolean
        get() = _canvas != null

    val canvas: Canvas
        get() {
            if (_canvas == null) startRecording()
            return _canvas!!
        }

    /// PictureLayerでの描画の録画を開始する
    private fun startRecording() {
        currentLayer = PictureLayer()
        recorder = PictureRecorder()
        _canvas = recorder!!.beginRecording(estimatedBounds)
        containerLayer.append(currentLayer!!)
    }


    fun stopRecordingIfNeeded() {
        if (!isRecording) return

        currentLayer!!.picture = recorder!!.finishRecordingAsPicture()
        currentLayer = null
        recorder = null
        _canvas = null
    }

    /**
     * 渡されたRenderObjectのpaint()を呼ぶ
     *
     * [RenderObject.isRepaintBoundary] == trueの場合、別で処理するため[compositeChild]を呼ぶ
     */
    fun paintChild(child: RenderObject, offset: Offset) {
        if (child.isRepaintBoundary) {
            stopRecordingIfNeeded()
            compositeChild(child, offset)
        } else {
            child.needsPaint = false
            child.paint(this, offset)
        }
    }

    /**
     * [RenderObject.isRepaintBoundary] == trueのものをpaintする
     *
     * [RenderObject.needsPaint]が立っていない場合は前のものを再利用する
     */
    fun compositeChild(child: RenderObject, offset: Offset) {
        if (child.needsPaint) {
            repaintCompositedChild(child)
        }
        val childOffsetLayer = child.layer as OffsetLayer
        childOffsetLayer.offset = offset
        appendLayer(childOffsetLayer)
    }

    fun appendLayer(layer: Layer) {
        layer.remove()
        containerLayer.append(layer)
    }


    fun pushLayer(
        childLayer: ContainerLayer,
        painter: PaintingContextCallback,
        offset: Offset,
        childPaintBounds: Rect? = null,
    ) {
        if (childLayer.children.isNotEmpty()) {
            childLayer.removeAllChildren()
        }

        // 新しいレイヤーを作るときは現在のPictureLayerを終了する
        stopRecordingIfNeeded()
        containerLayer.append(childLayer)

        // 新しいPaintingContextで再帰的に動作させる
        val childContext = PaintingContext(childLayer, childPaintBounds ?: estimatedBounds)
        painter(childContext, offset)
        childContext.stopRecordingIfNeeded()
    }

    fun pushOpacity(
        offset: Offset,
        alpha: Int,
        painter: PaintingContextCallback,
        oldLayer: OpacityLayer? = null,
    ): OpacityLayer {
        val layer = oldLayer ?: OpacityLayer()
        layer.let {
            it.alpha = alpha
            it.offset = offset
        }
        pushLayer(layer, painter, Offset.zero)
        return layer
    }

    fun pushClipPath(
        offset: Offset,
        bounds: Rect,
        clipPath: Path,
        painter: PaintingContextCallback,
        clipBehavior: Clip = Clip.AntiAlias,
        oldLayer: ClipPathLayer? = null,
    ): ClipPathLayer {
        val offsetBounds = bounds.offset(offset.dx.toFloat(), offset.dy.toFloat())
        val offsetClipPath = clipPath.offset(offset.dx.toFloat(), offset.dy.toFloat())
        val layer = oldLayer ?: ClipPathLayer(offsetClipPath)
        layer.let {
            it.clipPath = offsetClipPath
            it.clipBehavior = clipBehavior
        }
        pushLayer(layer, painter, offset, childPaintBounds = offsetBounds)
        return layer
    }

    fun pushClipRRect(
        offset: Offset,
        bounds: Rect,
        clipRRect: RRect,
        painter: PaintingContextCallback,
        clipBehavior: Clip = Clip.AntiAlias,
        oldLayer: ClipRRectLayer? = null,
    ): ClipRRectLayer {
        val offsetBounds = bounds.offset(offset.dx.toFloat(), offset.dy.toFloat())
        val offsetClipRRect = clipRRect.makeOffset(offset)
        val layer = oldLayer ?: ClipRRectLayer(offsetClipRRect)
        layer.let {
            it.clipRRect = offsetClipRRect
            it.clipBehavior = clipBehavior
        }
        pushLayer(layer, painter, offset, childPaintBounds = offsetBounds)
        return layer
    }

    fun pushClipRect(
        offset: Offset,
        clipRect: Rect,
        painter: PaintingContextCallback,
        clipBehavior: Clip = Clip.AntiAlias,
        oldLayer: ClipRectLayer? = null,
    ): ClipRectLayer {
        val offsetClipRect = clipRect.makeOffset(offset)
        val layer = oldLayer ?: ClipRectLayer(offsetClipRect)
        layer.let {
            it.clipRect = offsetClipRect
            it.clipBehavior = clipBehavior
        }
        pushLayer(layer, painter, offset, childPaintBounds = offsetClipRect)
        return layer
    }
}

typealias PaintingContextCallback = (PaintingContext, Offset) -> Unit