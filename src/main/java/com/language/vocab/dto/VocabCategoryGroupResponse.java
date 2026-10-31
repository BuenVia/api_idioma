package com.language.vocab.dto;

import com.language.common.enums.Level;

import java.util.List;

/**
 * One category in the response of {@code GET /{language}/vocab/getallgrouped}.
 * {@code categoryId} and {@code categoryName} are both null for the group of
 * subjects that have no category.
 */
public record VocabCategoryGroupResponse(
        Long categoryId,
        String categoryName,
        List<SubjectGroup> subjects
) {

    public record SubjectGroup(Long subjectId, String subjectName, Level level, List<VocabItem> vocabs) {}

    public record VocabItem(Long id, String term, String translation) {}
}
