package com.mse_project.auth;

import com.mse_project.admin.entity.Admin;
import com.mse_project.admin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String admincode) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByAdminCode(admincode)
                .orElseThrow(() -> new UsernameNotFoundException("유효한 관리자 코드가 아닙니다" + admincode));
        return new UserDetailsImpl(admin);
    }
}
