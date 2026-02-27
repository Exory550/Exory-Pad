package com.exory550.exorypad.ui.previews

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.exory550.exorypad.R
import com.exory550.exorypad.model.Note
import com.exory550.exorypad.model.NoteContents
import com.exory550.exorypad.model.NoteMetadata
import com.exory550.exorypad.ui.components.AppBarText
import com.exory550.exorypad.ui.components.BackButton
import com.exory550.exorypad.ui.components.DeleteButton
import com.exory550.exorypad.ui.components.EditButton
import com.exory550.exorypad.ui.components.NoteViewEditMenu
import com.exory550.exorypad.ui.content.ViewNoteContent
import java.util.Date

@Composable
private fun ViewNote(note: Note) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { BackButton() },
                title = { AppBarText(note.title) },
                backgroundColor = colorResource(id = R.color.primary),
                actions = {
                    EditButton()
                    DeleteButton()
                    NoteViewEditMenu()
                }
            )
        },
        content = { ViewNoteContent(note.text) }
    )
}

@Preview
@Composable
fun ViewNotePreview() = MaterialTheme {
    ViewNote(
        note = Note(
            metadata = NoteMetadata(
                metadataId = -1,
                title = "Title",
                date = Date(),
                hasDraft = false
            ),
            contents = NoteContents(
                contentsId = -1,
                text = "This is some text",
                draftText = null
            )
        ),
    )
}
