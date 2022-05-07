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

    val shell = Shell(taskRunners, null, windowWidth, windowHeight)
    shell.initRasterThread()

    shell.run {
        appMain()
    }

    while (!shell.glView.windowShouldClose()) {
        Thread.sleep(30)
        shell.onVsync()

        shell.glView.pollEvents()
    }
    taskRunners.terminateAll()
}