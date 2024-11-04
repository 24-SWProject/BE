package com.swproject.hereforus.Controller;

import com.swproject.hereforus.Service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Controller
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/user/login")
    public void getCodeStatus(HttpServletResponse request, HttpServletResponse response) throws IOException {
        String loginUrl = userService.fetchNaverUrl();
        response.sendRedirect(loginUrl);
    }

    @ResponseBody
    @GetMapping("/api/naver/auth")
    public Object getToken(@RequestParam(value="code") String code, @RequestParam(value="state") String state) {
        return userService.CodeToToken(code, state);
    }
}
