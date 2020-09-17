package com.sample.board.controller;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.board.config.authentication.PrincipalDetail;
import com.sample.board.config.authentication.PrincipalDetailService;
import com.sample.board.model.KakaoProfile;
import com.sample.board.model.OAuthToken;
import com.sample.board.model.ResponseDTO;
import com.sample.board.model.UserDTO;
import com.sample.board.service.UserService;

@Controller
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Value("${tiger.key}")
	private String tigerKey;
	
	@Autowired
	UserService userService;
	
	@Autowired
	PrincipalDetailService authService;
	
	@GetMapping("/admin/managed")
	public String managed(Model model) {
		model.addAttribute("message", "Admin Managed Form");
		return "user/managedForm";
	}
	
	@GetMapping("/auth/kakao/callback")
	public String kakaoCallback(String code) {
		logger.info("kakao authentication completed : "+code);
		
		RestTemplate rt = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		String grant_type = "authorization_code";
		String client_id = "062868188caa38399a29c76b43a4d391";
		String redirect_uri = "http://localhost/security/auth/kakao/callback";
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", grant_type);
		params.add("client_id", client_id);
		params.add("redirect_uri", redirect_uri);
		params.add("code", code);
		
		HttpEntity<MultiValueMap<String,String>> kakaoTokenRequest = 
			new HttpEntity<>(params, headers);
		
		ResponseEntity<String> response = rt.exchange(
				"https://kauth.kakao.com/oauth/token", 
				HttpMethod.POST, 
				kakaoTokenRequest, 
				String.class
		);
		
		logger.info("============= response body content : "+response.getBody());
		
//		Gson, Json, Simple, ObjectMapper
		ObjectMapper objectMapper = new ObjectMapper();
		OAuthToken oauthToken = null;
		try {
			oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("response body : "+response.getBody());
		logger.info("카카오 엑세스 토큰 : "+oauthToken.getAccess_token());
		
		kakaoLogin(oauthToken.getAccess_token());
		
		return "redirect:/";
	}
	
	public void kakaoLogin(String cacao_access_token) {
		RestTemplate rt = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer "+cacao_access_token);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = 
				new HttpEntity<>(headers);
		
		ResponseEntity<String> response = rt.exchange(
				"https://kapi.kakao.com/v2/user/me",
				HttpMethod.POST,
				kakaoProfileRequest,
				String.class
		);
		
		ObjectMapper objectMapper = new ObjectMapper();
		KakaoProfile kakaoProfile = null;
		try {
			kakaoProfile = objectMapper.readValue(response.getBody(), KakaoProfile.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("카카오 아이디(번호) : "+kakaoProfile.getId());
		logger.info("카카오 이메일 : "+kakaoProfile.getKakao_account().getEmail());
		logger.info("블로그 서버 유저네임 : "+kakaoProfile.getKakao_account().getEmail()+"_"+kakaoProfile.getId());
		logger.info("블로그 서버 이메일 : "+kakaoProfile.getKakao_account().getEmail());
		logger.info("블로그 서버 패스워드 : "+ tigerKey);
		
		UserDTO kakaoUser = UserDTO.builder()
				.username(kakaoProfile.getKakao_account().getEmail()+"_"+kakaoProfile.getId())
				.password(tigerKey)
				.email(kakaoProfile.getKakao_account().getEmail())
				.oauth("kakao")
				.role("ROLE_USER")
				.build();

//		가입자 혹은 비가입자 체크처리
		UserDTO dbUser = userService.login(kakaoUser.getUsername());
		
		if(dbUser == null) {
			logger.info("기존 회원이 아니기에 자동 회원가입을 진행합니다........................!");
			userService.insert(kakaoUser);
		}
		
		logger.info("자동 로그인을 진행합니다..........!");
//		로그인 및 세션 처리
		UserDetails principal = authService.loadUserByUsername(kakaoUser.getUsername());
		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	
	@ResponseBody
	@PutMapping("/user/update")
	public ResponseDTO<String> update(@RequestBody UserDTO user) {
		userService.update(user);
		UserDetails principal = authService.loadUserByUsername(user.getUsername());
		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return new ResponseDTO<String>(HttpStatus.OK.value(), "User update Success!");
	}
	
	@GetMapping("/user/userInfo")
	public String userInfo(Model model, Authentication auth) {
		PrincipalDetail principal = (PrincipalDetail)auth.getPrincipal();
		model.addAttribute("dto", userService.login(principal.getUsername()));
		return "user/detailForm";
	}
	
	@GetMapping("/auth/denied")
	public String denied(Model model) {
		model.addAttribute("message", "You has not a role!");
		return "user/deniedForm";
	}
	
//	@ResponseBody
//	@GetMapping("/auth/denied")
//	public ResponseDTO<String> denied() {
//		return new ResponseDTO<String>(HttpStatus.NOT_ACCEPTABLE.value(), "You has not a role!");
//	}
	
	@ResponseBody
	@GetMapping("/auth/loginSuccess")
	public ResponseDTO<String> loginSuccess(Authentication auth) {
		PrincipalDetail principal = (PrincipalDetail)auth.getPrincipal();
		return new ResponseDTO<String>(HttpStatus.OK.value(), principal.getUsername());
	}
	
	@ResponseBody
	@GetMapping("/auth/loginError")
	public ResponseDTO<String> loginError() {
		return new ResponseDTO<String>(HttpStatus.NOT_FOUND.value(), "Username or Password mismatch!");
	}
	
	@ResponseBody
	@PostMapping("/auth/insert")
	public ResponseDTO<String> insert(@RequestBody UserDTO user) {
		userService.insert(user);
		UserDetails principal = authService.loadUserByUsername(user.getUsername());
		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return new ResponseDTO<String>(HttpStatus.OK.value(), "user insert seccess!");
	}
	
	@ResponseBody
	@GetMapping("/logout")
	public ResponseDTO<String> logout(HttpSession session) {
		session.invalidate();
		return new ResponseDTO<String>(HttpStatus.OK.value(), "logout!");
	}
	
	@GetMapping("/auth/joinForm")
	public String joinForm() {
		return "user/joinForm";
	}
	
	@GetMapping("/auth/loginForm")
	public String loginForm() {
		return "user/loginForm";
	}
	
}
