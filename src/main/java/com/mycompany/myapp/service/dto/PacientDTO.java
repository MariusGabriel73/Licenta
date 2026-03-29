package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Pacient} entity.
 */
@Schema(description = "Informații specifice pacientului.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PacientDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String cnp;

    private String telefon;

    private LocalDate dataNasterii;

    private String adresa;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCnp() {
        return cnp;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public LocalDate getDataNasterii() {
        return dataNasterii;
    }

    public void setDataNasterii(LocalDate dataNasterii) {
        this.dataNasterii = dataNasterii;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PacientDTO)) {
            return false;
        }

        PacientDTO pacientDTO = (PacientDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, pacientDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PacientDTO{" +
            "id=" + getId() +
            ", cnp='" + getCnp() + "'" +
            ", telefon='" + getTelefon() + "'" +
            ", dataNasterii='" + getDataNasterii() + "'" +
            ", adresa='" + getAdresa() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
