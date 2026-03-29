package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Program} entity.
 */
@Schema(description = "Programul unui medic într-o locație.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProgramDTO implements Serializable {

    private Long id;

    private String ziuaSaptamanii;

    private ZonedDateTime oraStart;

    private ZonedDateTime oraFinal;

    private MedicDTO medic;

    private LocatieDTO locatie;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getZiuaSaptamanii() {
        return ziuaSaptamanii;
    }

    public void setZiuaSaptamanii(String ziuaSaptamanii) {
        this.ziuaSaptamanii = ziuaSaptamanii;
    }

    public ZonedDateTime getOraStart() {
        return oraStart;
    }

    public void setOraStart(ZonedDateTime oraStart) {
        this.oraStart = oraStart;
    }

    public ZonedDateTime getOraFinal() {
        return oraFinal;
    }

    public void setOraFinal(ZonedDateTime oraFinal) {
        this.oraFinal = oraFinal;
    }

    public MedicDTO getMedic() {
        return medic;
    }

    public void setMedic(MedicDTO medic) {
        this.medic = medic;
    }

    public LocatieDTO getLocatie() {
        return locatie;
    }

    public void setLocatie(LocatieDTO locatie) {
        this.locatie = locatie;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProgramDTO)) {
            return false;
        }

        ProgramDTO programDTO = (ProgramDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, programDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProgramDTO{" +
            "id=" + getId() +
            ", ziuaSaptamanii='" + getZiuaSaptamanii() + "'" +
            ", oraStart='" + getOraStart() + "'" +
            ", oraFinal='" + getOraFinal() + "'" +
            ", medic=" + getMedic() +
            ", locatie=" + getLocatie() +
            "}";
    }
}
