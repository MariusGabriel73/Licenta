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
 * Domeniu medical al unui medic / Secția din clinică.
 */
@Table("sectie")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Sectie implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("nume")
    private String nume;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "sectie", "clinicis", "programs", "programares", "specializaris" }, allowSetters = true)
    private Set<Medic> medicis = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Sectie id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNume() {
        return this.nume;
    }

    public Sectie nume(String nume) {
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
            this.medicis.forEach(i -> i.setSectie(null));
        }
        if (medics != null) {
            medics.forEach(i -> i.setSectie(this));
        }
        this.medicis = medics;
    }

    public Sectie medicis(Set<Medic> medics) {
        this.setMedicis(medics);
        return this;
    }

    public Sectie addMedici(Medic medic) {
        this.medicis.add(medic);
        medic.setSectie(this);
        return this;
    }

    public Sectie removeMedici(Medic medic) {
        this.medicis.remove(medic);
        medic.setSectie(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sectie)) {
            return false;
        }
        return getId() != null && getId().equals(((Sectie) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Sectie{" +
            "id=" + getId() +
            ", nume='" + getNume() + "'" +
            "}";
    }
}
