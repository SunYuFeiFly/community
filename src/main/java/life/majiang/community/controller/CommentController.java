package life.majiang.community.controller;

import life.majiang.community.dto.CommentCreateDTO;
import life.majiang.community.dto.CommentDTO;
import life.majiang.community.dto.ResultDTO;
import life.majiang.community.enums.CommentTypeEnum;
import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.exception.CustomizeException;
import life.majiang.community.model.Comment;
import life.majiang.community.model.User;
import life.majiang.community.service.CommentService;
import life.majiang.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private QuestionService questionService;

    //保存用户评论
    @ResponseBody
    @RequestMapping(value = "/comment",method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        //首先检查User登录状态
        User user = (User) request.getSession().getAttribute("user");
        System.out.println(user);
        if (user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }

        //验证评论内容是否为空
        if (commentCreateDTO == null || StringUtils.isBlank(commentCreateDTO.getContent())) {
            throw new CustomizeException(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }

        Comment comment = new Comment();
        comment.setContent(commentCreateDTO.getContent());
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setType(commentCreateDTO.getType());
        comment.setCommentator(user.getId());
        comment.setCommentCount(0);
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setLikeCount(0L);
        System.out.println(comment);
        commentService.insert(comment);
        return ResultDTO.okOf();
    }

    //获取所属二级评论
    @ResponseBody
    @RequestMapping(value = "/comment/{id}",method = RequestMethod.GET)
    public ResultDTO<List<CommentDTO>> comments(@PathVariable("id") Integer id){
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.okOf(commentDTOS);
    }


    //添加点赞数
//    @ResponseBody
//    @RequestMapping(value = "/comment/likeCount",method = RequestMethod.POST)
//    public Object incLikeComment(@RequestBody CommentCreateDTO commentCreateDTO){
//        long id = commentCreateDTO.getParentId();
//        System.out.println(id);
//        int updateCount = commentService.incLikeCount(id);
//        if(updateCount == 0){
//            return ResultDTO.falseOf();
//        }
//        return ResultDTO.okOf();
//    }


    @ResponseBody
    @RequestMapping(value = "/comment/likeCount",method = RequestMethod.POST)
    public Object incLikeComment(@RequestParam(value = "commentId", defaultValue = "") long commentId){
        System.out.println(commentId);
        int updateCount = commentService.incLikeCount(commentId);
        if(updateCount == 0){
            return ResultDTO.falseOf();
        }
        return ResultDTO.okOf();
    }

}
