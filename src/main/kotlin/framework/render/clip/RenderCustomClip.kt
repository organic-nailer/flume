package framework.render.clip

import common.Clip
import framework.render.RenderBox
import framework.render.RenderProxyBox

abstract class RenderCustomClip<T>(
    val clipper: CustomClipper<T>? = null, val clipBehavior: Clip = Clip.AntiAlias,
    child: RenderBox? = null,
) : RenderProxyBox(child) {
    protected val clip: T? get() = clipper?.getClip(size) ?: defaultClip
    protected abstract val defaultClip: T
}