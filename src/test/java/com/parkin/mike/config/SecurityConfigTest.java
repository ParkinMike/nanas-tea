package com.parkin.mike.config;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SecurityConfigTest {

    @Test
    void passwordEncoderEncodesAndMatches() {
        SecurityConfig config = new SecurityConfig();

        var encoder = config.passwordEncoder();

        assertThat(encoder.matches("averysecurepass", encoder.encode("averysecurepass"))).isTrue();
    }

    @Test
    void userDetailsServiceBuildsUserForValidPasscode() {
        SecurityConfig config = new SecurityConfig();
        ReflectionTestUtils.setField(config, "passcode", "averysecurepass");

        UserDetailsService service = config.userDetailsService(config.passwordEncoder());
        var user = service.loadUserByUsername("nanas");

        assertThat(user.getUsername()).isEqualTo("nanas");
        assertThat(config.passwordEncoder().matches("averysecurepass", user.getPassword())).isTrue();
        assertThat(user.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
    }

    @Test
    void userDetailsServiceRejectsMissingPasscode() {
        SecurityConfig config = new SecurityConfig();
        ReflectionTestUtils.setField(config, "passcode", null);

        assertThatThrownBy(() -> config.userDetailsService(config.passwordEncoder()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("APP_PASSCODE must be set");
    }

    @Test
    void userDetailsServiceRejectsBlankPasscode() {
        SecurityConfig config = new SecurityConfig();
        ReflectionTestUtils.setField(config, "passcode", "   ");

        assertThatThrownBy(() -> config.userDetailsService(config.passwordEncoder()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("APP_PASSCODE must be set");
    }

    @Test
    void userDetailsServiceRejectsOldDefaultPasscode() {
        SecurityConfig config = new SecurityConfig();
        ReflectionTestUtils.setField(config, "passcode", "nanas123");

        assertThatThrownBy(() -> config.userDetailsService(config.passwordEncoder()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("APP_PASSCODE must not use the old default value");
    }

    @Test
    void userDetailsServiceRejectsShortPasscode() {
        SecurityConfig config = new SecurityConfig();
        ReflectionTestUtils.setField(config, "passcode", "short-pass");

        assertThatThrownBy(() -> config.userDetailsService(config.passwordEncoder()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("APP_PASSCODE must be at least 12 characters long");
    }

    @Test
    void csrfCookieFilterReadsTokenAndContinuesChain() throws Exception {
        Object filter = instantiateInnerClass("com.parkin.mike.config.SecurityConfig$CsrfCookieFilter");
        Method method = filter.getClass().getDeclaredMethod(
                "doFilterInternal",
                jakarta.servlet.http.HttpServletRequest.class,
                jakarta.servlet.http.HttpServletResponse.class,
                FilterChain.class
        );
        method.setAccessible(true);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(CsrfToken.class.getName(), new DefaultCsrfToken("X-XSRF-TOKEN", "_csrf", "abc123"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        method.invoke(filter, request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void spaHandlerUsesPlainResolutionWhenHeaderExists() throws Exception {
        Object handler = instantiateInnerClass("com.parkin.mike.config.SecurityConfig$SpaCsrfTokenRequestHandler");
        Method resolveMethod = handler.getClass().getDeclaredMethod(
                "resolveCsrfTokenValue",
                jakarta.servlet.http.HttpServletRequest.class,
                CsrfToken.class
        );
        resolveMethod.setAccessible(true);

        MockHttpServletRequest request = new MockHttpServletRequest();
        CsrfToken token = new DefaultCsrfToken("X-XSRF-TOKEN", "_csrf", "raw-token");
        request.addHeader(token.getHeaderName(), "raw-token");

        Object resolved = resolveMethod.invoke(handler, request, token);

        assertThat(resolved).isEqualTo("raw-token");
    }

    @Test
    void spaHandlerFallsBackToXorResolutionWithoutHeader() throws Exception {
        Object handler = instantiateInnerClass("com.parkin.mike.config.SecurityConfig$SpaCsrfTokenRequestHandler");
        Method resolveMethod = handler.getClass().getDeclaredMethod(
                "resolveCsrfTokenValue",
                jakarta.servlet.http.HttpServletRequest.class,
                CsrfToken.class
        );
        resolveMethod.setAccessible(true);

        MockHttpServletRequest request = new MockHttpServletRequest();
        CsrfToken token = new DefaultCsrfToken("X-XSRF-TOKEN", "_csrf", "raw-token");

        Object resolved = resolveMethod.invoke(handler, request, token);

        assertThat(resolved).isNull();
    }

    @Test
    void spaHandlerHandleDelegatesWithoutError() throws Exception {
        Object handler = instantiateInnerClass("com.parkin.mike.config.SecurityConfig$SpaCsrfTokenRequestHandler");
        Method handleMethod = handler.getClass().getDeclaredMethod(
                "handle",
                jakarta.servlet.http.HttpServletRequest.class,
                jakarta.servlet.http.HttpServletResponse.class,
                Supplier.class
        );
        handleMethod.setAccessible(true);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Supplier<CsrfToken> supplier = () -> new DefaultCsrfToken("X-XSRF-TOKEN", "_csrf", "raw-token");

        handleMethod.invoke(handler, request, response, supplier);

        assertThat(request.getAttribute("_csrf")).isNotNull();
    }

    private Object instantiateInnerClass(String className) throws Exception {
        Class<?> type = Class.forName(className);
        Constructor<?> constructor = type.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }
}
