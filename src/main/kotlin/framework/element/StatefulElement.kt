package framework.element

import framework.widget.State
import framework.widget.StatefulWidget
import framework.widget.Widget

class StatefulElement(widget: StatefulWidget) : ComponentElement(widget) {
    val state: State<StatefulWidget>
        get() = stateInternal!!
    private var stateInternal: State<StatefulWidget>? = null
    private var didChangeDependencies = false
    init {
        stateInternal = widget.createState() as State<StatefulWidget>
        state.element = this
        state.widgetInternal = widget
    }

    override fun build(): Widget = state.build(this)

    override fun firstBuild() {
        state.initState()
        state.didChangeDependencies()
        super.firstBuild()
    }

    override fun performRebuild() {
        if(didChangeDependencies) {
            state.didChangeDependencies()
            didChangeDependencies = false
        }
        super.performRebuild()
    }

    override fun update(newWidget: Widget) {
        super.update(newWidget)
        val oldWidget = state.widget

        dirty = true
        state.widgetInternal = widget as StatefulWidget
        state.didUpdateWidget(oldWidget)
        rebuild()
    }
}