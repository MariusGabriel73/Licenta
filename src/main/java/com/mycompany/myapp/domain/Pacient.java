package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Informații specifice pacientului.
 */
@Table("pacient")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Pacient implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("cnp")
    private String cnp;

    @Column("telefon")
    private String telefon;

    @Column("data_nasterii")
    private LocalDate dataNasterii;

    @Column("adresa")
    private String adresa;

    @org.springframework.data.annotation.Transient
    private User user;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "pacient", "medic", "clinica", "fisaMedicala", "raportProgramare" }, allowSetters = true)
    private Set<Programare> programares = new HashSet<>();

    @Column("user_id")
    private Long userId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Pacient id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCnp() {
        return this.cnp;
    }

    public Pacient cnp(String cnp) {
        this.setCnp(cnp);
        return this;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    public String getTelefon() {
        return this.telefon;
    }

    public Pacient telefon(String telefon) {
        this.setTelefon(telefon);
        return this;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public LocalDate getDataNasterii() {
        return this.dataNasterii;
    }

    public Pacient dataNasterii(LocalDate dataNasterii) {
        this.setDataNasterii(dataNasterii);
        return this;
    }

    public void setDataNasterii(LocalDate dataNasterii) {
        this.dataNasterii = dataNasterii;
    }

    public String getAdresa() {
        return this.adresa;
    }

    public Pacient adresa(String adresa) {
        this.setAdresa(adresa);
        return this;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public Pacient user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Programare> getProgramares() {
        return this.programares;
    }

    public void setProgramares(Set<Programare> programares) {
        if (this.programares != null) {
            this.programares.forEach(i -> i.setPacient(null));
        }
        if (programares != null) {
            programares.forEach(i -> i.setPacient(this));
        }
        this.programares = programares;
    }

    public Pacient programares(Set<Programare> programares) {
        this.setProgramares(programares);
        return this;
    }

    public Pacient addProgramare(Programare programare) {
        this.programares.add(programare);
        programare.setPacient(this);
        return this;
    }

    public Pacient removeProgramare(Programare programare) {
        this.programares.remove(programare);
        programare.setPacient(null);
        return this;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pacient)) {
            return false;
        }
        return getId() != null && getId().equals(((Pacient) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pacient{" +
            "id=" + getId() +
            ", cnp='" + getCnp() + "'" +
            ", telefon='" + getTelefon() + "'" +
            ", dataNasterii='" + getDataNasterii() + "'" +
            ", adresa='" + getAdresa() + "'" +
            "}";
    }
}
