package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Domeniu medical al unui medic.
 */
@Table("specializare")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Specializare implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("nume")
    private String nume;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "specializaris", "clinicis", "programs", "programares" }, allowSetters = true)
    private Set<Medic> medicis = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Specializare id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNume() {
        return this.nume;
    }

    public Specializare nume(String nume) {
        this.setNume(nume);
        return this;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public Set<Medic> getMedicis() {
        return this.medicis;
    }

    public void setMedicis(Set<Medic> medics) {
        if (this.medicis != null) {
            this.medicis.forEach(i -> i.removeSpecializari(this));
        }
        if (medics != null) {
            medics.forEach(i -> i.addSpecializari(this));
        }
        this.medicis = medics;
    }

    public Specializare medicis(Set<Medic> medics) {
        this.setMedicis(medics);
        return this;
    }

    public Specializare addMedici(Medic medic) {
        this.medicis.add(medic);
        medic.getSpecializaris().add(this);
        return this;
    }

    public Specializare removeMedici(Medic medic) {
        this.medicis.remove(medic);
        medic.getSpecializaris().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Specializare)) {
            return false;
        }
        return getId() != null && getId().equals(((Specializare) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Specializare{" +
            "id=" + getId() +
            ", nume='" + getNume() + "'" +
            "}";
    }
}
