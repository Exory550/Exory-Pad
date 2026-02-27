package com.exory550.exorypad.data

import com.exory550.exorypad.Database
import com.exory550.exorypad.model.CrossRef
import com.exory550.exorypad.model.Defaults
import com.exory550.exorypad.model.Note
import com.exory550.exorypad.model.NoteContents
import com.exory550.exorypad.model.NoteMetadata
import com.exory550.exorypad.model.SortOrder
import com.exory550.exorypad.model.SortOrder.DateAscending
import com.exory550.exorypad.model.SortOrder.DateDescending
import com.exory550.exorypad.model.SortOrder.TitleAscending
import com.exory550.exorypad.model.SortOrder.TitleDescending
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import java.text.Collator
import java.util.Date
import kotlinx.coroutines.flow.map

class ExorypadRepository(
    private val database: Database
) {
    val savedDraftId get() = database.noteMetadataQueries.getDraftId()
        .asFlow()
        .mapToList()
        .map {
            it.firstOrNull() ?: -1L
        }

    fun noteMetadataFlow(order: SortOrder) = with(database.noteMetadataQueries) {
        when(order) {
            DateDescending -> getSortedByDateDescending().asFlow().mapToList()
            DateAscending -> getSortedByDateAscending().asFlow().mapToList()
            TitleDescending, TitleAscending -> {
                val collator = Collator.getInstance()
                getUnsorted().asFlow().mapToList().map { notes ->
                    val sortedNotes = notes.sortedWith { a, b ->
                        collator.compare(a.title, b.title)
                    }
                    if (order == TitleDescending) sortedNotes.reversed() else sortedNotes
                }
            }
        }
    }

    fun getNote(id: Long): Note = with(database) {
        transactionWithResult {
            val metadata = noteMetadataQueries.get(id).executeAsList().lastOrNull() ?: Defaults.metadata
            val crossRef = crossRefQueries.get(metadata.metadataId).executeAsList().lastOrNull() ?: Defaults.crossRef
            val contents = noteContentsQueries.get(crossRef.contentsId).executeAsList().lastOrNull() ?: Defaults.contents

            Note(
                metadata = metadata,
                contents = contents
            )
        }
    }

    fun getNotes(metadataList: List<NoteMetadata>): List<Note> = with(database) {
        transactionWithResult {
            val crossRefList = crossRefQueries.getMultiple(metadataList.map { it.metadataId }).executeAsList()
            val contentsList = noteContentsQueries.getMultiple(crossRefList.map { it.contentsId }).executeAsList()

            crossRefList.map { crossRef ->
                Note(
                    metadata = metadataList.find {
                        it.metadataId == crossRef.metadataId
                    } ?: Defaults.metadata,
                    contents = contentsList.find {
                        it.contentsId == crossRef.contentsId
                    } ?: Defaults.contents
                )
            }
        }
    }

    suspend fun saveNote(
        id: Long = -1,
        text: String,
        date: Date = Date(),
        draftText: String? = null,
        onSuccess: suspend (Long) -> Unit = {}
    ) = try {
        val crossRef = database.crossRefQueries.get(id).executeAsOneOrNull()

        val metadata = NoteMetadata(
            metadataId = crossRef?.metadataId ?: -1,
            title = text.substringBefore("\n"),
            date = date,
            hasDraft = draftText != null
        )

        val contents = NoteContents(
            contentsId = crossRef?.contentsId ?: -1,
            text = text,
            draftText = draftText
        )

        with(database) {
            crossRef?.let {
                noteMetadataQueries.update(metadata)
                noteContentsQueries.update(contents)
                onSuccess(id)
            } ?: run {
                noteMetadataQueries.insert(metadata)
                noteContentsQueries.insert(contents)

                val newCrossRef = CrossRef(
                    metadataId = noteMetadataQueries.getIndex().executeAsOne(),
                    contentsId = noteContentsQueries.getIndex().executeAsOne()
                )

                crossRefQueries.insert(newCrossRef)
                onSuccess(newCrossRef.metadataId)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    suspend fun deleteNote(id: Long, onSuccess: suspend () -> Unit = {}) = try {
        with(database) {
            crossRefQueries.get(id).executeAsOneOrNull()?.let {
                noteMetadataQueries.delete(it.metadataId)
                noteContentsQueries.delete(it.contentsId)
                crossRefQueries.delete(id)
            }
        }

        onSuccess()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    suspend fun deleteNotes(ids: List<Long>, onSuccess: suspend () -> Unit) = try {
        with(database) {
            crossRefQueries.getMultiple(ids).executeAsList().let { refs ->
                noteMetadataQueries.deleteMultiple(refs.map { it.metadataId })
                noteContentsQueries.deleteMultiple(refs.map { it.contentsId })
                crossRefQueries.deleteMultiple(ids)
            }
        }

        onSuccess()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
