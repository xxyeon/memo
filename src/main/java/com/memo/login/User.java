package com.memo.login;

import java.time.LocalDateTime;

import com.memo.login.oauth.OAuthUser;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id; //
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Entity
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;

	private String username;
	private String email;
	private String password;
	private String imgUrl;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Boolean isDeleted;
	private Boolean isBlacklist; //primitive type 대신 객체를 사용하는 이유
	private Boolean status;
	private Integer warning_count;
	private String providerId;

	@PrePersist
	public void perPersist() {
		if(isBlacklist == null) {
			this.isBlacklist = false;
		}
		if(status == null) {
			this.status = true;
		}
		if (warning_count == null) {
			this.warning_count = 0;
		}
		if (isDeleted == null) {
			this.isDeleted = false;
		}
		if (createdAt == null) {
			this.createdAt = LocalDateTime.now();
		}
		if (updatedAt == null) {
			this.updatedAt = LocalDateTime.now();
		}
	}
	private User(String email, String username, String password, String providerId, String imgUrl) {
		this.email = email;
		this.username = username;
		this.password = password;
		this.providerId = providerId;
		this.imgUrl = imgUrl;
	}
	public static User from(OAuthUser oAuthUser) {
		return new User(oAuthUser.getEmail(), oAuthUser.getUsername(), null, oAuthUser.getId(), oAuthUser.getProfileImg());
	}

}
