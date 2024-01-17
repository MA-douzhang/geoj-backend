package com.madou.geojmodel.dto.postComment;

import lombok.Data;

import java.io.Serializable;

/**
 * 帖子点赞请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class PostCommentThumbAddRequest implements Serializable {

    /**
     * 帖子评论 id
     */
    private Long postCommentId;

    private static final long serialVersionUID = 1L;
}
