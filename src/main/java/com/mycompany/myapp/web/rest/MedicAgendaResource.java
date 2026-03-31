package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Program;
import com.mycompany.myapp.repository.ProgramRepository;
import com.mycompany.myapp.repository.ProgramareRepository;
import com.mycompany.myapp.service.dto.ProgramareDTO;
import com.mycompany.myapp.service.mapper.ProgramareMapper;
import java.time.Instant;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
public class MedicAgendaResource {

    private final ProgramRepository programRepository;
    private final ProgramareRepository programareRepository;
    private final ProgramareMapper programareMapper;

    public MedicAgendaResource(
        ProgramRepository programRepository,
        ProgramareRepository programareRepository,
        ProgramareMapper programareMapper
    ) {
        this.programRepository = programRepository;
        this.programareRepository = programareRepository;
        this.programareMapper = programareMapper;
    }

    @GetMapping(value = "/medici/{medicId}/locatii/{locatieId}/program", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Program> getProgramForMedicAndLocatie(@PathVariable Long medicId, @PathVariable Long locatieId) {
        return programRepository.findByMedicAndLocatie(medicId, locatieId);
    }

    @GetMapping(value = "/medici/{medicId}/locatii/{locatieId}/programari", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<ProgramareDTO> getAppointmentsForMedicOnDate(
        @PathVariable Long medicId,
        @PathVariable Long locatieId,
        @RequestParam("from") Instant from,
        @RequestParam("to") Instant to
    ) {
        return programareRepository
            .findByMedicIdAndLocatieIdAndDataProgramareBetween(medicId, locatieId, from, to)
            .map(programareMapper::toDto);
    }
}
