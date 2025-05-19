package com.memo.login.oauth;

import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuthService {
	OAuthUser service(OAuth2User oAuth2User);
}
