package com.exory550.exorypad.model

import java.util.Date

data class Note(
    val metadata: NoteMetadata = Defaults.metadata,
    private val contents: NoteContents = Defaults.contents
) {
    val id: Long get() = metadata.metadataId
    val text: String get() = contents.text ?: ""
    val draftText: String get() = contents.draftText ?: ""
    val title: String get() = metadata.title
    val date: Date get() = metadata.date
}
