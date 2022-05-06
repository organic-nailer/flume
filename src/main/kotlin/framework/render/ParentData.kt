package framework.render

import common.Offset

interface ParentData

data class BoxParentData(
    var offset: Offset = Offset.zero,
) : ParentData