package com.madou.geojbackenduserservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.madou.geojbackenduserservice.mapper.PostCommentMapper;
import com.madou.geojbackenduserservice.mapper.PostThumbMapper;
import com.madou.geojbackenduserservice.service.NoticeService;
import com.madou.geojbackenduserservice.service.PostCommentService;
import com.madou.geojbackenduserservice.service.PostService;
import com.madou.geojbackenduserservice.service.UserService;
import com.madou.geojcommon.common.ErrorCode;
import com.madou.geojcommon.constant.CommonConstant;
import com.madou.geojcommon.constant.RedisConstant;
import com.madou.geojcommon.exception.BusinessException;
import com.madou.geojcommon.utils.SqlUtils;
import com.madou.geojmodel.dto.postComment.PostCommentAddRequest;
import com.madou.geojmodel.dto.postComment.PostCommentQueryRequest;
import com.madou.geojmodel.entity.*;
import com.madou.geojmodel.vo.PostCommentVO;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author MA_dou
 * @description 针对表【post_comment(帖子)】的数据库操作Service实现
 * @createDate 2023-02-25 17:17:07
 */
@Service
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment>
        implements PostCommentService {

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Lazy
    @Resource
    private PostService postService;

    @Resource
    private NoticeService noticeService;

    @Resource
    private PostThumbMapper postThumbMapper;

    @Override
    public List<PostCommentVO> getPostCommentVOList(Long postCommentId, Long userId) {
        if (postCommentId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //反复抢锁 保证数据一致性
        List<PostCommentVO> postCommentVOList = new ArrayList<>();
        //获取锁
        RLock lock = redissonClient.getLock(RedisConstant.REDIS_POST_COMMENT_KEY);
        try {
            while (true) {
                //反复抢锁，保证数据一致性
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    //当前获得锁的线程的id是
                    System.out.println("getLock" + Thread.currentThread().getId());
                    //查询信息
                    QueryWrapper<PostComment> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("postId", postCommentId);
                    queryWrapper.orderBy(true, false,
                            "thumbNum");
                    List<PostComment> postCommentList = this.list(queryWrapper);
                    //帖子有评论就加载评论
                    if (postCommentList != null && postCommentList.size() > 0) {
                        //查询评论用户的信息
                        List<Long> userIdList = postCommentList.stream().map(PostComment::getUserId).collect(Collectors.toList());
                        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                        userQueryWrapper.in("id", userIdList);
                        // userId -> user 用户id对应用户信息
                        Map<Long, List<User>> userListMap = userService.list(userQueryWrapper)
                                .stream().collect(Collectors.groupingBy(User::getId));
                        //将查出来的用户信息与评论信息对接
                        //将信息复制到返回类中
                        // 获取点赞 评论点赞
                        Map<Long, Boolean> postIdHasThumbMap = new HashMap<>();
                        Set<Long> postCommentIdSet = postCommentList.stream().map(PostComment::getId).collect(Collectors.toSet());
                        QueryWrapper<PostThumb> postThumbQueryWrapper = new QueryWrapper<>();
                        postThumbQueryWrapper.in("postId", postCommentIdSet);
                        postThumbQueryWrapper.eq("userId", userId);
                        postThumbQueryWrapper.eq("type", 1);

                        List<PostThumb> postPostThumbList = postThumbMapper.selectList(postThumbQueryWrapper);
                        postPostThumbList.forEach(postPostThumb -> postIdHasThumbMap.put(postPostThumb.getPostId(), true));
                        postCommentList.forEach(postComment -> {
                            PostCommentVO postCommentVO = new PostCommentVO();
                            BeanUtils.copyProperties(postComment, postCommentVO);
                            postCommentVOList.add(postCommentVO);
                        });
                        //将用户信息对接给评论
                        postCommentVOList.forEach(postCommentVO -> {
                            User user = userListMap.get(postCommentVO.getUserId()).get(0);
                            postCommentVO.setUsername(user.getUserName());
                            postCommentVO.setAvatarUrl(user.getUserAvatar());
                            postCommentVO.setHasThumb(postIdHasThumbMap.getOrDefault(postCommentVO.getId(), false));
                        });
                    }
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    //写缓存
                    try {
                        valueOperations.set(RedisConstant.REDIS_POST_COMMENT_KEY + postCommentId + userId, postCommentVOList);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }
                    return postCommentVOList;
                }
            }
        } catch (InterruptedException e) {
            log.error("getPostCommentVOList error", e);
        } finally {
            //只能自己释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return postCommentVOList;

    }

    @Override
    public List<PostCommentVO> getPostCommentVOListCache(Long postCommentId, Long userId) {
        //查询缓存
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        List<PostCommentVO> postCommentVOList = (List<PostCommentVO>) valueOperations.get(RedisConstant.REDIS_POST_COMMENT_KEY + postCommentId + userId);
        //缓存为空，查询数据库并写入缓存,不为空返回缓存数据
        return postCommentVOList == null ? getPostCommentVOList(postCommentId, userId) : postCommentVOList;
    }

    @Override
    public boolean addComment(PostCommentAddRequest postCommentAddRequest, User loginUser) {

        if (postCommentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //帖子id存在
        Long postId = postCommentAddRequest.getPostId();
        if (postId == null || postId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "帖子不存在");
        }
        //内容字数小于200，内容不能为空
        String content = postCommentAddRequest.getContent();
        if (StringUtils.isBlank(content) || content.length() == 0 || content.length() >= 200) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容字数不符合要求");
        }
        //判断评论的pid,pid为null代表该条评论pid是帖子的创建者，反之是回复者的id

        PostComment postComment = new PostComment();
        BeanUtils.copyProperties(postCommentAddRequest, postComment);
        //判断评论的父id
        Long pid = postComment.getPid();
        //获取帖子的创建者id
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "帖子不存在");
        }
        //默认为评论的父id为帖子的创建者
        if (pid == null) {
            postComment.setPid(post.getUserId());
        }
        final long userId = loginUser.getId();
        postComment.setUserId(userId);
        //查询当前帖子的评论缓存
        List<PostCommentVO> commentVOListCache = this.getPostCommentVOListCache(postId, userId);
        //抢锁更新数据库评论和更新缓存
        RLock lock = redissonClient.getLock(RedisConstant.REDIS_POST_COMMENT_UPDATE_KEY);
        try {
            while (true) {
                //反复抢锁，保证数据一致性
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    //当前获得锁的线程的id是
                    System.out.println("getLock" + Thread.currentThread().getId());
                    //保存评论
                    boolean result = this.save(postComment);
                    //脱敏
                    postComment = this.getById(postComment.getId());
                    PostCommentVO postCommentVO = new PostCommentVO();
                    BeanUtils.copyProperties(postComment, postCommentVO);
                    //添加默认值
                    postCommentVO.setAvatarUrl(loginUser.getUserAvatar());
                    postCommentVO.setUsername(loginUser.getUserName());
                    postCommentVO.setCommentState(0);
                    //向评论列表添加评论
                    commentVOListCache.add(postCommentVO);


                    //发送通知给帖子的创建者
                    Notice notice = new Notice();
                    notice.setSenderId(userId);
                    notice.setReceiverId(post.getUserId());
                    notice.setTargetId(postId);
                    notice.setContent(postComment.getContent());
                    //1为评论
                    notice.setContentType(1);
                    long addNotice = noticeService.addNotice(notice);

                    if (addNotice < 0) throw new BusinessException(ErrorCode.SYSTEM_ERROR, "通知失败");
                    //更新缓存
                    if (result) {
                        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                        try {
                            valueOperations.set(RedisConstant.REDIS_POST_COMMENT_KEY + postId, commentVOListCache);
                        } catch (Exception e) {
                            log.error("redis set key error", e);
                        }
                        return true;
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("addComment error", e);
            return false;
        } finally {
            //只能自己释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }
}
