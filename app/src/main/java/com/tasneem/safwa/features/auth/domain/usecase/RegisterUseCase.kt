package com.tasneem.safwa.features.auth.domain.usecase

import com.tasneem.safwa.core.util.Resource
import com.tasneem.safwa.features.auth.domain.model.User
import com.tasneem.safwa.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String,
        firstName: String,
        lastName: String,
        phone: String
    ): Resource<User> {
        val fNameTrimmed = firstName.trim()
        val lNameTrimmed = lastName.trim()
        val emailTrimmed = email.trim()
        val phoneTrimmed = phone.trim()

        if (fNameTrimmed.isBlank()) return Resource.Error("error_first_name_empty")
        if (!fNameTrimmed.matches(Regex("^[A-Za-z]+$"))) return Resource.Error("error_name_letters_only_first")

        if (lNameTrimmed.isBlank()) return Resource.Error("error_last_name_empty")
        if (!lNameTrimmed.matches(Regex("^[A-Za-z]+$"))) return Resource.Error("error_name_letters_only_last")

        if (emailTrimmed.isBlank()) return Resource.Error("error_email_empty")
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
        if (!emailTrimmed.matches(emailRegex)) return Resource.Error("error_invalid_email")

        val phoneRegex = Regex("^(010|011|012|015)[0-9]{8}$")
        if (phoneTrimmed.isNotBlank() && !phoneTrimmed.matches(phoneRegex)) return Resource.Error("error_phone_egypt_invalid")

        if (password.isBlank()) return Resource.Error("error_password_empty")
        if (password.length < 6) return Resource.Error("error_password_too_short")
        if (!password.any { it.isUpperCase() }) return Resource.Error("error_password_no_uppercase")
        if (!password.any { it.isLowerCase() }) return Resource.Error("error_password_no_lowercase")
        if (!password.any { it.isDigit() }) return Resource.Error("error_password_no_digit")
        if (!password.any { !it.isLetterOrDigit() }) return Resource.Error("error_password_no_symbol")

        if (confirmPassword.isBlank()) return Resource.Error("error_confirm_password_empty")
        if (password != confirmPassword) return Resource.Error("error_passwords_do_not_match")

        return authRepository.register(emailTrimmed, password, fNameTrimmed, lNameTrimmed, phoneTrimmed)
    }
}