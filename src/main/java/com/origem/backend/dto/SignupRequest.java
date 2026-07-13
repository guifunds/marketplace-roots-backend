package com.origem.backend.dto;

import com.origem.backend.domain.ProfileType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotNull(message = "Campo obrigatório")
        ProfileType profileType,

        @NotBlank(message = "Campo obrigatório")
        @Size(max = 255, message = "Nome muito longo")
        String name,

        @NotBlank(message = "Campo obrigatório")
        @Email(message = "E-mail inválido")
        @Size(max = 255, message = "E-mail muito longo")
        String email,

        @NotBlank(message = "Campo obrigatório")
        @Pattern(regexp = "^\\+?[0-9()\\-\\s]{8,20}$", message = "Telefone inválido")
        String phone,

        @NotBlank(message = "Campo obrigatório")
        @Size(max = 50, message = "Documento muito longo")
        String document,

        @NotBlank(message = "Campo obrigatório")
        @Size(max = 100, message = "País inválido")
        String country,

        @Pattern(regexp = "pt|en", message = "Idioma inválido")
        String lang
) {
}
