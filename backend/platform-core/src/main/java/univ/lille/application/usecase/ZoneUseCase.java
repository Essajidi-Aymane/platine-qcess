package univ.lille.application.usecase;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import univ.lille.application.usecase.mapper.ZoneMapper;
import univ.lille.domain.exception.ZoneNotFoundException;
import univ.lille.domain.model.Zone;
import univ.lille.domain.port.in.ZoneManagementPort;
import univ.lille.domain.port.out.ZoneEventPublisher;
import univ.lille.domain.port.out.ZoneRepository;
import univ.lille.dto.zone.CreateZoneRequest;
import univ.lille.dto.zone.UpdateZoneRequest;
import univ.lille.dto.zone.ZoneDTO;
import univ.lille.enums.ZoneStatus;
import univ.lille.events.ZoneCreatedEvent;

import java.time.LocalDateTime;
import java.util.List;

import static univ.lille.application.usecase.mapper.ZoneMapper.toDTO;

@Service
@RequiredArgsConstructor
@Transactional
public class ZoneUseCase implements ZoneManagementPort {

    private final ZoneRepository zoneRepository ;
    private final ZoneEventPublisher eventPublisher ;


    /**
     * @param request
     * @return
     */
    @Override
    public ZoneDTO createZone(CreateZoneRequest request, Long orgId) {
        Zone zone = Zone.builder()
                .orgId(orgId)
                .name(request.getName())
                .createdAt(LocalDateTime.now())
                .description(request.getDescription())
                .status(ZoneStatus.ACTIVE)
                .build();
        Zone zoneSaved = zoneRepository.save(zone);

        ZoneCreatedEvent event = new ZoneCreatedEvent(
          zoneSaved.getId(),
                zoneSaved.getOrgId(),
                zoneSaved.getName()
        );
        eventPublisher.publishZoneCreated(event);
        return toDTO(zoneSaved);

    }


    /**
     * @param orgId
     * @param zoneId
     * @return
     */
    @Override
    public ZoneDTO getZone(Long orgId, Long zoneId) {
        Zone zone = zoneRepository.findByIdAndOrganizationId(zoneId, orgId)
                .orElseThrow(() -> new ZoneNotFoundException("Zone not found"));
        return toDTO(zone);
    }

    /**
     * @param zoneId
     * @param orgId
     */
    @Override
    @Transactional
    public void deleteZone(Long zoneId, Long orgId) {
        Zone zone = zoneRepository.findByIdAndOrganizationId(zoneId,orgId).orElseThrow(()->
                new ZoneNotFoundException("Zone not found") );
        if (zone.getStatus() == ZoneStatus.INACTIVE) {
            return;
        }
        zone.setStatus(ZoneStatus.INACTIVE);

        zone.getAllowedRoles().clear();

        zoneRepository.save(zone);
    }

    /**
     * @param zoneId
     * @param request
     * @param orgId
     * @return
     */
    @Override
    public ZoneDTO updateZone(Long zoneId, UpdateZoneRequest request, Long orgId) {
       Zone zone = zoneRepository.findByIdAndOrganizationId(zoneId,orgId).orElseThrow(()->
               new ZoneNotFoundException("Zone not found"));
        if (request.getName() != null && !request.getName().isBlank()) {
            zone.setName(request.getName());
        }
        if (request.getDescription() != null) {
            zone.setDescription(request.getDescription());
        }
        Zone zoneSaved = zoneRepository.save(zone);
        return toDTO(zoneSaved);
    }

    /**
     * @param orgId
     * @return
     */
    @Override
    public List<ZoneDTO> getZonesForOrg(Long orgId) {
      List<Zone> zones =   zoneRepository.findByOrganizationIdAndStatus(orgId,ZoneStatus.ACTIVE);

        return zones.stream()
                .map(ZoneMapper::toDTO)
                .toList();
    }


}

