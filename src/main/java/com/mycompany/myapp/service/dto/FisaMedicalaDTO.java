package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.FisaMedicala} entity.
 */
@Schema(description = "Fișa medicală completată după consultație.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FisaMedicalaDTO implements Serializable {

    private Long id;

    private String diagnostic;

    private String tratament;

    private String recomandari;

    private ZonedDateTime dataConsultatie;

    private ProgramareDTO programare;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDiagnostic() {
        return diagnostic;
    }

    public void setDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
    }

    public String getTratament() {
        return tratament;
    }

    public void setTratament(String tratament) {
        this.tratament = tratament;
    }

    public String getRecomandari() {
        return recomandari;
    }

    public void setRecomandari(String recomandari) {
        this.recomandari = recomandari;
    }

    public ZonedDateTime getDataConsultatie() {
        return dataConsultatie;
    }

    public void setDataConsultatie(ZonedDateTime dataConsultatie) {
        this.dataConsultatie = dataConsultatie;
    }

    public ProgramareDTO getProgramare() {
        return programare;
    }

    public void setProgramare(ProgramareDTO programare) {
        this.programare = programare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FisaMedicalaDTO)) {
            return false;
        }

        FisaMedicalaDTO fisaMedicalaDTO = (FisaMedicalaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, fisaMedicalaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FisaMedicalaDTO{" +
            "id=" + getId() +
            ", diagnostic='" + getDiagnostic() + "'" +
            ", tratament='" + getTratament() + "'" +
            ", recomandari='" + getRecomandari() + "'" +
            ", dataConsultatie='" + getDataConsultatie() + "'" +
            ", programare=" + getProgramare() +
            "}";
    }
}
