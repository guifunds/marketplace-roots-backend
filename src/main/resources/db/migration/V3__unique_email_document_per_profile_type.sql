-- Normaliza e-mails existentes para minúsculo antes de aplicar a restrição de unicidade
-- (o e-mail passa a ser sempre gravado em minúsculo a partir de agora — ver SignupService).
UPDATE signups SET email = LOWER(email);

DROP INDEX idx_signups_email;

-- Um e-mail (ou CNPJ/documento) só pode ter um cadastro por tipo de perfil — a mesma empresa
-- pode se cadastrar como fornecedora E como compradora, mas não duas vezes com o mesmo perfil.
CREATE UNIQUE INDEX idx_signups_email_profile_type ON signups (email, profile_type);
CREATE UNIQUE INDEX idx_signups_document_profile_type ON signups (document, profile_type)
    WHERE document IS NOT NULL AND document <> '';
