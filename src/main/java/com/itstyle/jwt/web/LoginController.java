package com.itstyle.jwt.web;

import com.itstyle.jwt.common.entity.CheckResult;
import com.itstyle.jwt.common.util.GetIPAddress;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import com.itstyle.jwt.common.constant.SystemConstant;
import com.itstyle.jwt.common.entity.R;
import com.itstyle.jwt.common.util.JwtUtils;
import com.itstyle.jwt.model.User;
import com.itstyle.jwt.repository.UserRepository;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import static com.itstyle.jwt.common.util.JwtUtils.validateJWT;


@Api(tags ="用户管理")
@Controller
public class LoginController {
	@Autowired
	UserRepository userRepository;

	@ApiOperation(value="登陆状态检查")
	@CrossOrigin
	@RequestMapping(value="/check",method = RequestMethod.GET)
	public ModelAndView checkin(String token, HttpServletRequest request, ModelMap model){
        String addr = GetIPAddress.getIpAddress(request);
        token = request.getParameter("token");
        if (token.equals("")) {
            System.out.println("here");
            return new ModelAndView(new RedirectView(addr+"/login"));
        }else{
            CheckResult checkResult = null;
                checkResult = JwtUtils.validateJWT(token);
                if (checkResult.isSuccess()) {
                    System.out.println("Success");
                    model.put("msg", "Authorized");
                    return new ModelAndView(new RedirectView(addr+"/wordladder"), model);
                }
            switch (checkResult.getErrCode()) {
                // 签名验证不通过
                case SystemConstant.JWT_ERRCODE_FAIL:
                    return new ModelAndView(new RedirectView(addr+"/login"));
                // 签名过期，返回过期提示码
                case SystemConstant.JWT_ERRCODE_EXPIRE:
                    return new ModelAndView(new RedirectView(addr+"/login"));
                default:
                    break;
            }
            return new ModelAndView("redirect:"+ addr+"/login");
            //验证JWT的签名，返回CheckResult对象
        }
	}


	@ApiOperation(value="用户登陆")
	@CrossOrigin
	@RequestMapping(value="/login",method = RequestMethod.POST)
    public ModelAndView login(String username,String password, RedirectAttributes attr,
                              HttpServletRequest request, HttpServletResponse responese) {
		User user =  userRepository.findByUsername(username);
        String addr = GetIPAddress.getIpAddress(request);
        String URI = request.getRequestURI();
        addr += URI;

        if(user!=null){
			if(user.getPassword().equals(password)){
				//把token返回给客户端-->客户端保存至cookie-->客户端每次请求附带cookie参数
				String JWT = JwtUtils.createJWT("1", username, SystemConstant.JWT_TTL);
                attr.addAttribute("token", JWT);
                System.out.println(JWT);
                return new ModelAndView("redirect:"+ addr);
			}else{
                attr.addAttribute("msg", "密码与用户名不匹配");
                return new ModelAndView("redirect:"+ addr);
			}
		}else{
            attr.addAttribute("msg", "密码与用户名不匹配");
            return new ModelAndView("redirect:"+ addr);
		}
    }
	@ApiOperation(value="获取用户信息")
	@CrossOrigin
	@RequestMapping(value="description",method = RequestMethod.POST)
    public R  description(String username) {
		User user =  userRepository.findByUsername(username);
		return R.ok(user.getDescription());
    }
}
