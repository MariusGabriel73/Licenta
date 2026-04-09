package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Sectie} entity.
 */
@Schema(description = "Domeniu medical al unui medic / Secția din clinică.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SectieDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String nume;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SectieDTO sectieDTO)) {
            return false;
        }

        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, sectieDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SectieDTO{" +
            "id=" + getId() +
            ", nume='" + getNume() + "'" +
            "}";
    }
}
