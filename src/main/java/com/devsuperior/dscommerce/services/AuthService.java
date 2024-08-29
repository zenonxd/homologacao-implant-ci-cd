package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.services.exceptions.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    //testar para ver se o usuário que está logado é admin
    //se não for admin
    public void validateSelfOrAdmin(long userId) {
        //pegando usuário logado
        User me = userService.authenticate();

        //vendo se o me não é admin ou não é igual ao user passado acima (userId)
        //para isso, usaremos o hasRole do User

        //se ambas as negações derem true, significa que ele não é admin
        //e também não é o próprio usuário do argumento acima
        if (!me.hasRole("ROLE_ADMIN") && !me.getId().equals(userId)) {
            throw new ForbiddenException("Access denied");
        }
    }
}
