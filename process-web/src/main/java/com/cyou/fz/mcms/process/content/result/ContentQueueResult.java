package com.cyou.fz.mcms.process.content.result;

import com.cyou.fz.mcms.process.content.request.ContentRequest;

import java.util.List;

/**
 * Created by cnJason on 2016/11/28.
 */
public class ContentQueueResult {

    private List<ContentRequest> rows;
    private Long totalPages;
    private Long totalCount;


    public List<ContentRequest> getRows() {
        return rows;
    }

    public void setRows(List<ContentRequest> rows) {
        this.rows = rows;
    }

    public Long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Long totalPages) {
        this.totalPages = totalPages;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
