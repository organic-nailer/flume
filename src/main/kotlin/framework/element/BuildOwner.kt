package framework.element

class BuildOwner(
    private val onBuildScheduled: () -> Unit,
) {
    private val dirtyElements: MutableList<Element> = mutableListOf()
    private var dirtyElementsNeedsResorting: Boolean? = null
    private var scheduledFlushDirtyElements: Boolean = false

    fun scheduleBuildFor(element: Element) {
        if (element.inDirtyList) {
            dirtyElementsNeedsResorting = true
            return
        }
        if (!scheduledFlushDirtyElements) {
            onBuildScheduled()
        }
        dirtyElements.add(element)
        element.inDirtyList = true
    }

    fun buildScope(callback: (() -> Unit)? = null) {
        if (callback == null && dirtyElements.isEmpty()) return
        scheduledFlushDirtyElements = true
        if (callback != null) {
            dirtyElementsNeedsResorting = false
            callback()
        }
        dirtyElements.sortBy { it }
        dirtyElementsNeedsResorting = false
        var dirtyCount = dirtyElements.size
        var index = 0
        while (index < dirtyCount) {
            val element = dirtyElements[index]
            element.rebuild()
            index++
            if (dirtyCount < dirtyElements.size || dirtyElementsNeedsResorting!!) {
                dirtyElements.sortBy { it }
                dirtyElementsNeedsResorting = false
                dirtyCount = dirtyElements.size
                // dirtyなものが追加されている場合があるので、dirtyでない場所まで戻る
                while (index > 0 && dirtyElements[index - 1].dirty) {
                    index--
                }
            }
        }
        for (dirtyElement in dirtyElements) {
            dirtyElement.inDirtyList = false
        }
        dirtyElements.clear()
        scheduledFlushDirtyElements = false
        dirtyElementsNeedsResorting = null
    }
}