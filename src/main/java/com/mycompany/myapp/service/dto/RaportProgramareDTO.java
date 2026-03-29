package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.RaportProgramare} entity.
 */
@Schema(description = "Raport legat de durata și desfășurarea consultației.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RaportProgramareDTO implements Serializable {

    private Long id;

    private ZonedDateTime oraProgramata;

    private ZonedDateTime oraInceputConsultatie;

    private Integer durataConsultatie;

    private ProgramareDTO programare;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getOraProgramata() {
        return oraProgramata;
    }

    public void setOraProgramata(ZonedDateTime oraProgramata) {
        this.oraProgramata = oraProgramata;
    }

    public ZonedDateTime getOraInceputConsultatie() {
        return oraInceputConsultatie;
    }

    public void setOraInceputConsultatie(ZonedDateTime oraInceputConsultatie) {
        this.oraInceputConsultatie = oraInceputConsultatie;
    }

    public Integer getDurataConsultatie() {
        return durataConsultatie;
    }

    public void setDurataConsultatie(Integer durataConsultatie) {
        this.durataConsultatie = durataConsultatie;
    }

    public ProgramareDTO getProgramare() {
        return programare;
    }

    public void setProgramare(ProgramareDTO programare) {
        this.programare = programare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RaportProgramareDTO)) {
            return false;
        }

        RaportProgramareDTO raportProgramareDTO = (RaportProgramareDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, raportProgramareDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RaportProgramareDTO{" +
            "id=" + getId() +
            ", oraProgramata='" + getOraProgramata() + "'" +
            ", oraInceputConsultatie='" + getOraInceputConsultatie() + "'" +
            ", durataConsultatie=" + getDurataConsultatie() +
            ", programare=" + getProgramare() +
            "}";
    }
}
