package engine

fun runFlume(
    appMain: () -> Unit,
    windowWidth: Int = 640,
    windowHeight: Int = 480,
) {
    val taskRunners = TaskRunners(
        rasterTaskRunner = TaskRunner("RasterTaskRunner"),
        uiTaskRunner = TaskRunner("UITaskRunner"),
    )

    println("task created")

    val glView = GLView(windowWidth, windowHeight)
    val shell = Shell(taskRunners, glView, null, windowWidth, windowHeight)
    shell.initRasterThread()

    shell.run {
        appMain()
    }

    while (!glView.windowShouldClose()) {
        Thread.sleep(30)
        shell.onVsync()

        glView.pollEvents()
    }
    taskRunners.terminateAll()
}