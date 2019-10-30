package life.majiang.community.community.controller;

import life.majiang.community.community.mapper.QuestionMapper;
import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.Question;
import life.majiang.community.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PublicController {

    @Autowired
    private QuestionMapper questionMapper;

    @GetMapping("/publish")
    public String toPublish(){
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(@RequestParam(value = "title", required = false) String title,
                            @RequestParam(value = "description", required = false) String description,
                            @RequestParam(value = "tag", required = false) String tag,
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
        question.setGmtCreate(System.currentTimeMillis());
        question.setGmtModified(null);
        question.setCommentCount(0);
        question.setLikeCount(0);
        question.setViewCount(0);

        questionMapper.create(question);

        return "redirect:/";
    }
}
