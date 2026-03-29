package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Medic} entity.
 */
@Schema(description = "Informații specifice medicului.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MedicDTO implements Serializable {

    private Long id;

    private String gradProfesional;

    private String telefon;

    private Boolean disponibil;

    private UserDTO user;

    private SectieDTO sectie;

    private Set<ClinicaDTO> clinicis = new HashSet<>();

    private Set<SpecializareDTO> specializaris = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGradProfesional() {
        return gradProfesional;
    }

    public void setGradProfesional(String gradProfesional) {
        this.gradProfesional = gradProfesional;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public Boolean getDisponibil() {
        return disponibil;
    }

    public void setDisponibil(Boolean disponibil) {
        this.disponibil = disponibil;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public SectieDTO getSectie() {
        return sectie;
    }

    public void setSectie(SectieDTO sectie) {
        this.sectie = sectie;
    }

    public Set<ClinicaDTO> getClinicis() {
        return clinicis;
    }

    public void setClinicis(Set<ClinicaDTO> clinicis) {
        this.clinicis = clinicis;
    }

    public Set<SpecializareDTO> getSpecializaris() {
        return specializaris;
    }

    public void setSpecializaris(Set<SpecializareDTO> specializaris) {
        this.specializaris = specializaris;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MedicDTO)) {
            return false;
        }

        MedicDTO medicDTO = (MedicDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, medicDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MedicDTO{" +
            "id=" + getId() +
            ", gradProfesional='" + getGradProfesional() + "'" +
            ", telefon='" + getTelefon() + "'" +
            ", disponibil='" + getDisponibil() + "'" +
            ", user=" + getUser() +
            ", sectie=" + getSectie() +
            ", clinicis=" + getClinicis() +
            ", specializaris=" + getSpecializaris() +
            "}";
    }
}
