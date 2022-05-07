package framework.render.clip

import common.Size

abstract class CustomClipper<T> {
    abstract fun getClip(size: Size): T

    abstract fun shouldReclip(oldClipper: CustomClipper<T>): Boolean
}