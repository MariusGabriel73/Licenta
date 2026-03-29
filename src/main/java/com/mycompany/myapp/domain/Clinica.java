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
 * Informații despre o clinică.
 */
@Table("clinica")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Clinica implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("nume")
    private String nume;

    @Column("telefon")
    private String telefon;

    @Column("email")
    private String email;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "programs", "clinicis" }, allowSetters = true)
    private Locatie locatie;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "pacient", "medic", "clinica", "fisaMedicala", "raportProgramare" }, allowSetters = true)
    private Set<Programare> programares = new HashSet<>();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "sectie", "clinicis", "programs", "programares", "specializaris" }, allowSetters = true)
    private Set<Medic> medicis = new HashSet<>();

    @Column("locatie_id")
    private Long locatieId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Clinica id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNume() {
        return this.nume;
    }

    public Clinica nume(String nume) {
        this.setNume(nume);
        return this;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getTelefon() {
        return this.telefon;
    }

    public Clinica telefon(String telefon) {
        this.setTelefon(telefon);
        return this;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getEmail() {
        return this.email;
    }

    public Clinica email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Locatie getLocatie() {
        return this.locatie;
    }

    public void setLocatie(Locatie locatie) {
        this.locatie = locatie;
        this.locatieId = locatie != null ? locatie.getId() : null;
    }

    public Clinica locatie(Locatie locatie) {
        this.setLocatie(locatie);
        return this;
    }

    public Set<Programare> getProgramares() {
        return this.programares;
    }

    public void setProgramares(Set<Programare> programares) {
        if (this.programares != null) {
            this.programares.forEach(i -> i.setClinica(null));
        }
        if (programares != null) {
            programares.forEach(i -> i.setClinica(this));
        }
        this.programares = programares;
    }

    public Clinica programares(Set<Programare> programares) {
        this.setProgramares(programares);
        return this;
    }

    public Clinica addProgramare(Programare programare) {
        this.programares.add(programare);
        programare.setClinica(this);
        return this;
    }

    public Clinica removeProgramare(Programare programare) {
        this.programares.remove(programare);
        programare.setClinica(null);
        return this;
    }

    public Set<Medic> getMedicis() {
        return this.medicis;
    }

    public void setMedicis(Set<Medic> medics) {
        if (this.medicis != null) {
            this.medicis.forEach(i -> i.removeClinici(this));
        }
        if (medics != null) {
            medics.forEach(i -> i.addClinici(this));
        }
        this.medicis = medics;
    }

    public Clinica medicis(Set<Medic> medics) {
        this.setMedicis(medics);
        return this;
    }

    public Clinica addMedici(Medic medic) {
        this.medicis.add(medic);
        medic.getClinicis().add(this);
        return this;
    }

    public Clinica removeMedici(Medic medic) {
        this.medicis.remove(medic);
        medic.getClinicis().remove(this);
        return this;
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
        if (!(o instanceof Clinica)) {
            return false;
        }
        return getId() != null && getId().equals(((Clinica) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Clinica{" +
            "id=" + getId() +
            ", nume='" + getNume() + "'" +
            ", telefon='" + getTelefon() + "'" +
            ", email='" + getEmail() + "'" +
            "}";
    }
}
