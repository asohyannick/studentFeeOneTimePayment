package com.leedTech.studentFeeOneTimePayment.config.firebaseConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

private final FirebaseAuth firebaseAuth;

@Override
protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
) throws ServletException, IOException {
	
	String authHeader = request.getHeader("Authorization");
	
	if (authHeader == null || !authHeader.startsWith("Firebase ")) {
		filterChain.doFilter(request, response);
		return;
	}
	
	String idToken = authHeader.substring(9);
	
	try {
		FirebaseToken firebaseToken = firebaseAuth.verifyIdToken(idToken);
		
		String email = firebaseToken.getEmail();
		String role  = (String) firebaseToken.getClaims().getOrDefault("role", "STUDENT");
		
		UsernamePasswordAuthenticationToken authentication =
				new UsernamePasswordAuthenticationToken(
						email,
						null,
						List.of(new SimpleGrantedAuthority("ROLE_" + role))
				);
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		log.info("Firebase token verified for user: {}", email);
		
	} catch (Exception e) {
		log.error("Firebase token verification failed: {}", e.getMessage());
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Firebase token");
		return;
	}
	
	filterChain.doFilter(request, response);
}
}