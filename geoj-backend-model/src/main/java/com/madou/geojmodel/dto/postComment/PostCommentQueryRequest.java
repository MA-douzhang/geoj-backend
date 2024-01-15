package com.madou.geojmodel.dto.postComment;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.madou.geojcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 查询请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostCommentQueryRequest extends PageRequest implements Serializable {

    /**
     * 评论帖子id
     */
    private Long postId;

    private static final long serialVersionUID = 1L;
}
