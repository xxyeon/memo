package com.memo.common.oauth.google;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomOAuthService {
	private static final String authUrl = "https://accounts.google.com/o/oauth2/v2/auth";
	private static final String tokenUrl = "https://oauth2.googleapis.com/token";
	private static final String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
	private static final String redirectUrl = "http://localhost:8080/login/oauth2/code/google";
	private ObjectMapper objectMapper;
	private RestTemplate restTemplate;
	private String clientId;
	private String clientSecret;

	public CustomOAuthService(@Value("${spring.security.oauth2.client.registration.google.client-id}") String clientId,
		@Value("${spring.security.oauth2.client.registration.google.client-secret}") String clientSecret, ObjectMapper objectMapper, RestTemplate restTemplate ) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.objectMapper = objectMapper;
		this.restTemplate = restTemplate;
	}
	// AuthorizationCodeFlow authorizationCodeFlow;
	// @Autowired
	// private BCryptPasswordEncoder bCryptPasswordEncoder;

	// @Override
	// public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
	// 	//user Info request 에 대한 전처리 후처리하는 메서드
	// 	OAuth2User oauth2User = super.loadUser(userRequest);
	// 	GoogleUserInfo googleUserInfo = null; //회원 정보
	//
	//
	// 	//인증 코드를 기지고 token 가져오기
	//
	// 	OAuth2AccessToken accessToken = userRequest.getAccessToken();
	// 	System.out.println("accessToken: " + accessToken.getTokenType().getValue());
	// 	if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
	// 		googleUserInfo = new GoogleUserInfo(oauth2User.getAttributes());
	// 	}
	// 	//리포지토리에 없다면 회원가입
	// 	User user;
	// 	user = new User(googleUserInfo.getProvider(), googleUserInfo.getEmail(),"11111");
	// 	System.out.println(user.toString());
	// 	Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
	//
	// 	// TODO
	// 	// 1) Fetch the authority information from the protected resource using accessToken
	//
	// 	// 2) Map the authority information to one or more GrantedAuthority's and add it to mappedAuthorities
	//
	// 	// 3) Create a copy of oidcUser but use the mappedAuthorities instead
	// 	// ClientRegistration.ProviderDetails providerDetails = userRequest.getClientRegistration().getProviderDetails();
	// 	// String userNameAttributeName = providerDetails.getUserInfoEndpoint().getUserNameAttributeName();
	// 	// if (StringUtils.hasText(userNameAttributeName)) {
	// 	// 	oidcUser = new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo(), userNameAttributeName);
	// 	// } else {
	// 	// 	oidcUser = new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
	// 	// }
	// 	// System.out.println(oidcUser.toString());
	//
	// 	// return oidcUser;
	// 	System.out.println(userRequest);
	// 	return new PrincipalDetails(user, oauth2User.getAttributes());
	// }


	public String request() {
		return authServerRequest(); //google 인증 서버에 인증 코드 요청
	}
	public String authServerRequest() {
		//google api와 통신하기 위한 요청 만들기(승인 코드를 받기 위한 요청) //
		// authorizationCodeFlow.loadCredential(String);
		Map<String, String> params = new HashMap<>();
		params.put("client_id", clientId);
		params.put("redirect_uri", redirectUrl);
		params.put("response_type", "code");
		params.put("scope", "email");

		// params.put("state", createCSRFToken())
		String parameterString=params.entrySet().stream()
			.map(x->x.getKey()+"="+x.getValue())
			.collect(Collectors.joining("&"));
		String redirectURL=authUrl+"?"+parameterString;
		log.info("redirect-URL={}", redirectURL);
		return redirectURL;
	}

	public void oAuthLogin(String code) throws JsonProcessingException {
		ResponseEntity<String> response = requestAccessToken(code);
		GoogleOAuthResponse oAuthResponse = getAccessToken(response);
		log.info("accessToken: {}", oAuthResponse.getAccessToken());
		// GoogleOAuthResponse googleOAuthToken =socialOauth.getAccessToken(accessToken);
		//
		ResponseEntity<String> userInfoResponse=requestUserInfo(oAuthResponse.getAccessToken());
		//
		GoogleUser googleUser = getUserInfo(userInfoResponse);
		//
		String user_id = googleUser.getEmail();
		//
		log.info("login user email: {}", user_id);
		// return new GetSocialOAuthRes("1234",1,"asdf", googleOAuthToken.getToken_type());

	}
	private GoogleOAuthResponse getAccessToken(ResponseEntity<String> response) throws JsonProcessingException {
		log.info("accessTokenBody: {}",response.getBody());
		return objectMapper.readValue(response.getBody(), GoogleOAuthResponse.class);
	}

	public ResponseEntity<String> requestAccessToken(String code) {
		RestTemplate restTemplate=new RestTemplate();
		Map<String, Object> params = new HashMap<>();
		params.put("code", code);
		params.put("client_id", clientId);
		params.put("client_secret", clientSecret);
		params.put("redirect_uri", redirectUrl);
		params.put("grant_type", "authorization_code");
		ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(tokenUrl, params, String.class);
		System.out.println(stringResponseEntity.toString());
		return stringResponseEntity;
	}

	private ResponseEntity<String> requestUserInfo(String token) {
		HttpHeaders headers = new HttpHeaders();

		HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(headers);
		headers.add("Authorization","Bearer "+ token);
		ResponseEntity<String> exchange = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, String.class);
		System.out.println(exchange.getBody());
		return exchange;
	}

	private GoogleUser getUserInfo(ResponseEntity<String> response) throws JsonProcessingException {
		log.info("Response Body: {}",response.getBody());
		return objectMapper.readValue(response.getBody(), GoogleUser.class);
	}

	// public String parseToekn(ResponseEntity<String> responseBody) {
	//
	// }


	// private String createCSRFToken() {
	// 	// Create a state token to prevent request forgery.
	// 	// Store it in the session for later validation.
	// 	String state = new BigInteger(130, new SecureRandom()).toString(32);
	// 	request.session().attribute("state", state);
	// 	// Read index.html into memory, and set the client ID,
	// 	// token state, and application name in the HTML before serving it.
	// 	return new Scanner(new File("index.html"), "UTF-8")
	// 		.useDelimiter("\\A").next()
	// 		.replaceAll("[{]{2}\\s*CLIENT_ID\\s*[}]{2}", CLIENT_ID)
	// 		.replaceAll("[{]{2}\\s*STATE\\s*[}]{2}", state)
	// 		.replaceAll("[{]{2}\\s*APPLICATION_NAME\\s*[}]{2}",
	// 			APPLICATION_NAME);
	// }
}
