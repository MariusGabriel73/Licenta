package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.ProgramareStatus;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Programarea unui pacient.
 */
@Table("programare")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Programare implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("data_programare")
    private ZonedDateTime dataProgramare;

    @Column("status")
    private ProgramareStatus status;

    @Column("observatii")
    private String observatii;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "programares" }, allowSetters = true)
    private Pacient pacient;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "sectie", "clinicis", "programs", "programares", "specializaris" }, allowSetters = true)
    private Medic medic;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "locatie", "programares", "medicis" }, allowSetters = true)
    private Clinica clinica;

    @org.springframework.data.annotation.Transient
    private FisaMedicala fisaMedicala;

    @org.springframework.data.annotation.Transient
    private RaportProgramare raportProgramare;

    @Column("pacient_id")
    private Long pacientId;

    @Column("medic_id")
    private Long medicId;

    @Column("clinica_id")
    private Long clinicaId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Programare id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDataProgramare() {
        return this.dataProgramare;
    }

    public Programare dataProgramare(ZonedDateTime dataProgramare) {
        this.setDataProgramare(dataProgramare);
        return this;
    }

    public void setDataProgramare(ZonedDateTime dataProgramare) {
        this.dataProgramare = dataProgramare;
    }

    public ProgramareStatus getStatus() {
        return this.status;
    }

    public Programare status(ProgramareStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ProgramareStatus status) {
        this.status = status;
    }

    public String getObservatii() {
        return this.observatii;
    }

    public Programare observatii(String observatii) {
        this.setObservatii(observatii);
        return this;
    }

    public void setObservatii(String observatii) {
        this.observatii = observatii;
    }

    public Pacient getPacient() {
        return this.pacient;
    }

    public void setPacient(Pacient pacient) {
        this.pacient = pacient;
        this.pacientId = pacient != null ? pacient.getId() : null;
    }

    public Programare pacient(Pacient pacient) {
        this.setPacient(pacient);
        return this;
    }

    public Medic getMedic() {
        return this.medic;
    }

    public void setMedic(Medic medic) {
        this.medic = medic;
        this.medicId = medic != null ? medic.getId() : null;
    }

    public Programare medic(Medic medic) {
        this.setMedic(medic);
        return this;
    }

    public Clinica getClinica() {
        return this.clinica;
    }

    public void setClinica(Clinica clinica) {
        this.clinica = clinica;
        this.clinicaId = clinica != null ? clinica.getId() : null;
    }

    public Programare clinica(Clinica clinica) {
        this.setClinica(clinica);
        return this;
    }

    public FisaMedicala getFisaMedicala() {
        return this.fisaMedicala;
    }

    public void setFisaMedicala(FisaMedicala fisaMedicala) {
        if (this.fisaMedicala != null) {
            this.fisaMedicala.setProgramare(null);
        }
        if (fisaMedicala != null) {
            fisaMedicala.setProgramare(this);
        }
        this.fisaMedicala = fisaMedicala;
    }

    public Programare fisaMedicala(FisaMedicala fisaMedicala) {
        this.setFisaMedicala(fisaMedicala);
        return this;
    }

    public RaportProgramare getRaportProgramare() {
        return this.raportProgramare;
    }

    public void setRaportProgramare(RaportProgramare raportProgramare) {
        if (this.raportProgramare != null) {
            this.raportProgramare.setProgramare(null);
        }
        if (raportProgramare != null) {
            raportProgramare.setProgramare(this);
        }
        this.raportProgramare = raportProgramare;
    }

    public Programare raportProgramare(RaportProgramare raportProgramare) {
        this.setRaportProgramare(raportProgramare);
        return this;
    }

    public Long getPacientId() {
        return this.pacientId;
    }

    public void setPacientId(Long pacient) {
        this.pacientId = pacient;
    }

    public Long getMedicId() {
        return this.medicId;
    }

    public void setMedicId(Long medic) {
        this.medicId = medic;
    }

    public Long getClinicaId() {
        return this.clinicaId;
    }

    public void setClinicaId(Long clinica) {
        this.clinicaId = clinica;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Programare)) {
            return false;
        }
        return getId() != null && getId().equals(((Programare) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Programare{" +
            "id=" + getId() +
            ", dataProgramare='" + getDataProgramare() + "'" +
            ", status='" + getStatus() + "'" +
            ", observatii='" + getObservatii() + "'" +
            "}";
    }
}
