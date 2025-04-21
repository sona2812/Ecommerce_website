package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UserService {
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 30;
    
    private final ConcurrentHashMap<String, AtomicInteger> loginAttempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LocalDateTime> accountLockTime = new ConcurrentHashMap<>();

    @Autowired
    private UserRepository userRepository;

    public User registerUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setRole(UserRole.CUSTOMER);
        user.setEnabled(true);

        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Transactional
    public User login(UserDTO userDTO) {
        String email = userDTO.getEmail();
        
        // Check if account is locked
        if (isAccountLocked(email)) {
            LocalDateTime lockTime = accountLockTime.get(email);
            long minutesLeft = LOCK_DURATION_MINUTES - java.time.Duration.between(lockTime, LocalDateTime.now()).toMinutes();
            throw new RuntimeException("Account is locked. Please try again in " + minutesLeft + " minutes");
        }

        User user = getUserByEmail(email);

        if (!user.isEnabled()) {
            throw new RuntimeException("Account is disabled");
        }

        // Validate password
        if (!userDTO.getPassword().equals(user.getPassword())) {
            // Increment login attempts
            AtomicInteger attempts = loginAttempts.computeIfAbsent(email, k -> new AtomicInteger(0));
            int currentAttempts = attempts.incrementAndGet();
            
            if (currentAttempts >= MAX_LOGIN_ATTEMPTS) {
                // Lock account
                accountLockTime.put(email, LocalDateTime.now());
                throw new RuntimeException("Account locked due to too many failed attempts. Please try again in " + LOCK_DURATION_MINUTES + " minutes");
            }
            
            throw new RuntimeException("Invalid email or password. " + (MAX_LOGIN_ATTEMPTS - currentAttempts) + " attempts remaining");
        }

        // Reset login attempts on successful login
        loginAttempts.remove(email);
        accountLockTime.remove(email);

        return user;
    }
    
    private boolean isAccountLocked(String email) {
        LocalDateTime lockTime = accountLockTime.get(email);
        if (lockTime != null) {
            if (LocalDateTime.now().isAfter(lockTime.plusMinutes(LOCK_DURATION_MINUTES))) {
                // Lock duration has expired
                accountLockTime.remove(email);
                loginAttempts.remove(email);
                return false;
            }
            return true;
        }
        return false;
    }
    
    @Transactional
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + user.getId()));
        
        // Update the fields that can be changed
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setPassword(user.getPassword());
        
        // Save and return updated user
        return userRepository.save(existingUser);
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }
    
    @Transactional
    public void disableUser(Long userId) {
        User user = getUserById(userId);
        user.setEnabled(false);
        userRepository.save(user);
    }
    
    @Transactional
    public void enableUser(Long userId) {
        User user = getUserById(userId);
        user.setEnabled(true);
        userRepository.save(user);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public List<User> getCustomerUsers() {
        return userRepository.findByRole(UserRole.CUSTOMER);
    }
    
    public List<User> getAdminUsers() {
        return userRepository.findByRole(UserRole.ADMIN);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
} 