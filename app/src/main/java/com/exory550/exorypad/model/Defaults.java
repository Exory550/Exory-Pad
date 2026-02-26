package com.exory550.exorypad.model;

import java.util.Date;

public final class Defaults {
    public static final NoteMetadata metadata = new NoteMetadata(
        -1,
        "",
        new Date(),
        false
    );

    public static final NoteContents contents = new NoteContents(
        -1,
        null,
        null
    );

    public static final CrossRef crossRef = new CrossRef(
        -1,
        -1
    );

    private Defaults() {}
}
