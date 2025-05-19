package com.memo.login.oauth.google;

import com.memo.login.oauth.OAuthUser;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access= AccessLevel.PRIVATE)
public class GoogleUser implements OAuthUser {
	private String email;
	private String id;
	private String picture;

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public String getUsername() {
		return id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getProfileImg() {
		return picture;
	}

	public static GoogleUser of(String email, String id, String picture) {
		return new GoogleUser(email, id, picture);
	}

}
