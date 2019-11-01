package life.majiang.community.controller;

import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.mapper.QuestionMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    private QuestionService questionService;

    @RequestMapping("/profile/{action}")
    public String profile(@PathVariable("action") String action,
                           @RequestParam(value="page",defaultValue = "1") Integer page,
                           @RequestParam(value="size",defaultValue = "5") Integer size,
                           HttpServletRequest request,
                           Model model){
        //通过拦截器获取session中token，从而获取user存于session中

        User user = (User)request.getSession().getAttribute("user");

        //如果session 用户查询为空，跳转至首页进行登录
        if(user == null){
            return "redirect:/";
        }

        //根据请求页url不同，保存不同内容消息方便多页面显示
        if("questions".equals(action)){
            model.addAttribute("section","questions");
            model.addAttribute("sectionName","我的提问");
        }else if ("replies".equals(action)){
            model.addAttribute("section","replies");
            model.addAttribute("sectionName","最新回复");
        }

        //查询专属的question合集
        PaginationDTO paginationDTO = questionService.listByUserId(user.getId(), page, size);
        System.out.println(paginationDTO);
        model.addAttribute("pagination",paginationDTO);
        return "profile";




    }




}
