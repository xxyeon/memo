package com.memo.common.oauth.google;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GoogleUser {
	String email;
	String id;
	String picture;
}
