package com.exory550.exorypad.model

import java.util.Date

data class NoteMetadata(
    val metadataId: Long,
    val title: String,
    val date: Date,
    val hasDraft: Boolean
)
