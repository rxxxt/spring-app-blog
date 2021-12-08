package org.example.myblog.service.impl;

import org.example.myblog.domain.Role;
import org.example.myblog.domain.User;
import org.example.myblog.repository.UserRepository;
import org.example.myblog.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    private final MailSender mailSender;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, MailSender mailSender, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Use not found"));
    }

    @Override
    public boolean addUser(User user) {
        Optional<User> userFromDb = userRepository.findByUsername(user.getUsername());

        if (userFromDb.isPresent())
            return false;

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        sendMessage(user);

        return true;
    }

    private void sendMessage(User user) {
        if (!user.getEmail().isEmpty()) {
            String message = String.format(
                    "Hello, %s!\n" +
                            "Welcome to myBlog. Please, visit next link: http://localhost:8080/activate/%s",
                    user.getUsername(),
                    user.getActivationCode()
            );
            mailSender.send(user.getEmail(), "Activation code", message);
        }
    }

    @Override
    public boolean activateUser(String code) {
        User user = userRepository.findByActivationCode(code);

        if (user == null)
            return false;

        user.setActivationCode(null);

        userRepository.save(user);

        return true;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void saveUser(User user, String username, Map<String, String> form) {
        user.setUsername(username);

        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());

        user.getRoles().clear();

        form.keySet().forEach(key -> {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        });

        userRepository.save(user);
    }

    public void updateProfile(User user, String newPassword, String newEmail) {

        if (isValid(newEmail, newPassword)) {
            user.setPassword(passwordEncoder.encode(newPassword));

            if (!newEmail.equals(user.getEmail())) {
                user.setEmail(newEmail);
                user.setActivationCode(UUID.randomUUID().toString());
                sendMessage(user);
            }

            userRepository.save(user);
        }
    }

    public void subscribe(User currentUser, User user) {
        user.getSubscribers().add(currentUser);

        userRepository.save(user);
    }

    public void unsubscribe(User currentUser, User user) {
        user.getSubscribers().remove(currentUser);

        userRepository.save(user);
    }

    private boolean isValid(String newEmail, String newPassword) {
        return newPassword != null && !newPassword.isEmpty() && newEmail != null && !newEmail.isEmpty();
    }
}
