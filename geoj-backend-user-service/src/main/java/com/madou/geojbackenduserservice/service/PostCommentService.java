package com.madou.geojbackenduserservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.madou.geojmodel.dto.postComment.PostCommentAddRequest;
import com.madou.geojmodel.dto.postComment.PostCommentQueryRequest;
import com.madou.geojmodel.entity.PostComment;
import com.madou.geojmodel.entity.User;
import com.madou.geojmodel.vo.PostCommentVO;

import java.util.List;

/**
* @author MA_dou
* @description 针对表【post_comment(帖子)】的数据库操作Service
* @createDate 2023-02-25 17:17:07
*/
public interface PostCommentService extends IService<PostComment> {
    /**
     * 根据帖子id获取评论
     *
     * @param postCommentId
     * @param userId
     * @return
     */
    List<PostCommentVO> getPostCommentVOList(Long postCommentId, Long userId);

    /**
     * 根据帖子id获取评论（查询缓存）
     *
     * @param postCommentId
     * @param userId
     * @return
     */
    List<PostCommentVO> getPostCommentVOListCache(Long postCommentId,Long userId);

    /**
     * 添加评论
     * @param postCommentAddRequest
     * @param loginUser
     * @return
     */
    boolean addComment(PostCommentAddRequest postCommentAddRequest, User loginUser);
}
