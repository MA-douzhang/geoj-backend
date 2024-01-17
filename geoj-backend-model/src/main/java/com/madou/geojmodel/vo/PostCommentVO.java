package com.madou.geojmodel.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 帖子
 * @TableName post_comment
 */
@Data
public class PostCommentVO implements Serializable {
    /**
     * 评论id
     */
    private Long id;

    /**
     * 评论用户id
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 头像
     */
    private String avatarUrl;
    /**
     * 评论帖子id
     */
    private Long postId;

    /**
     * 评论内容(最大200字)
     */
    private String content;

    /**
     * 父id
     */
    private Long pid;

    /**
     * 状态 0 正常
     */
    private Integer commentState;


    /**
     * 点赞数
     */
    private Integer thumbNum;
    /**
     * 是否已点赞
     */
    private Boolean hasThumb;

    /**
     * 创建时间
     */
    private Date createTime;


}
