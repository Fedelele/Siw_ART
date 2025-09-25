package it.uniroma3.siw.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class AuthConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Public permits: anyone can access
                        .requestMatchers(HttpMethod.GET, "/", "/index", "/home", "/login", "/register", "/success", "/css/**", "/js/**", "/images/**", "/favicon.ico", "/artist-photo/**", "/artwork-cover/**", "/museum-photo/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/artist/details/**", "/artwork/details/**", "/museum/details/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/artwork/all", "/museum/all", "/artist/all").permitAll()
                        .requestMatchers(HttpMethod.POST, "/register", "/login").permitAll()

                        //Accessible to admin only
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")

                        //Accessible to users only
                        .requestMatchers(HttpMethod.POST, "/user/**").authenticated()

                        //Accessible only to authenticated users
                        .requestMatchers("/profile/**").authenticated()
                        //Any other request requires authentication
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .permitAll()
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/login?error=true")
                )
                .logout(logout -> logout
                        // The url to send the POST request to for the logout
                        .logoutUrl("/logout")
                        // Url to redirect after logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .clearAuthentication(true)
                        .permitAll()
                );
        return http.build();
    }
}
