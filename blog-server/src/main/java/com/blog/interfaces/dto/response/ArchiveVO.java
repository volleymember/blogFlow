package com.blog.interfaces.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 归档视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchiveVO {
    private Long id;
    private String title;
    private String slug;
    private Integer visibility;
    private LocalDateTime createTime;
    private String yearMonth;
}
