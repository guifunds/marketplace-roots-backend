package com.origem.backend.exception;

import java.util.UUID;

public class SignupNotFoundException extends RuntimeException {
    public SignupNotFoundException(UUID id) {
        super("Cadastro não encontrado: " + id);
    }
}
