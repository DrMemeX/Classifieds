package ru.drmemex.classifieds.common.util.pagination;

import jakarta.persistence.Query;
import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.common.util.pagination.dto.PageResponse;

import java.util.List;

public final class PaginationUtils {

    private PaginationUtils() {
    }

    public static void applyPagination(
            Query query,
            PageRequest pageRequest
    ) {
        query.setFirstResult(
                pageRequest.page() * pageRequest.size()
        );

        query.setMaxResults(
                pageRequest.size()
        );
    }

    public static <T> PageResponse<T> buildPageResponse(
            List<T> content,
            PageRequest pageRequest,
            long totalElements
    ) {
        int totalPage = (int) Math.ceil(
                (double) totalElements / pageRequest.size()
        );

        return new PageResponse<>(
                content,
                pageRequest.page(),
                pageRequest.size(),
                totalElements,
                totalPage
        );
    }
}