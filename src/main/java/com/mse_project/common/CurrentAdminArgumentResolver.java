package com.mse_project.common;

import com.mse_project.admin.dto.AdminSessionDto;
import com.mse_project.admin.entity.Admin;
import com.mse_project.auth.UserDetailsImpl;
import com.mse_project.common.annotation.CurrentAdmin;
import jakarta.annotation.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CurrentAdminArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentAdmin.class) &&
                parameter.getParameterType().equals(AdminSessionDto.class);
    }

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return null;

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Admin admin = userDetails.getAdmin();

        return new AdminSessionDto(admin.getAdminId(), admin.getAdminCode());
    }
}
