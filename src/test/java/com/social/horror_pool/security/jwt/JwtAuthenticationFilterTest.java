package com.social.horror_pool.security.jwt;

import com.social.horror_pool.model.User;
import com.social.horror_pool.security.CustomUserDetails;
import com.social.horror_pool.security.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private static final String TOKEN = "valid.jwt.token";
    private static final String USERNAME = "testuser";

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        this.jwtAuthenticationFilter = new JwtAuthenticationFilter(this.jwtTokenProvider, this.userDetailsServiceImpl);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_WithValidJwtAndActiveUser_AuthenticatesUser() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        CustomUserDetails userDetails = createUserDetails(true, false);
        mockValidTokenFor(userDetails);

        this.jwtAuthenticationFilter.doFilter(request, response, this.filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertSame(userDetails, authentication.getPrincipal());
        assertEquals(USERNAME, authentication.getName());
        verify(this.filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_WithValidJwtAndDisabledUser_DoesNotAuthenticateUser() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        CustomUserDetails userDetails = createUserDetails(false, false);
        mockValidTokenFor(userDetails);

        this.jwtAuthenticationFilter.doFilter(request, response, this.filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(this.filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_WithValidJwtAndLockedUser_DoesNotAuthenticateUser() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        CustomUserDetails userDetails = createUserDetails(true, true);
        mockValidTokenFor(userDetails);

        this.jwtAuthenticationFilter.doFilter(request, response, this.filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(this.filterChain).doFilter(request, response);
    }

    private void mockValidTokenFor(CustomUserDetails userDetails) {
        when(this.jwtTokenProvider.getJwtFromCookie(any())).thenReturn(TOKEN);
        when(this.jwtTokenProvider.validateToken(TOKEN)).thenReturn(true);
        when(this.jwtTokenProvider.getUsernameFromToken(TOKEN)).thenReturn(USERNAME);
        when(this.userDetailsServiceImpl.loadUserByUsername(USERNAME)).thenReturn(userDetails);
    }

    private CustomUserDetails createUserDetails(boolean enabled, boolean locked) {
        User user = new User(USERNAME, "testuser@example.com", "password");
        user.setEnabled(enabled);
        user.setLocked(locked);
        return new CustomUserDetails(user);
    }
}
