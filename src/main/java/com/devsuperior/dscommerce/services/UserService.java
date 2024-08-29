package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.UserDto;
import com.devsuperior.dscommerce.entities.Role;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.projections.UserDetailsProjection;
import com.devsuperior.dscommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> result = userRepository.searchUserAndRolesByEmail(username);
        if (result.size() == 0) {
            throw new UsernameNotFoundException("User not found");
        }

        User user = new User();
        user.setEmail(username);
        user.setPassword(result.get(0).getPassword());

        for (UserDetailsProjection p : result) {
            user.addRole(new Role(p.getRoleId(), p.getAuthority()));
        }
        return user;
    }

    protected User authenticate() {
        try {
            //Pega um objeto do Aipo authentication dentro do Spring Security.
            //Ou seja, se tem um cara autenticado ele pega.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            //pegando o user autenticado (dentro de authentication) faremos o casting para JWT,
            //e usaremos o getPrincipal.
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();

            //O getPrincipal por sua vez, possui os claims (onde foi configurado dentro do pacote config).
            // A partir disso, podemos recuperar o seu username (email)
            String username = jwtPrincipal.getClaim("username");
            return userRepository.findByEmail(username).get();
        }
        catch (Exception e) {
            throw new UsernameNotFoundException("User not found");
        }
    }

    @Transactional(readOnly = true)
    public UserDto getMe() {
        //pegando o usuário autenticado (logado) criado acima
        User user = authenticate();
        return new UserDto(user);
    }
}
