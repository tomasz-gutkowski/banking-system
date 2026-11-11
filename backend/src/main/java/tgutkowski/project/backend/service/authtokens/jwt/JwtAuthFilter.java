package tgutkowski.project.backend.service.authtokens.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tgutkowski.project.backend.model.entities.AppUser;
import tgutkowski.project.backend.repository.AppUserRepository;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final AppUserRepository appUserRepository;

    public JwtAuthFilter(JwtUtil jwtUtil, AppUserRepository appUserRepository) {
        this.jwtUtil = jwtUtil;
        this.appUserRepository = appUserRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.replace("Bearer ", "");
        Claims claims;
        try {
            claims = jwtUtil.extractClaims(token);
        }catch (JwtException e) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = claims.getSubject();

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<AppUser> optUser = appUserRepository.findByUsername(username);


            if (optUser.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }
            AppUser user = optUser.get();


            if(!user.isEnabled() || !user.isAccountNonLocked() || !user.isAccountNonExpired()) {
                filterChain.doFilter(request, response);
                return;
            }

            if(!jwtUtil.isTokenExpired(claims)){
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
