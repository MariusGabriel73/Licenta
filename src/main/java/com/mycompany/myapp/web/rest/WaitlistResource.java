package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Waitlist;
import com.mycompany.myapp.service.WaitlistService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for managing Waitlist.
 */
@RestController
@RequestMapping("/api/waitlist")
public class WaitlistResource {

    private static final Logger LOG = LoggerFactory.getLogger(WaitlistResource.class);

    private static final String ENTITY_NAME = "waitlist";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WaitlistService waitlistService;

    public WaitlistResource(WaitlistService waitlistService) {
        this.waitlistService = waitlistService;
    }

    /**
     * {@code POST  /waitlist} : Create a new waitlist entry.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Waitlist>> addToWaitlist(@RequestBody Waitlist waitlist) throws URISyntaxException {
        LOG.debug("REST request to add to Waitlist : {}", waitlist);
        if (waitlist.getId() != null) {
            throw new BadRequestAlertException("A new waitlist entry cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return waitlistService
            .addToWaitlist(waitlist)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/waitlist/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code GET  /waitlist/test-trigger} : Manually trigger waitlist check (for debugging).
     * Example: GET /api/waitlist/test-trigger?medicId=1&date=2026-04-30T08:00:00Z
     */
    @GetMapping("/test-trigger")
    public Mono<List<Waitlist>> testTrigger(@RequestParam Long medicId, @RequestParam String date) {
        LOG.info("[TEST-TRIGGER] Manual waitlist check for Medic {} on date {}", medicId, date);
        java.time.ZonedDateTime slotDate = java.time.ZonedDateTime.parse(date);
        return waitlistService.checkWaitlistOnCancellation(medicId, slotDate).collectList();
    }

    /**
     * {@code POST  /waitlist/:id/claim} : Claim a waitlist spot.
     */
    @PostMapping("/{id}/claim")
    public Mono<com.mycompany.myapp.domain.Programare> claimWaitlistSpot(@PathVariable("id") Long id) {
        LOG.debug("REST request to claim Waitlist spot : {}", id);
        return waitlistService.claimWaitlistSpot(id);
    }

    /**
     * {@code GET  /waitlist/pacient/:id} : Get all waitlist entries for a patient.
     */
    @GetMapping("/pacient/{id}")
    public Mono<List<Waitlist>> getMyWaitlist(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Waitlist for pacient : {}", id);
        return waitlistService.findMyWaitlistEntries(id).collectList();
    }
}
