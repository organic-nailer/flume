package engine

class Shell(
    val taskRunners: TaskRunners,
    var glView: GLView,
    var rasterizer: Rasterizer?
)