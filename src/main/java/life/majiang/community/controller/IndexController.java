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
                         @RequestParam(value="search",required = false) String search,
                         Model model){
        //获取通知数目（但此次利用拦截器获取保存至session，但是否查询太过频繁！）
//        Long unreadCount = notificationService.unreadCount(user.getId());
//        model.addAttribute("unreadCount",unreadCount);

        //获取主页问题合集
        PaginationDTO paginationDTO = questionService.list(search,page, size);
        model.addAttribute("pagination",paginationDTO);
        //用于翻页操作时参数
        model.addAttribute("search", search);
        return "index";
    }

}
