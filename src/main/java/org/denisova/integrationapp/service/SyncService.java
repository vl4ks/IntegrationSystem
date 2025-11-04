package org.denisova.integrationapp.service;

import jakarta.transaction.Transactional;
import org.denisova.integrationapp.client.CmsClient;
import org.denisova.integrationapp.client.dto.CmsSpareDto;
import org.denisova.integrationapp.config.AppProperties;
import org.denisova.integrationapp.domain.SyncRun;
import org.denisova.integrationapp.repo.SyncRunRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class SyncService {
    private final CmsClient cmsClient;
    private final UpsertService upsertService;
    private final SyncRunRepository syncRunRepo;
    private final AppProperties props;

    public SyncService(CmsClient cmsClient, UpsertService upsertService,
                       SyncRunRepository syncRunRepo, AppProperties props) {
        this.cmsClient = cmsClient;
        this.upsertService = upsertService;
        this.syncRunRepo = syncRunRepo;
        this.props = props;
    }

    @Transactional
    public SyncRun syncAll() {
        var run = new SyncRun();
        run.setStartedAt(OffsetDateTime.now());
        run.setStatus("STARTED");
        run = syncRunRepo.save(run);

        var counters = new UpsertService.Counters();
        int total = 0;
        OffsetDateTime started = run.getStartedAt();

        try {
            int page = 0;
            int size = props.getCms().getPageSize();
            while (true) {
                List<CmsSpareDto> content = cmsClient.getSparesPage(page, size);
                if (content == null || content.isEmpty()) break;

                total += content.size();
                for (CmsSpareDto dto : content) {
                    upsertService.upsertFromCms(dto, started, counters);
                }
                page++;
            }

            int deact = upsertService.deactivateMissing(started);

            run.setTotalFromCms(total);
            run.setInserted(counters.inserted);
            run.setUpdated(counters.updated);
            run.setDeactivated(deact);
            run.setStatus("OK");
            run.setFinishedAt(OffsetDateTime.now());
            return syncRunRepo.save(run);
        } catch (Exception ex) {
            run.setStatus("ERROR");
            run.setMessage(ex.getMessage());
            run.setFinishedAt(OffsetDateTime.now());
            return syncRunRepo.save(run);
        }
    }
}
