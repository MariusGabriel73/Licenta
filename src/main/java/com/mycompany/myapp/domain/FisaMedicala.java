package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Fișa medicală completată după consultație.
 */
@Table("fisa_medicala")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FisaMedicala implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("diagnostic")
    private String diagnostic;

    @Column("tratament")
    private String tratament;

    @Column("recomandari")
    private String recomandari;

    @Column("data_consultatie")
    private ZonedDateTime dataConsultatie;

    @org.springframework.data.annotation.Transient
    private Programare programare;

    @Column("programare_id")
    private Long programareId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FisaMedicala id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDiagnostic() {
        return this.diagnostic;
    }

    public FisaMedicala diagnostic(String diagnostic) {
        this.setDiagnostic(diagnostic);
        return this;
    }

    public void setDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
    }

    public String getTratament() {
        return this.tratament;
    }

    public FisaMedicala tratament(String tratament) {
        this.setTratament(tratament);
        return this;
    }

    public void setTratament(String tratament) {
        this.tratament = tratament;
    }

    public String getRecomandari() {
        return this.recomandari;
    }

    public FisaMedicala recomandari(String recomandari) {
        this.setRecomandari(recomandari);
        return this;
    }

    public void setRecomandari(String recomandari) {
        this.recomandari = recomandari;
    }

    public ZonedDateTime getDataConsultatie() {
        return this.dataConsultatie;
    }

    public FisaMedicala dataConsultatie(ZonedDateTime dataConsultatie) {
        this.setDataConsultatie(dataConsultatie);
        return this;
    }

    public void setDataConsultatie(ZonedDateTime dataConsultatie) {
        this.dataConsultatie = dataConsultatie;
    }

    public Programare getProgramare() {
        return this.programare;
    }

    public void setProgramare(Programare programare) {
        this.programare = programare;
        this.programareId = programare != null ? programare.getId() : null;
    }

    public FisaMedicala programare(Programare programare) {
        this.setProgramare(programare);
        return this;
    }

    public Long getProgramareId() {
        return this.programareId;
    }

    public void setProgramareId(Long programare) {
        this.programareId = programare;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FisaMedicala)) {
            return false;
        }
        return getId() != null && getId().equals(((FisaMedicala) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FisaMedicala{" +
            "id=" + getId() +
            ", diagnostic='" + getDiagnostic() + "'" +
            ", tratament='" + getTratament() + "'" +
            ", recomandari='" + getRecomandari() + "'" +
            ", dataConsultatie='" + getDataConsultatie() + "'" +
            "}";
    }
}
