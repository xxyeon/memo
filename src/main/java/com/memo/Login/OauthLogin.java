package com.memo.Login;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.memo.common.oauth.google.CustomOAuthService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OauthLogin {

	private final CustomOAuthService customOAuthService;

	// /login/oauth2로 요청을 하면 내부에서 구글 서버로 승인 코드를 받기위해 요청을 하고 우리가 설정한 리다이렉션으로 승인 코드 받은 후 엑세스토큰 발급 후 사용자 정보 가져오는 것 까지
	@GetMapping("/login/oauth2")
	public void googleLogin(HttpServletResponse response) throws IOException {
		String requestURL = customOAuthService.request();
		response.sendRedirect(requestURL);
	}

	@GetMapping("/login/oauth2/code/google")
	public ResponseEntity<String> callback(@RequestParam(name = "code") String code) throws JsonProcessingException {
		customOAuthService.oAuthLogin(code);
		return new ResponseEntity<>("ok", HttpStatus.OK);
	}

}
