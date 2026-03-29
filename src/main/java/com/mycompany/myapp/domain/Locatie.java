package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Informații despre o locație fizică.
 */
@Table("locatie")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Locatie implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("oras")
    private String oras;

    @Column("adresa")
    private String adresa;

    @Column("cod_postal")
    private String codPostal;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "medic", "locatie" }, allowSetters = true)
    private Set<Program> programs = new HashSet<>();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "locatie", "programares", "medicis" }, allowSetters = true)
    private Set<Clinica> clinicis = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Locatie id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOras() {
        return this.oras;
    }

    public Locatie oras(String oras) {
        this.setOras(oras);
        return this;
    }

    public void setOras(String oras) {
        this.oras = oras;
    }

    public String getAdresa() {
        return this.adresa;
    }

    public Locatie adresa(String adresa) {
        this.setAdresa(adresa);
        return this;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public String getCodPostal() {
        return this.codPostal;
    }

    public Locatie codPostal(String codPostal) {
        this.setCodPostal(codPostal);
        return this;
    }

    public void setCodPostal(String codPostal) {
        this.codPostal = codPostal;
    }

    public Set<Program> getPrograms() {
        return this.programs;
    }

    public void setPrograms(Set<Program> programs) {
        if (this.programs != null) {
            this.programs.forEach(i -> i.setLocatie(null));
        }
        if (programs != null) {
            programs.forEach(i -> i.setLocatie(this));
        }
        this.programs = programs;
    }

    public Locatie programs(Set<Program> programs) {
        this.setPrograms(programs);
        return this;
    }

    public Locatie addProgram(Program program) {
        this.programs.add(program);
        program.setLocatie(this);
        return this;
    }

    public Locatie removeProgram(Program program) {
        this.programs.remove(program);
        program.setLocatie(null);
        return this;
    }

    public Set<Clinica> getClinicis() {
        return this.clinicis;
    }

    public void setClinicis(Set<Clinica> clinicas) {
        if (this.clinicis != null) {
            this.clinicis.forEach(i -> i.setLocatie(null));
        }
        if (clinicas != null) {
            clinicas.forEach(i -> i.setLocatie(this));
        }
        this.clinicis = clinicas;
    }

    public Locatie clinicis(Set<Clinica> clinicas) {
        this.setClinicis(clinicas);
        return this;
    }

    public Locatie addClinici(Clinica clinica) {
        this.clinicis.add(clinica);
        clinica.setLocatie(this);
        return this;
    }

    public Locatie removeClinici(Clinica clinica) {
        this.clinicis.remove(clinica);
        clinica.setLocatie(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Locatie)) {
            return false;
        }
        return getId() != null && getId().equals(((Locatie) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Locatie{" +
            "id=" + getId() +
            ", oras='" + getOras() + "'" +
            ", adresa='" + getAdresa() + "'" +
            ", codPostal='" + getCodPostal() + "'" +
            "}";
    }
}
