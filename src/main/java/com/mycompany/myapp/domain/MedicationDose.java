package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entitate pentru urmărirea dozelor de medicamente și a conformității.
 */
@Table("medication_dose")
public class MedicationDose implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("fisa_medicala_id")
    private Long fisaMedicalaId;

    @Column("medicament")
    private String medicament;

    @Column("ora_planificata")
    private ZonedDateTime oraPlanificata;

    @Column("ora_confirmata")
    private ZonedDateTime oraConfirmata;

    @Column("status")
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFisaMedicalaId() {
        return fisaMedicalaId;
    }

    public void setFisaMedicalaId(Long fisaMedicalaId) {
        this.fisaMedicalaId = fisaMedicalaId;
    }

    public String getMedicament() {
        return medicament;
    }

    public void setMedicament(String medicament) {
        this.medicament = medicament;
    }

    public ZonedDateTime getOraPlanificata() {
        return oraPlanificata;
    }

    public void setOraPlanificata(ZonedDateTime oraPlanificata) {
        this.oraPlanificata = oraPlanificata;
    }

    public ZonedDateTime getOraConfirmata() {
        return oraConfirmata;
    }

    public void setOraConfirmata(ZonedDateTime oraConfirmata) {
        this.oraConfirmata = oraConfirmata;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
