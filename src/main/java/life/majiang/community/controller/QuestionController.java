package life.majiang.community.controller;

import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.mapper.QuestionMapper;
import life.majiang.community.model.User;
import life.majiang.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable("id") Integer id,
                            HttpServletRequest request,
                            Model model){
        User user = (User)request.getSession().getAttribute("user");
        if(user == null){
            return "redirect:/";
        }
        //通过id或去封装了question与user的questionDTO对象
        QuestionDTO questionDTO = questionService.getById(id);
        model.addAttribute("question",questionDTO);
        return "question";
    }
}
