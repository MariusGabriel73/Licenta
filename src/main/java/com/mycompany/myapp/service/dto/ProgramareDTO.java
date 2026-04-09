package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.ProgramareStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Programare} entity.
 */
@Schema(description = "Programarea unui pacient.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProgramareDTO implements Serializable {

    private Long id;

    private ZonedDateTime dataProgramare;

    private ProgramareStatus status;

    private String observatii;

    private PacientDTO pacient;

    private MedicDTO medic;

    private ClinicaDTO clinica;

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(value = { "programare" }, allowSetters = true)
    private FisaMedicalaDTO fisaMedicala;

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(value = { "programare" }, allowSetters = true)
    private RaportProgramareDTO raportProgramare;

    private Long pacientId;

    private Long medicId;

    private Long clinicaId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDataProgramare() {
        return dataProgramare;
    }

    public void setDataProgramare(ZonedDateTime dataProgramare) {
        this.dataProgramare = dataProgramare;
    }

    public ProgramareStatus getStatus() {
        return status;
    }

    public void setStatus(ProgramareStatus status) {
        this.status = status;
    }

    public String getObservatii() {
        return observatii;
    }

    public void setObservatii(String observatii) {
        this.observatii = observatii;
    }

    public PacientDTO getPacient() {
        return pacient;
    }

    public void setPacient(PacientDTO pacient) {
        this.pacient = pacient;
    }

    public MedicDTO getMedic() {
        return medic;
    }

    public void setMedic(MedicDTO medic) {
        this.medic = medic;
    }

    public ClinicaDTO getClinica() {
        return clinica;
    }

    public void setClinica(ClinicaDTO clinica) {
        this.clinica = clinica;
    }

    public FisaMedicalaDTO getFisaMedicala() {
        return fisaMedicala;
    }

    public void setFisaMedicala(FisaMedicalaDTO fisaMedicala) {
        this.fisaMedicala = fisaMedicala;
    }

    public RaportProgramareDTO getRaportProgramare() {
        return raportProgramare;
    }

    public void setRaportProgramare(RaportProgramareDTO raportProgramare) {
        this.raportProgramare = raportProgramare;
    }

    public Long getPacientId() {
        return pacientId;
    }

    public void setPacientId(Long pacientId) {
        this.pacientId = pacientId;
    }

    public Long getMedicId() {
        return medicId;
    }

    public void setMedicId(Long medicId) {
        this.medicId = medicId;
    }

    public Long getClinicaId() {
        return clinicaId;
    }

    public void setClinicaId(Long clinicaId) {
        this.clinicaId = clinicaId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProgramareDTO programareDTO)) {
            return false;
        }

        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, programareDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProgramareDTO{" +
            "id=" + getId() +
            ", dataProgramare='" + getDataProgramare() + "'" +
            ", status='" + getStatus() + "'" +
            ", observatii='" + getObservatii() + "'" +
            ", pacient=" + getPacient() +
            ", medic=" + getMedic() +
            ", clinica=" + getClinica() +
            "}";
    }
}
