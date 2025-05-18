package com.memo.common.oauth.google;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GoogleOAuthResponse {
	@JsonProperty("access_token")
	String accessToken;
	@JsonProperty("expires_in")
	String expiresIn;
	String scope;
	@JsonProperty("token_type")
	String tokenType;
	@JsonProperty("id_token")
	String idToken;
}
