package life.majiang.community.service;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import life.majiang.community.dto.CommentDTO;
import life.majiang.community.enums.CommentTypeEnum;
import life.majiang.community.enums.NotificationStatusEnum;
import life.majiang.community.enums.NotificationTypeEnum;
import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.exception.CustomizeException;
import life.majiang.community.mapper.*;
import life.majiang.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentExtMapper commentExtMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    //保存用户评论
    @Transactional
    public void insert(Comment comment, User commentator){
        //判断主题id或父主题id，存在在进行后续操作
        if(comment.getParentId() == null || comment.getParentId() == 0){
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        //判断评论类型不为空且评论类型符合（1/2）
        if(comment.getType() == null || !CommentTypeEnum.isExit(comment.getType())){
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }

        //二级评论(仍要先查询问题是否还存在，二级评论只存在存储评论至数据库，不用关心评论数)
        if(comment.getType() == CommentTypeEnum.COMMENT.getType() ){
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if(dbComment == null){
                //一级评论可能已删除
                throw  new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
            if (question == null) {
                //问题可能已删除
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);

            //添加一级评论评论数
            int insertCount = incCommentCount(comment);
            System.out.println(insertCount);

            //创建二级评论通知
            createNotify(comment, dbComment.getCommentator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_COMMENT, question.getId());
//            createNotify(comment,dbComment.getCommentator(),NotificationTypeEnum.REPLY_COMMENT);

        //一级评论
        }else{
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if(question == null){
                //主题可能已删除
                throw  new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            //添加主题一级评论评论数
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);

            //将评论更新至数据库
            commentMapper.insert(comment);

            //创建一级评论通知
            createNotify(comment, question.getCreator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_QUESTION, question.getId());
//            createNotify(comment,question.getCreator(),NotificationTypeEnum.REPLY_QUESTION);
        }
    }

    //根据回复id 获取二级回复
    public List<CommentDTO> listByTargetId(Integer id, CommentTypeEnum type) {

        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo((long)id)
                .andTypeEqualTo(type.getType());
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);

        if (comments.size() == 0) {
            return new ArrayList<>();
        }
        // 获取去重的评论人
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList();
        userIds.addAll(commentators);


        // 获取评论人并转换为 Map
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));


        // 转换 comment 为 commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOS;
    }

    //添加点赞数
    public int incLikeCount(Long commentId) {
        //添加一级评论评论数
        Comment parentComment = new Comment();
        parentComment.setId(commentId);
        parentComment.setLikeCount(1L);
        int i1 = commentExtMapper.incLikeCount(parentComment);
        return i1;
    }

    //添加一级评论评论数
    public int incCommentCount(Comment comment){
        Comment parentComment = new Comment();
        parentComment.setId(comment.getParentId());
        parentComment.setCommentCount(1);
        int insertCount = commentExtMapper.incCommentCount(parentComment);
        return insertCount;
    }

    //创建通知
    private void createNotify(Comment comment, Long receiver, String notifierName, String outerTitle, NotificationTypeEnum notificationType, Long outerId) {
        if (receiver == comment.getCommentator()) {
            return;
        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(notificationType.getType());
        notification.setOuterid(outerId);
        notification.setNotifier(comment.getCommentator());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setReceiver(receiver);
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        notificationMapper.insert(notification);
    }












}
