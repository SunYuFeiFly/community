package life.majiang.community.community.controller;

import life.majiang.community.community.dto.AccessTokenDTO;
import life.majiang.community.community.dto.GithubUser;
import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.User;
import life.majiang.community.community.provider.GithubProvider;
import life.majiang.community.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${git.client.id}")
    private String clientId;

    @Value("${git.client.secret}")
    private String clientSecret;

    @Value("${git.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    //登录时用户信息获取更新操作
    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletRequest request,
                           HttpServletResponse response){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);
        accessTokenDTO.setRedirect_uri(redirectUri);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);

        //获取到反馈用户信息
        GithubUser githubUser = githubProvider.getUser(accessToken);
        if(null != githubUser && githubUser.getId() != null){
            //现将githubUser信息复制到user中，利用user中accountId（accountId是唯一不会变的）到数据库查询，决定添加还是更新
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setAvatarUrl(githubUser.getAvatarUrl());
            userService.createOrUpdate(user);

            //将user登录信息存入cookie中,待此后每次登陆首页获取seeion值，利用辞职值数据库获取User对象，以此达到长时间保存用户登陆状态
            response.addCookie(new Cookie("token",token));

        }else{
            //用户为空
        }
        return "redirect:/";
    }

    //用户退出登录操作
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request,
                          HttpServletResponse response){
        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }
}
