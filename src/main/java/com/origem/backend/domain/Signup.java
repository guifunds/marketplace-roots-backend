package com.origem.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "signups")
public class Signup {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProfileType profileType;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    private String phone;

    private String document;

    private String country;

    @Column(nullable = false, length = 2)
    private String lang;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SignupStatus status;

    @Column(name = "stripe_payment_intent_id")
    private String stripePaymentIntentId;

    @Column(name = "founding_fee_amount_cents", nullable = false)
    private long foundingFeeAmountCents;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "paid_at")
    private Instant paidAt;

    public Signup() {
    }

    public Signup(UUID id, ProfileType profileType, String name, String email, String phone, String document,
                  String country, String lang, SignupStatus status, String stripePaymentIntentId,
                  long foundingFeeAmountCents, String currency, Instant createdAt, Instant paidAt) {
        this.id = id;
        this.profileType = profileType;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.document = document;
        this.country = country;
        this.lang = lang;
        this.status = status;
        this.stripePaymentIntentId = stripePaymentIntentId;
        this.foundingFeeAmountCents = foundingFeeAmountCents;
        this.currency = currency;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ProfileType getProfileType() {
        return profileType;
    }

    public void setProfileType(ProfileType profileType) {
        this.profileType = profileType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public SignupStatus getStatus() {
        return status;
    }

    public void setStatus(SignupStatus status) {
        this.status = status;
    }

    public String getStripePaymentIntentId() {
        return stripePaymentIntentId;
    }

    public void setStripePaymentIntentId(String stripePaymentIntentId) {
        this.stripePaymentIntentId = stripePaymentIntentId;
    }

    public long getFoundingFeeAmountCents() {
        return foundingFeeAmountCents;
    }

    public void setFoundingFeeAmountCents(long foundingFeeAmountCents) {
        this.foundingFeeAmountCents = foundingFeeAmountCents;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }

    public static final class Builder {
        private UUID id;
        private ProfileType profileType;
        private String name;
        private String email;
        private String phone;
        private String document;
        private String country;
        private String lang;
        private SignupStatus status;
        private String stripePaymentIntentId;
        private long foundingFeeAmountCents;
        private String currency;
        private Instant createdAt;
        private Instant paidAt;

        private Builder() {
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder profileType(ProfileType profileType) {
            this.profileType = profileType;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder document(String document) {
            this.document = document;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder lang(String lang) {
            this.lang = lang;
            return this;
        }

        public Builder status(SignupStatus status) {
            this.status = status;
            return this;
        }

        public Builder stripePaymentIntentId(String stripePaymentIntentId) {
            this.stripePaymentIntentId = stripePaymentIntentId;
            return this;
        }

        public Builder foundingFeeAmountCents(long foundingFeeAmountCents) {
            this.foundingFeeAmountCents = foundingFeeAmountCents;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder paidAt(Instant paidAt) {
            this.paidAt = paidAt;
            return this;
        }

        public Signup build() {
            return new Signup(id, profileType, name, email, phone, document, country, lang, status,
                    stripePaymentIntentId, foundingFeeAmountCents, currency, createdAt, paidAt);
        }
    }
}
