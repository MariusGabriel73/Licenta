package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Programul unui medic într-o locație.
 */
@Table("program")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Program implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("ziua_saptamanii")
    private String ziuaSaptamanii;

    @Column("ora_start")
    private ZonedDateTime oraStart;

    @Column("ora_final")
    private ZonedDateTime oraFinal;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "sectie", "clinicis", "programs", "programares", "specializaris" }, allowSetters = true)
    private Medic medic;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "programs", "clinicis" }, allowSetters = true)
    private Locatie locatie;

    @Column("medic_id")
    private Long medicId;

    @Column("locatie_id")
    private Long locatieId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Program id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getZiuaSaptamanii() {
        return this.ziuaSaptamanii;
    }

    public Program ziuaSaptamanii(String ziuaSaptamanii) {
        this.setZiuaSaptamanii(ziuaSaptamanii);
        return this;
    }

    public void setZiuaSaptamanii(String ziuaSaptamanii) {
        this.ziuaSaptamanii = ziuaSaptamanii;
    }

    public ZonedDateTime getOraStart() {
        return this.oraStart;
    }

    public Program oraStart(ZonedDateTime oraStart) {
        this.setOraStart(oraStart);
        return this;
    }

    public void setOraStart(ZonedDateTime oraStart) {
        this.oraStart = oraStart;
    }

    public ZonedDateTime getOraFinal() {
        return this.oraFinal;
    }

    public Program oraFinal(ZonedDateTime oraFinal) {
        this.setOraFinal(oraFinal);
        return this;
    }

    public void setOraFinal(ZonedDateTime oraFinal) {
        this.oraFinal = oraFinal;
    }

    public Medic getMedic() {
        return this.medic;
    }

    public void setMedic(Medic medic) {
        this.medic = medic;
        this.medicId = medic != null ? medic.getId() : null;
    }

    public Program medic(Medic medic) {
        this.setMedic(medic);
        return this;
    }

    public Locatie getLocatie() {
        return this.locatie;
    }

    public void setLocatie(Locatie locatie) {
        this.locatie = locatie;
        this.locatieId = locatie != null ? locatie.getId() : null;
    }

    public Program locatie(Locatie locatie) {
        this.setLocatie(locatie);
        return this;
    }

    public Long getMedicId() {
        return this.medicId;
    }

    public void setMedicId(Long medic) {
        this.medicId = medic;
    }

    public Long getLocatieId() {
        return this.locatieId;
    }

    public void setLocatieId(Long locatie) {
        this.locatieId = locatie;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Program)) {
            return false;
        }
        return getId() != null && getId().equals(((Program) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Program{" +
            "id=" + getId() +
            ", ziuaSaptamanii='" + getZiuaSaptamanii() + "'" +
            ", oraStart='" + getOraStart() + "'" +
            ", oraFinal='" + getOraFinal() + "'" +
            "}";
    }
}
