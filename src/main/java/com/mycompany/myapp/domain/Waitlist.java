package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entitate pentru lista de așteptare inteligentă.
 */
@Table("waitlist")
public class Waitlist implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("pacient_id")
    private Long pacientId;

    @Column("medic_id")
    private Long medicId;

    @Column("clinica_id")
    private Long clinicaId;

    @Column("locatie_id")
    private Long locatieId;

    @Column("data_preferata")
    private ZonedDateTime dataPreferata;

    @Column("created_at")
    private ZonedDateTime createdAt;

    @Column("status")
    private String status;

    @Column("notified_slot_time")
    private ZonedDateTime notifiedSlotTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPacientId() {
        return pacientId;
    }

    public void setPacientId(Long pacientId) {
        this.pacientId = pacientId;
    }

    public Long getMedicId() {
        return medicId;
    }

    public void setMedicId(Long medicId) {
        this.medicId = medicId;
    }

    public Long getClinicaId() {
        return clinicaId;
    }

    public void setClinicaId(Long clinicaId) {
        this.clinicaId = clinicaId;
    }

    public Long getLocatieId() {
        return locatieId;
    }

    public void setLocatieId(Long locatieId) {
        this.locatieId = locatieId;
    }

    public ZonedDateTime getDataPreferata() {
        return dataPreferata;
    }

    public void setDataPreferata(ZonedDateTime dataPreferata) {
        this.dataPreferata = dataPreferata;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ZonedDateTime getNotifiedSlotTime() {
        return notifiedSlotTime;
    }

    public void setNotifiedSlotTime(ZonedDateTime notifiedSlotTime) {
        this.notifiedSlotTime = notifiedSlotTime;
    }
}
