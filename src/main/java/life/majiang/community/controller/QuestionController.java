package life.majiang.community.controller;

import life.majiang.community.dto.CommentDTO;
import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.enums.CommentTypeEnum;
import life.majiang.community.mapper.CommentMapper;
import life.majiang.community.mapper.QuestionMapper;
import life.majiang.community.model.User;
import life.majiang.community.service.CommentService;
import life.majiang.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    //根据question id查询question及一级评论
    @GetMapping("/question/{id}")
    public String question(@PathVariable("id") Integer id,
                            HttpServletRequest request,
                            Model model){
        // 1.根据id 获取封装了question与user的questionDTO对象
        QuestionDTO questionDTO = questionService.getById(id);

        // 2.获取现主题标签相关话题
        List<QuestionDTO> relatedQuestions = questionService.selectRelated(questionDTO);

        // 3. 根据id查询封装了Comment 与User 的CommentDTO对象
        List<CommentDTO> commentDTOs = commentService.listByTargetId(id,CommentTypeEnum.QUESTION);
        System.out.println(commentDTOs);

        //在获取数据成功，在跳转至question页面之前对阅读数进行 +1
        questionService.incView((long)id);

        model.addAttribute("question",questionDTO);
        model.addAttribute("comments",commentDTOs);
        model.addAttribute("relatedQuestions", relatedQuestions);
        return "question";
    }
}
