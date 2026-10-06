package ru.drmemex.classifieds.common.util.pagination;

import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.common.util.pagination.dto.PageResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PaginationUtilsTest {

    @Test
    void applyPagination_ShouldSetCorrectOffsetAndLimit() {

        Query query = mock(Query.class);

        PageRequest pageRequest =
                new PageRequest(
                        2,
                        10
                );

        PaginationUtils.applyPagination(
                query,
                pageRequest
        );

        verify(query)
                .setFirstResult(20);

        verify(query)
                .setMaxResults(10);
    }

    @Test
    void buildPageResponse_ShouldCalculateTotalPages_WhenElementsDivideExactly() {

        List<String> content =
                List.of(
                        "first",
                        "second"
                );

        PageRequest pageRequest =
                new PageRequest(
                        0,
                        10
                );

        PageResponse<String> result =
                PaginationUtils.buildPageResponse(
                        content,
                        pageRequest,
                        20
                );

        assertEquals(
                content,
                result.content()
        );

        assertEquals(
                0,
                result.page()
        );

        assertEquals(
                10,
                result.size()
        );

        assertEquals(
                20,
                result.totalElements()
        );

        assertEquals(
                2,
                result.totalPages()
        );
    }

    @Test
    void buildPageResponse_ShouldRoundTotalPagesUp() {

        PageRequest pageRequest =
                new PageRequest(
                        0,
                        10
                );

        PageResponse<String> result =
                PaginationUtils.buildPageResponse(
                        List.of(),
                        pageRequest,
                        21
                );

        assertEquals(
                3,
                result.totalPages()
        );
    }
}