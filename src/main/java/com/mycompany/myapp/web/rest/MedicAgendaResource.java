package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Program;
import com.mycompany.myapp.domain.Programare;
import com.mycompany.myapp.repository.ProgramRepository;
import com.mycompany.myapp.repository.ProgramareRepository;
import java.time.Instant;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
public class MedicAgendaResource {

    private final ProgramRepository programRepository;
    private final ProgramareRepository programareRepository;

    public MedicAgendaResource(ProgramRepository programRepository, ProgramareRepository programareRepository) {
        this.programRepository = programRepository;
        this.programareRepository = programareRepository;
    }

    @GetMapping(value = "/medici/{medicId}/locatii/{locatieId}/program", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Program> getProgramForMedicAndLocatie(@PathVariable Long medicId, @PathVariable Long locatieId) {
        return programRepository.findByMedicAndLocatie(medicId, locatieId);
    }

    @GetMapping(value = "/medici/{medicId}/locatii/{locatieId}/programari", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Programare> getAppointmentsForMedicOnDate(
        @PathVariable Long medicId,
        @PathVariable Long locatieId,
        @RequestParam("from") Instant from,
        @RequestParam("to") Instant to
    ) {
        return programareRepository.findByMedicIdAndLocatieIdAndDataProgramareBetween(medicId, locatieId, from, to);
    }
}
