package com.jobportal.service;

import com.jobportal.dto.ProfileDto;
import com.jobportal.dto.RegisterDto;
import com.jobportal.model.User;
import com.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User register(RegisterDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setUsername(dto.getUsername().toLowerCase().trim());
        user.setEmail(dto.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(User.Role.valueOf(dto.getRole().toUpperCase()));

        if (dto.getRole().equalsIgnoreCase("EMPLOYER")) {
            user.setCompany(dto.getCompany());
            user.setLocation(dto.getLocation());
        }
        return userRepository.save(user);
    }

    @Transactional
    public User updateProfile(String username, ProfileDto dto) {
        User user = findByUsername(username);
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setSkills(dto.getSkills());
        user.setExperience(dto.getExperience());
        user.setAge(dto.getAge());
        user.setGender(dto.getGender());
        user.setPhone(dto.getPhone());
        user.setTenthMarks(dto.getTenthMarks());
        user.setTwelfthMarks(dto.getTwelfthMarks());
        user.setUgDetails(dto.getUgDetails());
        user.setPgDetails(dto.getPgDetails());
        user.setCollegeName(dto.getCollegeName());
        user.setCollegePercentage(dto.getCollegePercentage());
        user.setExperienceCompany(dto.getExperienceCompany());
        user.setExperienceYears(dto.getExperienceYears());
        user.setCompany(dto.getCompany());
        user.setLocation(dto.getLocation());
        return userRepository.save(user);
    }

    @Transactional
    public void updatePhotoPath(String username, String photoPath) {
        User user = findByUsername(username);
        user.setPhotoPath(photoPath);
        userRepository.save(user);
    }

    @Transactional
    public void updateResumePath(String username, String resumePath) {
        User user = findByUsername(username);
        user.setResumePath(resumePath);
        userRepository.save(user);
    }
}
