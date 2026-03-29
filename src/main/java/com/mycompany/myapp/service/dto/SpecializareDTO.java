package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Specializare} entity.
 */
@Schema(description = "Domeniu medical al unui medic.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SpecializareDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String nume;

    private Set<MedicDTO> medicis = new HashSet<>();

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

    public Set<MedicDTO> getMedicis() {
        return medicis;
    }

    public void setMedicis(Set<MedicDTO> medicis) {
        this.medicis = medicis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpecializareDTO)) {
            return false;
        }

        SpecializareDTO specializareDTO = (SpecializareDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, specializareDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SpecializareDTO{" +
            "id=" + getId() +
            ", nume='" + getNume() + "'" +
            ", medicis=" + getMedicis() +
            "}";
    }
}
