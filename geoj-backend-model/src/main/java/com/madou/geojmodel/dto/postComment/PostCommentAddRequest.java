package com.madou.geojmodel.dto.postComment;

import lombok.Data;

import java.io.Serializable;

/**
 * 帖子评论请求体
 */
@Data
public class PostCommentAddRequest implements Serializable {

    /**
     * 帖子id
     */
    private Long postId;

    /**
     * 内容
     */
    private String content;

    /**
     * 父id
     */
    private Long pid;

}
