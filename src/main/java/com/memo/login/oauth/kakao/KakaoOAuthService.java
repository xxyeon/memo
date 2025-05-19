package com.memo.login.oauth.kakao;

import java.util.Map;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.memo.login.oauth.OAuthService;
import com.memo.login.oauth.OAuthUser;

@Service
public class KakaoOAuthService implements OAuthService {

	@Override
	public OAuthUser service(OAuth2User oAuth2User) {
		Map<String, Object> attributes = oAuth2User.getAttributes();
		//프로필이 없을 수 있다.
		String profileImage = null;
		if (attributes.containsKey("profile_image")) {
			profileImage = (String)attributes.get("profile_image");
		}
		Long id = (Long) attributes.get("id");
		Map<String, Object> properties = (Map)attributes.get("properties");
		String nickname = getNickname(properties);
		String email = "";

		return KakaoUser.of(email, nickname, String.valueOf(id), profileImage);
	}

	private String getNickname(Map<String, Object> properties) {
		return (String)properties.get("nickname");
	}
}
