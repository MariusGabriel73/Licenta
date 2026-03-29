package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Informații specifice medicului.
 */
@Table("medic")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Medic implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("grad_profesional")
    private String gradProfesional;

    @Column("telefon")
    private String telefon;

    @Column("disponibil")
    private Boolean disponibil;

    @org.springframework.data.annotation.Transient
    private User user;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "medicis" }, allowSetters = true)
    private Sectie sectie;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "locatie", "programares", "medicis" }, allowSetters = true)
    private Set<Clinica> clinicis = new HashSet<>();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "medic", "locatie" }, allowSetters = true)
    private Set<Program> programs = new HashSet<>();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "pacient", "medic", "clinica", "fisaMedicala", "raportProgramare" }, allowSetters = true)
    private Set<Programare> programares = new HashSet<>();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "medicis" }, allowSetters = true)
    private Set<Specializare> specializaris = new HashSet<>();

    @Column("user_id")
    private Long userId;

    @Column("sectie_id")
    private Long sectieId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Medic id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGradProfesional() {
        return this.gradProfesional;
    }

    public Medic gradProfesional(String gradProfesional) {
        this.setGradProfesional(gradProfesional);
        return this;
    }

    public void setGradProfesional(String gradProfesional) {
        this.gradProfesional = gradProfesional;
    }

    public String getTelefon() {
        return this.telefon;
    }

    public Medic telefon(String telefon) {
        this.setTelefon(telefon);
        return this;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public Boolean getDisponibil() {
        return this.disponibil;
    }

    public Medic disponibil(Boolean disponibil) {
        this.setDisponibil(disponibil);
        return this;
    }

    public void setDisponibil(Boolean disponibil) {
        this.disponibil = disponibil;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public Medic user(User user) {
        this.setUser(user);
        return this;
    }

    public Sectie getSectie() {
        return this.sectie;
    }

    public void setSectie(Sectie sectie) {
        this.sectie = sectie;
        this.sectieId = sectie != null ? sectie.getId() : null;
    }

    public Medic sectie(Sectie sectie) {
        this.setSectie(sectie);
        return this;
    }

    public Set<Clinica> getClinicis() {
        return this.clinicis;
    }

    public void setClinicis(Set<Clinica> clinicas) {
        this.clinicis = clinicas;
    }

    public Medic clinicis(Set<Clinica> clinicas) {
        this.setClinicis(clinicas);
        return this;
    }

    public Medic addClinici(Clinica clinica) {
        this.clinicis.add(clinica);
        return this;
    }

    public Medic removeClinici(Clinica clinica) {
        this.clinicis.remove(clinica);
        return this;
    }

    public Set<Program> getPrograms() {
        return this.programs;
    }

    public void setPrograms(Set<Program> programs) {
        if (this.programs != null) {
            this.programs.forEach(i -> i.setMedic(null));
        }
        if (programs != null) {
            programs.forEach(i -> i.setMedic(this));
        }
        this.programs = programs;
    }

    public Medic programs(Set<Program> programs) {
        this.setPrograms(programs);
        return this;
    }

    public Medic addProgram(Program program) {
        this.programs.add(program);
        program.setMedic(this);
        return this;
    }

    public Medic removeProgram(Program program) {
        this.programs.remove(program);
        program.setMedic(null);
        return this;
    }

    public Set<Programare> getProgramares() {
        return this.programares;
    }

    public void setProgramares(Set<Programare> programares) {
        if (this.programares != null) {
            this.programares.forEach(i -> i.setMedic(null));
        }
        if (programares != null) {
            programares.forEach(i -> i.setMedic(this));
        }
        this.programares = programares;
    }

    public Medic programares(Set<Programare> programares) {
        this.setProgramares(programares);
        return this;
    }

    public Medic addProgramare(Programare programare) {
        this.programares.add(programare);
        programare.setMedic(this);
        return this;
    }

    public Medic removeProgramare(Programare programare) {
        this.programares.remove(programare);
        programare.setMedic(null);
        return this;
    }

    public Set<Specializare> getSpecializaris() {
        return this.specializaris;
    }

    public void setSpecializaris(Set<Specializare> specializares) {
        this.specializaris = specializares;
    }

    public Medic specializaris(Set<Specializare> specializares) {
        this.setSpecializaris(specializares);
        return this;
    }

    public Medic addSpecializari(Specializare specializare) {
        this.specializaris.add(specializare);
        return this;
    }

    public Medic removeSpecializari(Specializare specializare) {
        this.specializaris.remove(specializare);
        return this;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
    }

    public Long getSectieId() {
        return this.sectieId;
    }

    public void setSectieId(Long sectie) {
        this.sectieId = sectie;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Medic)) {
            return false;
        }
        return getId() != null && getId().equals(((Medic) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Medic{" +
            "id=" + getId() +
            ", gradProfesional='" + getGradProfesional() + "'" +
            ", telefon='" + getTelefon() + "'" +
            ", disponibil='" + getDisponibil() + "'" +
            "}";
    }
}
