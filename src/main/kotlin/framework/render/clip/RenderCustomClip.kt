package framework.render.clip

import common.Clip
import framework.render.RenderProxyBox

abstract class RenderCustomClip<T>(
    val clipper: CustomClipper<T>? = null, val clipBehavior: Clip = Clip.AntiAlias,
) : RenderProxyBox() {
    protected val clip: T? get() = clipper?.getClip(size) ?: defaultClip
    protected abstract val defaultClip: T
}