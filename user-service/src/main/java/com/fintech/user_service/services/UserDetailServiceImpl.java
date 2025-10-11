package com.fintech.user_service.services;



import com.fintech.user_service.entities.UserEntity;
import com.fintech.user_service.repositories.AuthRepo;
import com.fintech.user_service.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final AuthRepo authRepo;
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserEntity> dbuser= userRepository.findByEmail(email);

        if(dbuser.isPresent()){
            UserEntity user=dbuser.get();
            return User
                    .builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .roles(String.valueOf(user.getRole()))
                    .build();

        }

        throw new UsernameNotFoundException("user not found with email : "+email);
    }
}
