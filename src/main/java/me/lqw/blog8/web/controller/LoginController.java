package me.lqw.blog8.web.controller;

import me.lqw.blog8.BlogConstants;
import me.lqw.blog8.BlogContext;
import me.lqw.blog8.mapper.UserMapper;
import me.lqw.blog8.model.User;
import me.lqw.blog8.model.dto.CR;
import me.lqw.blog8.model.dto.ResultDTO;
import me.lqw.blog8.model.vo.LoginParam;
import me.lqw.blog8.service.RememberMeService;
import me.lqw.blog8.service.UserService;
import me.lqw.blog8.web.controller.console.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * 登录控制器
 * @author liqiwen
 * @version 1.0
 * @since 1.0
 */
@Controller
public class LoginController extends BaseController {

    /**
     * 用户 UserService
     */
    private final UserService userService;

    /**
     * 记住我 service
     */
    private final RememberMeService rememberMeService;

    public LoginController(UserService userService, RememberMeService rememberMeService) {
        this.userService = userService;
        this.rememberMeService = rememberMeService;
    }

    /**
     * 访问登录页面
     * @return login.html
     */
    @GetMapping("login")
    public String login(){
        return "login";
    }


    /**
     * 登录请求
     * @param loginParam loginParam
     * @param request request
     * @return CR<?>
     */
    @PostMapping(value = "login")
    @ResponseBody
    public CR<?> userAuth(@Valid @RequestBody LoginParam loginParam, HttpServletRequest request, HttpServletResponse response){

        HttpSession session = request.getSession();

        User user = userService.userAuth(loginParam.getUsername(), loginParam.getPassword());

        session.setAttribute(BlogConstants.AUTH_USER, user);

        Boolean rememberMe = loginParam.getRememberMe();
        if(rememberMe){
            rememberMeService.remember(request, response, user);
        }

        return ResultDTO.create();
    }


    @GetMapping("authorized")
    @ResponseBody
    public ResponseEntity<Boolean> authorized(){
        return ResponseEntity.ok(BlogContext.isAuthorized());
    }

}
