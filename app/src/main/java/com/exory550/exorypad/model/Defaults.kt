package com.exory550.exorypad.model

import java.util.Date

object Defaults {
    val metadata = NoteMetadata(
        metadataId = -1,
        title = "",
        date = Date(),
        hasDraft = false
    )

    val contents = NoteContents(
        contentsId = -1,
        text = null,
        draftText = null
    )

    val crossRef = CrossRef(
        metadataId = -1,
        contentsId = -1
    )
}
