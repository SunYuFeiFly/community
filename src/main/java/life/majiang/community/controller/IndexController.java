package life.majiang.community.controller;

import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.service.NotificationService;
import life.majiang.community.service.QuestionService;
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

    @Autowired
    private NotificationService notificationService;

    @RequestMapping("/")
    public String index(HttpServletRequest request,
                         @RequestParam(value="page",defaultValue = "1") Integer page,
                         @RequestParam(value="size",defaultValue = "5") Integer size,
                         Model model){
        //通过拦截器获取session中token，从而获取user存于session中

        User user = (User)request.getSession().getAttribute("user");

        //如果session 用户查询为空，跳转至首页进行登录
        if(user == null){
            return "redirect:/";
        }

        //获取通知数目（但此次利用拦截器获取保存至session，但是否查询太过频繁！）
//        Long unreadCount = notificationService.unreadCount(user.getId());
//        model.addAttribute("unreadCount",unreadCount);

        //获取主页问题合集
//        List<QuestionDTO> QuestionDTOList = questionService.list(page,size);
        PaginationDTO paginationDTO = questionService.list(page, size);
        System.out.println(paginationDTO);
        model.addAttribute("pagination",paginationDTO);
        return "index";
    }

}
