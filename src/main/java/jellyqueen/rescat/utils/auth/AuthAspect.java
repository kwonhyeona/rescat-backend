package jellyqueen.rescat.utils.auth;

import jellyqueen.rescat.domain.User;
import jellyqueen.rescat.domain.enums.Role;
import jellyqueen.rescat.exception.UnAuthenticationException;
import jellyqueen.rescat.repository.UserRepository;
import jellyqueen.rescat.service.JWTService;
import jellyqueen.rescat.vo.JwtTokenVO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
public class AuthAspect {
    public final static String USER_KEY = "rescat-user";
    private final static String AUTHORIZATION = "Authorization";
    private final HttpServletRequest httpServletRequest;
    private final JWTService jwtService;
    private final UserRepository userRepository;

    public AuthAspect(final HttpServletRequest httpServletRequest, final JWTService jwtService, final UserRepository userRepository) {
        this.httpServletRequest = httpServletRequest;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    private User checkAuth(final String jwtToken) {
        if (jwtToken == null) throw new UnAuthenticationException("token", "유효하지 않은 토큰입니다.");

        final JwtTokenVO token = jwtService.decode(jwtToken);
        if (token == null || token.getIdx() == -1) throw new UnAuthenticationException("token", "유효하지 않은 토큰입니다.");

        final User user = userRepository.findByIdx(token.getIdx());
        if (user == null) throw new UnAuthenticationException("idx", "해당하는 유저가 존재하지 않습니다.");

        return user;
    }

    /**
     * 토큰 유효성 검사
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    //항상 @annotation 패키지 이름을 실제 사용할 annotation 경로로 맞춰줘야 한다.
    @Around("@annotation(jellyqueen.rescat.utils.auth.Auth)")
    public Object aroundMember(final ProceedingJoinPoint pjp) throws Throwable {
        final String jwt = httpServletRequest.getHeader(AUTHORIZATION);
        httpServletRequest.setAttribute(USER_KEY, checkAuth(jwt));
        return pjp.proceed(pjp.getArgs());
    }

    // 케어테이커 인증
    @Around("@annotation(jellyqueen.rescat.utils.auth.CareTakerAuth)")
    public Object aroundCareTaker(final ProceedingJoinPoint pjp) throws Throwable {
        final String jwt = httpServletRequest.getHeader(AUTHORIZATION);
        User user = checkAuth(jwt);
        if (user.getRole() != Role.CARETAKER)
            throw new UnAuthenticationException("user", "케어테이커 인증을 받지 않은 사용자입니다.");

        httpServletRequest.setAttribute(USER_KEY, user);
        return pjp.proceed(pjp.getArgs());
    }

    /**
     * 관리자 권한 인증
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("@annotation(jellyqueen.rescat.utils.auth.AdminAuth)")
    public Object aroundAdmin(final ProceedingJoinPoint pjp) throws Throwable {
        final String jwt = httpServletRequest.getHeader(AUTHORIZATION);
        User user = checkAuth(jwt);
        if (user.getRole() != Role.ADMIN)
            throw new UnAuthenticationException("user", "어드민 인증을 받지 않은 사용자입니다.");

        httpServletRequest.setAttribute(USER_KEY, user);
        return pjp.proceed(pjp.getArgs());
    }
}
