package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Clinica} entity.
 */
@Schema(description = "Informații despre o clinică.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClinicaDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String nume;

    private String telefon;

    private String email;

    private LocatieDTO locatie;

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

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocatieDTO getLocatie() {
        return locatie;
    }

    public void setLocatie(LocatieDTO locatie) {
        this.locatie = locatie;
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
        if (!(o instanceof ClinicaDTO)) {
            return false;
        }

        ClinicaDTO clinicaDTO = (ClinicaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, clinicaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClinicaDTO{" +
            "id=" + getId() +
            ", nume='" + getNume() + "'" +
            ", telefon='" + getTelefon() + "'" +
            ", email='" + getEmail() + "'" +
            ", locatie=" + getLocatie() +
            ", medicis=" + getMedicis() +
            "}";
    }
}
