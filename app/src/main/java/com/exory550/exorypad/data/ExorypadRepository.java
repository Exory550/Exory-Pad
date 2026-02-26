package com.exory550.exorypad.data;

import com.exory550.exorypad.Database;
import com.exory550.exorypad.model.CrossRef;
import com.exory550.exorypad.model.Defaults;
import com.exory550.exorypad.model.Note;
import com.exory550.exorypad.model.NoteContents;
import com.exory550.exorypad.model.NoteMetadata;
import com.exory550.exorypad.model.SortOrder;
import com.exory550.exorypad.model.SortOrder.DateAscending;
import com.exory550.exorypad.model.SortOrder.DateDescending;
import com.exory550.exorypad.model.SortOrder.TitleAscending;
import com.exory550.exorypad.model.SortOrder.TitleDescending;
import com.squareup.sqldelight.runtime.coroutines.asFlow;
import com.squareup.sqldelight.runtime.coroutines.mapToList;
import java.text.Collator;
import java.util.Date;
import kotlinx.coroutines.flow.map;

public class ExorypadRepository {
    private final Database database;

    public ExorypadRepository(Database database) {
        this.database = database;
    }

    public Flow<Long> getSavedDraftId() {
        return database.getNoteMetadataQueries().getDraftId()
            .asFlow()
            .mapToList()
            .map(list -> {
                if (list.isEmpty()) return -1L;
                return list.get(0);
            });
    }

    public Flow<List<NoteMetadata>> noteMetadataFlow(SortOrder order) {
        switch(order) {
            case DateDescending:
                return database.getNoteMetadataQueries().getSortedByDateDescending()
                    .asFlow().mapToList();
            case DateAscending:
                return database.getNoteMetadataQueries().getSortedByDateAscending()
                    .asFlow().mapToList();
            case TitleDescending:
            case TitleAscending:
                Collator collator = Collator.getInstance();
                return database.getNoteMetadataQueries().getUnsorted()
                    .asFlow().mapToList()
                    .map(notes -> {
                        List<NoteMetadata> sortedNotes = notes.stream()
                            .sorted((a, b) -> collator.compare(a.getTitle(), b.getTitle()))
                            .collect(Collectors.toList());
                        if (order == TitleDescending) {
                            Collections.reverse(sortedNotes);
                        }
                        return sortedNotes;
                    });
            default:
                return database.getNoteMetadataQueries().getUnsorted()
                    .asFlow().mapToList();
        }
    }

    public Note getNote(long id) {
        return database.transactionWithResult(() -> {
            NoteMetadata metadata = database.getNoteMetadataQueries().get(id)
                .executeAsList().stream()
                .reduce((first, second) -> second)
                .orElse(Defaults.metadata);
            
            CrossRef crossRef = database.getCrossRefQueries().get(metadata.getMetadataId())
                .executeAsList().stream()
                .reduce((first, second) -> second)
                .orElse(Defaults.crossRef);
            
            NoteContents contents = database.getNoteContentsQueries().get(crossRef.getContentsId())
                .executeAsList().stream()
                .reduce((first, second) -> second)
                .orElse(Defaults.contents);

            return new Note(metadata, contents);
        });
    }

    public List<Note> getNotes(List<NoteMetadata> metadataList) {
        return database.transactionWithResult(() -> {
            List<Long> metadataIds = metadataList.stream()
                .map(NoteMetadata::getMetadataId)
                .collect(Collectors.toList());
            
            List<CrossRef> crossRefList = database.getCrossRefQueries()
                .getMultiple(metadataIds).executeAsList();
            
            List<Long> contentsIds = crossRefList.stream()
                .map(CrossRef::getContentsId)
                .collect(Collectors.toList());
            
            List<NoteContents> contentsList = database.getNoteContentsQueries()
                .getMultiple(contentsIds).executeAsList();

            return crossRefList.stream()
                .map(crossRef -> {
                    NoteMetadata metadata = metadataList.stream()
                        .filter(m -> m.getMetadataId() == crossRef.getMetadataId())
                        .findFirst()
                        .orElse(Defaults.metadata);
                    
                    NoteContents contents = contentsList.stream()
                        .filter(c -> c.getContentsId() == crossRef.getContentsId())
                        .findFirst()
                        .orElse(Defaults.contents);
                    
                    return new Note(metadata, contents);
                })
                .collect(Collectors.toList());
        });
    }

    public void saveNote(long id, String text, Date date, String draftText, Function1<Long, Unit> onSuccess) {
        try {
            CrossRef crossRef = database.getCrossRefQueries().get(id).executeAsOneOrNull();

            NoteMetadata metadata = new NoteMetadata(
                crossRef != null ? crossRef.getMetadataId() : -1,
                text.substring(0, text.indexOf('\n')),
                date,
                draftText != null
            );

            NoteContents contents = new NoteContents(
                crossRef != null ? crossRef.getContentsId() : -1,
                text,
                draftText
            );

            if (crossRef != null) {
                database.getNoteMetadataQueries().update(metadata);
                database.getNoteContentsQueries().update(contents);
                onSuccess.invoke(id);
            } else {
                database.getNoteMetadataQueries().insert(metadata);
                database.getNoteContentsQueries().insert(contents);

                CrossRef newCrossRef = new CrossRef(
                    database.getNoteMetadataQueries().getIndex().executeAsOne(),
                    database.getNoteContentsQueries().getIndex().executeAsOne()
                );

                database.getCrossRefQueries().insert(newCrossRef);
                onSuccess.invoke(newCrossRef.getMetadataId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteNote(long id, Function0<Unit> onSuccess) {
        try {
            CrossRef crossRef = database.getCrossRefQueries().get(id).executeAsOneOrNull();
            if (crossRef != null) {
                database.getNoteMetadataQueries().delete(crossRef.getMetadataId());
                database.getNoteContentsQueries().delete(crossRef.getContentsId());
                database.getCrossRefQueries().delete(id);
            }
            onSuccess.invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteNotes(List<Long> ids, Function0<Unit> onSuccess) {
        try {
            List<CrossRef> refs = database.getCrossRefQueries().getMultiple(ids).executeAsList();
            
            List<Long> metadataIds = refs.stream()
                .map(CrossRef::getMetadataId)
                .collect(Collectors.toList());
            
            List<Long> contentsIds = refs.stream()
                .map(CrossRef::getContentsId)
                .collect(Collectors.toList());

            database.getNoteMetadataQueries().deleteMultiple(metadataIds);
            database.getNoteContentsQueries().deleteMultiple(contentsIds);
            database.getCrossRefQueries().deleteMultiple(ids);

            onSuccess.invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
