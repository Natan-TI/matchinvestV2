package com.matchinvest.api.vo;

import java.util.Objects;
import java.util.regex.Pattern;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor // exigido pelo JPA
@Getter
public class Email {
	@Column(name = "email", nullable = false, unique = true)
    private String value;

    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");

    public Email(String value) {
        if (value == null || !EMAIL_REGEX.matcher(value).matches()) {
            throw new IllegalArgumentException("E-mail inválido: " + value);
        }
        this.value = value.toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
