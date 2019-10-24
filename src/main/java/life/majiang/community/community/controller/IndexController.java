package life.majiang.community.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.jws.WebParam;

@Controller
public class IndexController {

    @RequestMapping("/")
    public String index(@RequestParam(name = "name",required=false) String name, Model model){
        model.addAttribute("name",name);
        return "index";
    }

}
