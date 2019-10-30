package life.majiang.community.community.controller;

import life.majiang.community.community.dto.PaginationDTO;
import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.User;
import life.majiang.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    @Autowired
    private QuestionService questionService;

    @RequestMapping("/")
    public String index(HttpServletRequest request,
                         @RequestParam(value="page",defaultValue = "1") Integer page,
                         @RequestParam(value="size",defaultValue = "5") Integer size,
                         Model model){
        //通过拦截器获取session中token，从而获取user存于session中

        User user = (User)request.getSession().getAttribute("user");
        //获取主页问题合集
//        List<QuestionDTO> QuestionDTOList = questionService.list(page,size);
        PaginationDTO paginationDTO = questionService.list(page, size);
        System.out.println(paginationDTO);
        model.addAttribute("pagination",paginationDTO);
        return "index";
    }

}
