package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Raport legat de durata și desfășurarea consultației.
 */
@Table("raport_programare")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RaportProgramare implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("ora_programata")
    private ZonedDateTime oraProgramata;

    @Column("ora_inceput_consultatie")
    private ZonedDateTime oraInceputConsultatie;

    @Column("durata_consultatie")
    private Integer durataConsultatie;

    @org.springframework.data.annotation.Transient
    private Programare programare;

    @Column("programare_id")
    private Long programareId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RaportProgramare id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getOraProgramata() {
        return this.oraProgramata;
    }

    public RaportProgramare oraProgramata(ZonedDateTime oraProgramata) {
        this.setOraProgramata(oraProgramata);
        return this;
    }

    public void setOraProgramata(ZonedDateTime oraProgramata) {
        this.oraProgramata = oraProgramata;
    }

    public ZonedDateTime getOraInceputConsultatie() {
        return this.oraInceputConsultatie;
    }

    public RaportProgramare oraInceputConsultatie(ZonedDateTime oraInceputConsultatie) {
        this.setOraInceputConsultatie(oraInceputConsultatie);
        return this;
    }

    public void setOraInceputConsultatie(ZonedDateTime oraInceputConsultatie) {
        this.oraInceputConsultatie = oraInceputConsultatie;
    }

    public Integer getDurataConsultatie() {
        return this.durataConsultatie;
    }

    public RaportProgramare durataConsultatie(Integer durataConsultatie) {
        this.setDurataConsultatie(durataConsultatie);
        return this;
    }

    public void setDurataConsultatie(Integer durataConsultatie) {
        this.durataConsultatie = durataConsultatie;
    }

    public Programare getProgramare() {
        return this.programare;
    }

    public void setProgramare(Programare programare) {
        this.programare = programare;
        this.programareId = programare != null ? programare.getId() : null;
    }

    public RaportProgramare programare(Programare programare) {
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
        if (!(o instanceof RaportProgramare)) {
            return false;
        }
        return getId() != null && getId().equals(((RaportProgramare) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RaportProgramare{" +
            "id=" + getId() +
            ", oraProgramata='" + getOraProgramata() + "'" +
            ", oraInceputConsultatie='" + getOraInceputConsultatie() + "'" +
            ", durataConsultatie=" + getDurataConsultatie() +
            "}";
    }
}
