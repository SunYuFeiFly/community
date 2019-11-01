package life.majiang.community.controller;

import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.mapper.QuestionMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.Question;
import life.majiang.community.model.User;
import life.majiang.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PublicController {

    @Autowired
    private QuestionService questionService;

    //根据id查询question数据进行页面回显
    @RequestMapping("public/{id}")
    public String edit(@PathVariable(name = "id") Integer id,
                         Model model){
        QuestionDTO questionDTO = questionService.getById(id);
        model.addAttribute("title", questionDTO.getTitle());
        model.addAttribute("description", questionDTO.getDescription());
        model.addAttribute("tag", questionDTO.getTag());
        model.addAttribute("id", questionDTO.getId());
        return "publish";
    }

    @GetMapping("/publish")
    public String toPublish(){
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(@RequestParam(value = "title", required = false) String title,
                            @RequestParam(value = "description", required = false) String description,
                            @RequestParam(value = "tag", required = false) String tag,
                            @RequestParam(value = "id", required = false) Long id,
                            HttpServletRequest request,
                            Model model){

        //通过拦截器获取session中token，从而获取user存于session中

        User user = (User)request.getSession().getAttribute("user");
        //判断用户是否登录，如未登录需先登录方可发布问题
        if(user == null){
            model.addAttribute("error","您还未登录，请登录后再进行发布！");
            return "redirect:/";
        }

        //将提交过来数据防止在model中，用于后面回显
        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        if(title.trim() == "" || title == null){
            model.addAttribute("error","主题内容不能为空！");
            return "publish";
        }
        if(description.trim() == "" || description == null){
            model.addAttribute("error","主题描述不能为空！");
            return "publish";
        }
        if(tag.trim() == "" || tag == null){
            model.addAttribute("error","主题标签不能为空！");
            return "publish";
        }

        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setId(id);

        //此处采用查询后添加或更新操作
//        questionMapper.create(question);
        questionService.createOrUpdate(question);

        return "redirect:/";
    }
}
