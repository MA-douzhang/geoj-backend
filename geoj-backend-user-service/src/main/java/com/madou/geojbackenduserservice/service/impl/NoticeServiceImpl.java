package com.madou.geojbackenduserservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.madou.geojbackenduserservice.mapper.NoticeMapper;
import com.madou.geojbackenduserservice.service.NoticeService;
import com.madou.geojbackenduserservice.service.PostService;
import com.madou.geojbackenduserservice.service.UserService;
import com.madou.geojcommon.common.ErrorCode;
import com.madou.geojcommon.exception.BusinessException;
import com.madou.geojmodel.dto.notice.NoticeUpdateRequest;
import com.madou.geojmodel.entity.Notice;
import com.madou.geojmodel.entity.Post;
import com.madou.geojmodel.entity.User;
import com.madou.geojmodel.vo.NoticeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author MA_dou
 * @description 针对表【notice(通知)】的数据库操作Service实现
 * @createDate 2023-04-10 15:03:50
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice>
        implements NoticeService {

    @Resource
    private UserService userService;

    @Lazy
    @Resource
    private PostService postService;
    @Override
    public long addNotice(Notice notice) {
        //请求参数是否为空
        if (notice == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //todo 判断敏感字符
        //信息插入帖子表
        notice.setId(null);
        boolean result = this.save(notice);
        Long noticeId = notice.getId();
        if (!result || noticeId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "发布帖子失败");
        }

        return noticeId;
    }

    @Override
    public boolean deleteNotice(Long id, User loginUser) {
        Notice notice = this.getById(id);
        if (notice == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "通知不存在");
        }
        //是否为通知的接收者或者管理员
        Long userId = notice.getReceiverId();
        if (!userService.isAdmin(loginUser) && userId != loginUser.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return this.removeById(id);
    }

    @Override
    public boolean updateNotice(NoticeUpdateRequest noticeUpdateRequest, User loginUser) {
        Notice oldNotice = this.getById(noticeUpdateRequest.getId());
        if (oldNotice == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "通知不存在");
        }
        Long userId = oldNotice.getReceiverId();
        //是否为帖子创建人或者管理员
        if (!userService.isAdmin(loginUser) && userId != loginUser.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        Integer noticeState = noticeUpdateRequest.getNoticeState();
        if (noticeState != 0 && noticeState != 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeUpdateRequest, notice);
        return this.updateById(notice);
    }


    //todo 从缓存中取
    @Override
    public NoticeVO getNoticeInfoById(Long id) {
        Notice notice = this.getById(id);
        if (notice == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "通知不存在");
        }
        //查询发送者的信息
        Long senderId = notice.getSenderId();
        User sender = userService.getById(senderId);
        String senderUsername = sender.getUserName();


        //查询接收者的信息
        Long receiverId = notice.getReceiverId();
        User receiver = userService.getById(receiverId);
        String receiverUsername = receiver.getUserName();

        //查询对象内容（帖子）
        Long targetId = notice.getTargetId();
        //代理 todo 可以采用反射方法对应不同的通知
        Post target = postService.getById(targetId);
        String content = target.getContent();
        if (content.length() > 20) {
            content = content.substring(0, 20) + "...";
        }
        NoticeVO noticeVO = new NoticeVO();
        BeanUtils.copyProperties(notice, noticeVO);
        noticeVO.setReceiverName(receiverUsername);
        noticeVO.setSenderName(senderUsername);
        noticeVO.setTargetContent(content);
        return noticeVO;
    }

    @Override
    public NoticeVO getNoticeInfoById(Long id, String receiverUsername) {
        Notice notice = this.getById(id);
        if (notice == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "通知不存在");
        }
        //查询发送者的信息
        Long senderId = notice.getSenderId();
        User sender = userService.getById(senderId);
        String senderUsername = sender.getUserName();

        //查询对象内容（帖子）
        Long targetId = notice.getTargetId();
        //代理 todo 可以采用反射方法对应不同的通知
        Post target = postService.getById(targetId);
        String content = target.getContent();
        if (content.length() > 20) {
            content = content.substring(0, 20) + "...";
        }
        NoticeVO noticeVO = new NoticeVO();
        BeanUtils.copyProperties(notice, noticeVO);
        noticeVO.setReceiverName(receiverUsername);
        noticeVO.setSenderName(senderUsername);
        noticeVO.setTargetContent(content);
        return noticeVO;
    }
    @Override
    public List<NoticeVO> getNoticeList(User loginUser) {
        long userId = loginUser.getId();
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receiverId",userId).orderByDesc("id");
        List<Notice> noticeList = this.list(queryWrapper);
        List<NoticeVO> noticeVOList = new ArrayList<>();
        if (noticeList != null){
            noticeVOList = noticeList.stream().map(notice ->
                            getNoticeInfoById(notice.getId(), loginUser.getUserName()))
                            .collect(Collectors.toList());
        }
        return noticeVOList;
    }
}
