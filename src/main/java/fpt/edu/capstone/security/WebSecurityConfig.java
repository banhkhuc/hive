package fpt.edu.capstone.security;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final MyAuthenticationEntryPoint myAuthenticationEntryPoint;

    private final UserDetailsService userDetailsService;

    private final RequestFilter requestFilter;

    private final PasswordEncoder passwordEncoder;

    public WebSecurityConfig(MyAuthenticationEntryPoint myAuthenticationEntryPoint, UserDetailsService userDetailsService,
                             RequestFilter requestFilter, PasswordEncoder passwordEncoder) {
        this.myAuthenticationEntryPoint = myAuthenticationEntryPoint;
        this.userDetailsService = userDetailsService;
        this.requestFilter = requestFilter;
        this.passwordEncoder = passwordEncoder;
    }

    private static final String[] HTTP_SECURITY_AUTH_LIST = {
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/login",
            "/api/v1/**",
            "/register",
            "/actuator*"
    };

    private static final String[] WEB_SECURITY_AUTH_LIST = {
//            "/v2/api-docs",
//            "/configuration/ui",
//            "/swagger-resources/**",
//            "/configuration/security",
//            "/swagger-ui/**",
//            "/webjars/**",
//            "/swagger/**"
    };
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }



    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .authorizeRequests().antMatchers(HTTP_SECURITY_AUTH_LIST).permitAll()
                .anyRequest().authenticated()
                .and().exceptionHandling().authenticationEntryPoint(myAuthenticationEntryPoint)
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/static/css/**, /static/js/**, *.ico");
        web.ignoring().antMatchers(WEB_SECURITY_AUTH_LIST);
    }
}