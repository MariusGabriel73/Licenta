package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Locatie} entity.
 */
@Schema(description = "Informații despre o locație fizică.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LocatieDTO implements Serializable {

    private Long id;

    private String oras;

    private String adresa;

    private String codPostal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOras() {
        return oras;
    }

    public void setOras(String oras) {
        this.oras = oras;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public String getCodPostal() {
        return codPostal;
    }

    public void setCodPostal(String codPostal) {
        this.codPostal = codPostal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LocatieDTO)) {
            return false;
        }

        LocatieDTO locatieDTO = (LocatieDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, locatieDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LocatieDTO{" +
            "id=" + getId() +
            ", oras='" + getOras() + "'" +
            ", adresa='" + getAdresa() + "'" +
            ", codPostal='" + getCodPostal() + "'" +
            "}";
    }
}
